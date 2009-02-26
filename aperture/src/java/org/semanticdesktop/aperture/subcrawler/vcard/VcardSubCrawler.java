/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.vcard.GroupRegistry;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;
import net.fortuna.ical4j.vcard.ParameterFactoryRegistry;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactoryRegistry;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.VCardBuilder;
import net.fortuna.ical4j.vcard.VCardOutputter;
import net.fortuna.ical4j.vcard.Property.Id;
import net.fortuna.ical4j.vcard.parameter.Encoding;
import net.fortuna.ical4j.vcard.parameter.Type;
import net.fortuna.ical4j.vcard.property.Address;
import net.fortuna.ical4j.vcard.property.BDay;
import net.fortuna.ical4j.vcard.property.Email;
import net.fortuna.ical4j.vcard.property.Geo;
import net.fortuna.ical4j.vcard.property.Logo;
import net.fortuna.ical4j.vcard.property.N;
import net.fortuna.ical4j.vcard.property.Nickname;
import net.fortuna.ical4j.vcard.property.Org;
import net.fortuna.ical4j.vcard.property.Photo;
import net.fortuna.ical4j.vcard.property.Revision;
import net.fortuna.ical4j.vcard.property.Telephone;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.subcrawler.base.AbstractSubCrawler;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.util.StringUtil;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Extractor Implementation working with VCard documents.
 * <p>
 * Known issues:
 * <ul>
 * <li>The preferred contact media aren't marked as such in the output, because the NCO doesn't cover this
 * <li>Theoretically the email addresses can have the TYPE=x400, this is not supported, all email addresses
 * are treated as internet addresses.
 * <li>The VCARD specification doesn't distinguish between private and business email addresses, so this
 * extractor doesn't do it either.
 * <li>The REV property defined in RFC 2426 sec. 3.6.4 doesn't have any direct equivalent in NCO, therefore
 * nie:contentLastModified is used.</li>
 * <li>NCO doesn't allow to preserve the order of the additional names, so this crawler discards that order.
 * Every additional name receives a separate nco:nameAdditional triple and the triples themselves are
 * unordered by definition.</li>
 * <li>The above consideration also applies to nicknames. Nicknames can be ordered in the vcard but they are
 * left unordered in the rdf data extracted from it.</li>
 * <li>The ORG type in the vcard can specify an entity within an organization at an arbitrary level of nesting.
 * E.g a team within a project, within a department, within a division, within a company within a corporation.
 * NCO only allows for a single nco:department property of the affiliation, therefore supporting only a single
 * level of nesting. If more than one organizational unit is specified in the ORG element, the information about
 * which unit is nested within which is lost, all units are recorded in the rdf at the same level with separate
 * nco:department triples attached to the affiliation resource.
 * </li>
 * <li>Other elements of the vcard specification that aren't supported by NCO: (they are supported by JPIM)
 * <ul>
 * <li>ACCESS</li>
 * <li>CATEGORY</li>
 * <li>LABEL</li>
 * <li>extended elements</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * <b>URIs for VCARDS</b><br/><br/> This crawler uses following conventions to generate URIS:
 * <ol>
 * <li>If the UID parameter is present, it is concatenated to the stream id (preceeded by a hash)</li>
 * <li>If it's not, then the contact is serialized to a string and a hash of that string is contactenated. to
 * the stream id.</li>
 * </ol>
 * This guarantees that an unmodified contact will be detected and reported as unmodified. (Which is not the
 * case if we used other properties, or consecutive numbers marking the position of the contact in a file
 * (which change if new contacts are added or removed)).
 */
public class VcardSubCrawler extends AbstractSubCrawler {

    private static final String OBJECT_HASH_KEY = "contactHash";
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * @see SubCrawler#subCrawl(URI, InputStream, SubCrawlerHandler, DataSource, AccessData, Charset, String, RDFContainer)
     */
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata) throws SubCrawlerException {

        if (handler == null) {
            throw new SubCrawlerException("The SubCrawlerHandler cannot be null");
        }
        else if (parentMetadata == null) {
            throw new SubCrawlerException("The parentMetadata cannot be null");
        }

        try {
            Reader reader = new InputStreamReader(stream);
            GroupRegistry groupRegistry = new GroupRegistry();
            PropertyFactoryRegistry propReg = new PropertyFactoryRegistry();
            ParameterFactoryRegistry parReg = new ParameterFactoryRegistry();
            
            addTypeParamsToRegistry(parReg);

            VCardBuilder builder = new VCardBuilder(reader,groupRegistry,propReg,parReg);
            List<VCard> cards = builder.buildAll();
            VCardOutputter outputter = new VCardOutputter(false);
            if (cards.size() == 1) {
                processContact(cards.get(0), parentMetadata.getModel(), parentMetadata.getDescribedUri());
            }
            else {
                processAddressBook(cards, parentMetadata, handler, outputter, accessData, dataSource);
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
        }
    }

    private void addTypeParamsToRegistry(ParameterFactoryRegistry parReg) {
        for (final String name : new String[] {"HOME","WORK","MSG","PREF","VOICE","FAX","CELL",
                "VIDEO","PAGER","BBS","MODEM","CAR","ISDN","PCS","INTERNET","X400","DOM",
                "INTL","POSTAL","PARCEL"}) {
            parReg.register(name, new ParameterFactory<Parameter>() {
                public Parameter createParameter(String value) {
                    return new Type(name);
                }
            });
            parReg.register(name.toLowerCase(), new ParameterFactory<Parameter>() {
                public Parameter createParameter(String value) {
                    return new Type(name);
                }
            });
        }
    }

    /**
     * @see SubCrawler#stopSubCrawler()
     */
    public void stopSubCrawler() {
    // haha, not supported (yet)
    }
    
    @Override
    public String getUriPrefix() {
        return VcardSubCrawlerFactory.VCARD_URI_PREFIX;
    }

    private void processAddressBook(List<VCard> contacts, RDFContainer parentMetadata,
            SubCrawlerHandler handler, VCardOutputter out, AccessData accessData, DataSource source) {
        parentMetadata.add(RDF.type, NCO.ContactList);
        for (VCard contact : contacts) {
            try {
                String contactHash = getContactHash(contact, out);
                URI contactUri = generateURIForContact(contact, parentMetadata, contactHash);
                RDFContainerFactory factory = handler.getRDFContainerFactory(contactUri.toString());
                RDFContainer container = factory.getRDFContainer(contactUri);
                processContact(contact, container.getModel(), contactUri);
                parentMetadata.add(NCO.containsContact, contactUri);
                container.add(RDF.type, NCO.ContactListDataObject);
                passMetadataToHandler(container, handler, contactHash, accessData, source);
            } catch (Exception e) {
                logger.warn("Failed to process vcard",e);
            }
        }
    }

    private void passMetadataToHandler(RDFContainer container, SubCrawlerHandler handler, String objectHash,
            AccessData accessData, DataSource source) {
        URI uri = container.getDescribedUri();
        DataObject object = new DataObjectBase(uri, source, container);
        if (accessData == null) {
            handler.objectNew(object);
        } else if (!accessData.isKnownId(uri.toString())) {
            accessData.put(uri.toString(), OBJECT_HASH_KEY, objectHash);
            handler.objectNew(object);
        } else {
            String oldHash = accessData.get(uri.toString(), OBJECT_HASH_KEY);
            if (oldHash == null || !oldHash.equals(objectHash)) {
                accessData.put(uri.toString(), OBJECT_HASH_KEY, objectHash);
                handler.objectChanged(object);
            } else {
                handler.objectNotModified(uri.toString());
            }
        }
    }

    private void processContact(VCard contact, Model model, Resource contactResource) {
        model.addStatement(contactResource, RDF.type, NCO.Contact);
        processPersonalIdentity(contact, model, contactResource);
        Resource affiliationResource = processOrganizationIdentity(contact, model, contactResource);
        processCommonProperties(contact, model, contactResource, affiliationResource);

    }

    private void processPersonalIdentity(VCard vc, Model model,
            Resource contactResource) {
        
        // this property is present in all contacts, regardless of whether they are PersonContacts
        // or OrganizationContacts, the presence of this property cannot tell us anything interesting
        addStringProperty(model, contactResource, NCO.fullname, getPropertyValue(vc,Id.FN));

        // but the presence of the properties below is a different thing.
        // first we may check if any one of them is present and insert an appropriate type
        N name = (N)vc.getProperty(Id.N);
        if (name != null && (!empty(name.getAdditionalNames()) || vc.getProperty(Id.BDAY) != null
                || name.getGivenName() != null || name.getFamilyName() != null
                || !empty(name.getPrefixes())
                || !empty(name.getSuffixes()))) {
            model.addStatement(contactResource, RDF.type, NCO.PersonContact);

        }
        
        if (name == null) {
            return;
        }

        for (int i = 0; i < length(name.getAdditionalNames()); i++) {
            String addName = name.getAdditionalNames()[i];
            model.addStatement(contactResource, NCO.nameAdditional, addName);
        }
        BDay bday = (BDay)vc.getProperty(Id.BDAY);
        if (bday != null) {
            addDateProperty(model, contactResource, NCO.birthDate, bday.getDate());
        }
        addStringProperty(model, contactResource, NCO.nameGiven, name.getGivenName());
        addStringProperty(model, contactResource, NCO.nameFamily, name.getFamilyName());
        Nickname nickname = (Nickname)vc.getProperty(Id.NICKNAME);
        if (nickname != null) {
            for (int i = 0; i < length(nickname.getNames()); i++) {
                model.addStatement(contactResource, NCO.nickname, nickname.getNames()[i]);
            }
        }
        Photo photo = (Photo)vc.getProperty(Id.PHOTO);
        if (photo != null) {
            processImage(model, contactResource, NCO.photo,
                getParameterValue(photo, net.fortuna.ical4j.vcard.Parameter.Id.TYPE));
        }
        for (int i = 0; i < length(name.getPrefixes()); i++) {
        	String prefix = name.getPrefixes()[i];
            addStringProperty(model, contactResource, NCO.nameHonorificPrefix, prefix);
        }
        for (int i = 0; i < length(name.getSuffixes()); i++) {
        	String suffix = name.getSuffixes()[i];
            addStringProperty(model, contactResource, NCO.nameHonorificSuffix, suffix);
        }

    }

    private Resource processOrganizationIdentity(VCard organizationalIdentity, Model model,
            Resource contactResource) {

        // first some sanity checking
        if (organizationalIdentity == null) {
            return null;
        }

        // The question is, how to determine if the OrganizatonalIdentity is non-empty..
        if (organizationalIdentity.getProperty(Id.AGENT) == null && 
            organizationalIdentity.getProperty(Id.ORG) == null && 
            organizationalIdentity.getProperty(Id.ROLE) == null && 
            organizationalIdentity.getProperty(Id.TITLE) == null) {
            return null;
        }
        // let's hope this sanity-check is enough, that's the price you pay for a higher-level API

        // the first thing we know, that if a Contact is affiliated with an organization, we may infer
        // that it is a PersonContact, this triple needs to be added manually
        model.addStatement(contactResource, RDF.type, NCO.PersonContact);

        Resource affiliationResource = UriUtil.generateRandomResource(model);
        model.addStatement(contactResource, NCO.hasAffiliation, affiliationResource);
        model.addStatement(affiliationResource, RDF.type, NCO.Affiliation);
        addStringProperty(model, affiliationResource, NCO.role, getPropertyValue(organizationalIdentity, Id.ROLE));
        addStringProperty(model, affiliationResource, NCO.title, getPropertyValue(organizationalIdentity, Id.TITLE));

        // so, the AGENT and ORGANIZATION both require an organizationResource, but we don't need to
        // create it if there are none (is it possible in VCARD at all?)
        if (organizationalIdentity.getProperty(Id.AGENT) == null && 
            organizationalIdentity.getProperty(Id.ORG) == null) {
            return affiliationResource;
        }
        // now we know we have to create an organization resource

        Resource organizationResource = UriUtil.generateRandomResource(model);
        model.addStatement(organizationResource, RDF.type, NCO.OrganizationContact);
        model.addStatement(affiliationResource, NCO.org, organizationResource);

        
        Logo logo = (Logo)organizationalIdentity.getProperty(Id.LOGO);
        if (logo != null) {
            processImage(model, organizationResource, NCO.logo, 
                getParameterValue(logo, net.fortuna.ical4j.vcard.Parameter.Id.TYPE));
        }
        
        Org org = (Org) organizationalIdentity.getProperty(Id.ORG);
        if (org != null) {
            String[] vals = org.getValues();
            if (length(vals) >= 1) {
                addStringProperty(model, organizationResource, NCO.fullname, vals[0]);
            }
            for (int i = 1; i < length(vals); i++) {
                // note that the departments are attached to the affiliationResource
                addStringProperty(model, affiliationResource, NCO.department, vals[i]);
            }
        }

        return affiliationResource;
    }

    private void processCommonProperties(VCard contact, Model model, Resource contactResource,
            Resource affiliationResource) {
        // so, first the addresses
        List<Property> adrs = contact.getProperties(Id.ADR);
        
        for (Property address : adrs) {
            // let's hope this simple comparison will work as desired...
            String type = getParameterValue(address, net.fortuna.ical4j.vcard.Parameter.Id.TYPE);
            if (type != null && type.contains("pref")) {
                processAddress(model, (Address)address, contactResource, affiliationResource, true);
            }
            else {
                processAddress(model, (Address)address, contactResource, affiliationResource, false);
            }
        }

        // then the complex properties
        processCommunications(model, contact, contactResource, affiliationResource);
        processGeographicalInformation(model, contactResource, NCO.hasLocation, contact);
        Property key = contact.getProperty(Id.KEY);
        if (key != null) {
            processPublicKey(model, contactResource, NCO.key,  
                getParameterValue(key, net.fortuna.ical4j.vcard.Parameter.Id.TYPE));
        }
        Property sound = contact.getProperty(Id.SOUND);
        if (sound != null) {
            processSound(model, contactResource, NCO.sound, 
                getParameterValue(sound, net.fortuna.ical4j.vcard.Parameter.Id.TYPE));
        }

        // and then the simple properties
        addStringProperty(model, contactResource, NCO.contactUID, getPropertyValue(contact, Id.UID));
        addUriProperty(model, contactResource, NCO.url, getPropertyValue(contact, Id.URL));
        addStringProperty(model, contactResource, NCO.note, getPropertyValue(contact, Id.NOTE));
        
        Revision rev = (Revision)contact.getProperty(Id.REV);
        if (rev != null) {
            addDateTimeProperty(model, contactResource, NIE.contentLastModified, rev.getDate());
        }
    }

    private void processAddress(Model model, Address address, Resource contactResource,
            Resource affiliationResource, boolean preferred) {
        if (address != null) {
            Resource addressResource = UriUtil.generateRandomResource(model);
            model.addStatement(addressResource, RDF.type, NCO.PostalAddress);
            addStringProperty(model, addressResource, NCO.locality, address.getLocality());
            addStringProperty(model, addressResource, NCO.country, address.getCountry());
            addStringProperty(model, addressResource, NCO.postalcode, address.getPostcode());
            addStringProperty(model, addressResource, NCO.pobox, address.getPoBox());
            addStringProperty(model, addressResource, NCO.region, address.getRegion());
            addStringProperty(model, addressResource, NCO.streetAddress, address.getStreet());
            addStringProperty(model, addressResource, NCO.extendedAddress, address.getExtended());

            /*
             * First a set of flags that occur only on addresses of organizations. The problem is: should we
             * attache them to the contact (meaning that the contact itself is an OrganizationContact, or to
             * the Affiliation - which means a work address or to the personContact.
             * 
             * It is impossible to tell. So let's use some common sense.
             * 
             * This heuristic is reflects my own view and is not an official policy of the Aperture Project
             * members :)
             */

            String type = getParameterValue(address, net.fortuna.ical4j.vcard.Parameter.Id.TYPE);
            if (type == null) {
                return; 
            }
            type = type.toLowerCase();
            // the 'postal' is neutral, it may occur everywhere, we don't do anything with it

            // These flags occur in contacts for organizations, hardly anyone includes all of these on
            // a personal business card, if these flags are tru, this means that this contact is a
            // contact to an organization, so we attach the address to the contact itself
            if (type.contains("dom")) {
                model.addStatement(addressResource, RDF.type, NCO.DomesticDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }
            if (type.contains("intl")) {
                model.addStatement(addressResource, RDF.type, NCO.InternationalDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }
            if (type.contains("parcel")) {
                model.addStatement(addressResource, RDF.type, NCO.ParcelDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // this means that it is a work address of a person, we can attach it to the affiliation
            if (type.contains("work")) {
                if (affiliationResource != null) {
                    // this means that an affiliation has already been created while processing
                    // the organizational identity
                    addContactMediumProperty(model, affiliationResource, NCO.hasPostalAddress,
                        addressResource, preferred);
                }
                else {
                    // this means that no affiliation has been created, we need to create one now
                    // an anonymous affiliation with an anonymous organization
                    affiliationResource = UriUtil.generateRandomResource(model);
                    model.addStatement(affiliationResource, RDF.type, NCO.Affiliation);
                    model.addStatement(contactResource, NCO.hasAffiliation, affiliationResource);
                    addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                        preferred);
                }
            }

            // this means that this is a home address, no additional types and attach it to the
            // contact
            if (type.contains("home")) {
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // and there is always the case that an address has no TYPE whatsoever
            if (type == null || type.trim().length() == 0) {
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // nco doesn't support uid's and labels of addresses
        }
    }

    private void processCommunications(Model model, VCard communications, Resource contactResource,
            Resource affiliationResource) {
        List<Property> mails = communications.getProperties(Id.EMAIL);
        
        //EmailAddress preferredAddress = communications.getPreferredEmailAddress();
        for (Property address : mails) {
            String type = getParameterValue(address, net.fortuna.ical4j.vcard.Parameter.Id.TYPE);
            
            if (type != null && type.toLowerCase().contains("pref")) {
                processEmailAddress(model, (Email)address, contactResource, affiliationResource, true);
            }
            else {
                processEmailAddress(model, (Email)address, contactResource, affiliationResource, false);
            }
        }
        
        List<Property> phones = communications.getProperties(Id.TEL);
        for (Property number : phones) {
            processPhoneNumber(model, (Telephone)number, contactResource, affiliationResource);
        }
    }

    private void processPhoneNumber(Model model, Telephone number, Resource contactResource,
            Resource affiliationResource) {

        Resource numberResource = UriUtil.generateRandomResource(model);
        model.addStatement(numberResource, RDF.type, NCO.PhoneNumber);
        addStringProperty(model, numberResource, NCO.phoneNumber, number.getValue());
        String type = getParameterValue(number, net.fortuna.ical4j.vcard.Parameter.Id.TYPE);
        
        if (type == null) {
            return;
        }
        type = type.toLowerCase();
        if (type.contains("bbs")) {
            model.addStatement(numberResource, RDF.type, NCO.BbsNumber);
        }
        if (type.contains("car")) {
            model.addStatement(numberResource, RDF.type, NCO.CarPhoneNumber);
        }
        if (type.contains("cell")) {
            model.addStatement(numberResource, RDF.type, NCO.CellPhoneNumber);
        }
        if (type.contains("fax")) {
            model.addStatement(numberResource, RDF.type, NCO.FaxNumber);
        }
        if (type.contains("home")) {
            addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource, 
                type.contains("pref"));
        }
        if (type.contains("isdn")) {
            model.addStatement(numberResource, RDF.type, NCO.IsdnNumber);
        }
        if (type.contains("msg")) {
            model.addStatement(numberResource, RDF.type, NCO.MessagingNumber);
        }
        if (type.contains("modem")) {
            model.addStatement(numberResource, RDF.type, NCO.ModemNumber);
        }
        if (type.contains("pager")) {
            model.addStatement(numberResource, RDF.type, NCO.PagerNumber);
        }
        if (type.contains("pcs")) {
            model.addStatement(numberResource, RDF.type, NCO.PcsNumber);
        }
        if (type.contains("video")) {
            model.addStatement(numberResource, RDF.type, NCO.VideoTelephoneNumber);
        }
        if (type.contains("voice")) {
            model.addStatement(numberResource, RDF.type, NCO.VoicePhoneNumber);
        }
        if (type.contains("work")) {
            if (affiliationResource != null) {
                // this means that an affiliation has already been created while processing
                // the organizational identity
                addContactMediumProperty(model, affiliationResource, NCO.hasPhoneNumber, numberResource, 
                    type.contains("pref"));
            }
            else {
                // this means that no affiliation has been created, we need to create one now
                // an anonymous affiliation with an anonymous organization
                affiliationResource = UriUtil.generateRandomResource(model);
                model.addStatement(affiliationResource, RDF.type, NCO.Affiliation);
                model.addStatement(contactResource, NCO.hasAffiliation, affiliationResource);
                addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource, 
                    type.contains("pref"));
            }
        }

        if (!type.contains("home") && !type.contains("work")) {
            addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource, 
                type.contains("pref"));
        }
    }

    private void processEmailAddress(Model model, Email address, Resource contactResource,
            Resource affiliationResource, boolean preferred) {
        /*
         * This sucks, there is no way to tell if an email is a private email or a business email this sucks
         * ass...
         */
        String type = getParameterValue(address, net.fortuna.ical4j.vcard.Parameter.Id.TYPE);
        if (address != null && (type == null || type.toLowerCase().contains("internet"))) {
            // we don't support other than internet addresses anyway, I have
            // no idea what are these x.400 addresses
            // TODO solve the x.400 address type issue
            Resource addressResource = UriUtil.generateRandomResource(model);
            model.addStatement(addressResource, RDF.type, NCO.EmailAddress);
            addStringProperty(model, addressResource, NCO.emailAddress, address.getValue());
            // due to the fact that the VCARD specs suck ass, we always attach the address
            // to the contact and never to the affiliation
            addContactMediumProperty(model, contactResource, NCO.hasEmailAddress, addressResource, preferred);
        }
    }

    private void processGeographicalInformation(Model model, Resource contactResource, URI property, VCard vc) {
        Geo geo = (Geo)vc.getProperty(Id.GEO);
        if (geo != null) {
            Resource geoResource = UriUtil.generateRandomResource(model);
            model.addStatement(geoResource, RDF.type, GEO.Point);
            model.addStatement(geoResource, GEO.lat, geo.getLatitude().toPlainString());
            model.addStatement(geoResource, GEO.long_, geo.getLongitude().toPlainString());
            model.addStatement(contactResource, property, geoResource);
        }
    }

    private void processImage(Model model, Resource contactResource, URI property, String mimeType) {
        Resource imageResource = UriUtil.generateRandomResource(model);
        model.addStatement(imageResource, RDF.type, NEXIF.Photo);
        model.addStatement(imageResource, RDF.type, NFO.Attachment);
        model.addStatement(contactResource, property, imageResource);
        addStringProperty(model, imageResource, NIE.mimeType, mimeType);
    }

    private void processSound(Model model, Resource contactResource, URI property, String mimeType) {
        Resource soundResource = UriUtil.generateRandomResource(model);
        model.addStatement(soundResource, RDF.type, NFO.Audio);
        model.addStatement(soundResource, RDF.type, NFO.Attachment);
        model.addStatement(contactResource, property, soundResource);
        addStringProperty(model, soundResource, NIE.mimeType, mimeType);
    }

    private void processPublicKey(Model model, Resource contactResource, URI property, String mimeType) {
        Resource keyResource = UriUtil.generateRandomResource(model);
        model.addStatement(keyResource, RDF.type, NIE.InformationElement);
        model.addStatement(keyResource, RDF.type, NFO.Attachment);
        model.addStatement(contactResource, property, keyResource);
        addStringProperty(model, keyResource, NIE.mimeType, mimeType);
    }

    private void addStringProperty(Model model, Resource resource, URI property, String value) {
        if (value != null && value.trim().length() > 0) {
            model.addStatement(resource, property, value);
        }
    }

    private void addDateProperty(Model model, Resource resource, URI property, Date date) {
        if (date != null) {
            String dateString = DateUtil.date2String(date);
            model.addStatement(resource, property, model.createDatatypeLiteral(dateString, XSD._date));
        }
    }

    private void addDateTimeProperty(Model model, Resource resource, URI property, Date date) {
        if (date != null) {
            String dateString = DateUtil.dateTime2String(date);
            model.addStatement(resource, property, model.createDatatypeLiteral(dateString, XSD._dateTime));
        }
    }

    private void addUriProperty(Model model, Resource contactResource, URI property, String uriString) {
        if (uriString != null) {
            URI uri = model.createURI(uriString);
            model.addStatement(contactResource, property, uri);
            model.addStatement(uri, RDF.type, RDFS.Resource);
        }
    }

    private void addContactMediumProperty(Model model, Resource subject, URI property, Resource object,
            boolean preferred) {
        model.addStatement(subject, property, object);
        if (preferred) {
            // TODO decide what to do with the prefered contact media
        }
    }
    
    /**
     * Generates a URI for a Contact.
     * @param contact the contact
     * @param container the container for which the URI should be generated
     * @param marshaller the contact marshaller, may be used if the contact doesn't contain the UID property.
     * @return
     */
    private URI generateURIForContact(VCard contact, RDFContainer container, String contactHash) {
        String contactIdentifier = null;
        Property uid = contact.getProperty(Id.UID);
        if (uid != null) {
            contactIdentifier = uid.getValue();
        } else {
            contactIdentifier = contactHash;
        }
        return createChildUri(container.getDescribedUri(), contactIdentifier);
    }
    
    private String getContactHash(VCard contact, VCardOutputter outputter) throws IOException, ValidationException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        outputter.output(contact, stream);
        return StringUtil.sha1Hash(stream.toByteArray());
    }
    
    private String getPropertyValue(VCard vc, Id fn) {
        Property p = vc.getProperty(fn);
        if (p != null) {
            try {
                return getDecodedPropertyalue(p);
            }
            catch (DecoderException e) {
                // may happen, return the normal value instead
                return p.getValue();
            }
        } else {
            return null;
        }
    }
    
    private String getParameterValue(Property photo, net.fortuna.ical4j.vcard.Parameter.Id type) {
        Parameter param = photo.getParameter(type);
        if (param != null) {
            return param.getValue();
        } else {
            return null;
        }
    }
    
    private boolean empty(Object [] array) {
    	if (array == null || array.length == 0) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private int length(Object[] arr) {
        if (arr == null) {
        	return 0;
        } else {
        	return arr.length;
        }
    }
    
    private String getDecodedPropertyalue(Property prop) throws DecoderException {
        Encoding enc = (Encoding)prop.getParameter(Parameter.Id.ENCODING);
        String val = prop.getValue();
        if (enc != null && enc.getValue().equalsIgnoreCase("QUOTED-PRINTABLE")) {
            
            /*
             * A special Outlook2003 hack.
             */
            if (val.endsWith("=")) {
                val = val.substring(0,val.length() - 1);
            }
            
            QuotedPrintableCodec codec = new QuotedPrintableCodec();
            return codec.decode(val);
        } else {
            return val;
        }
    }
}
