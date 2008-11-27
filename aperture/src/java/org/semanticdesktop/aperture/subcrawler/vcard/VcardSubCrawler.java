/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import net.wimpi.pim.Pim;
import net.wimpi.pim.contact.io.ContactMarshaller;
import net.wimpi.pim.contact.io.ContactUnmarshaller;
import net.wimpi.pim.contact.model.Address;
import net.wimpi.pim.contact.model.Communications;
import net.wimpi.pim.contact.model.Contact;
import net.wimpi.pim.contact.model.EmailAddress;
import net.wimpi.pim.contact.model.GeographicalInformation;
import net.wimpi.pim.contact.model.Image;
import net.wimpi.pim.contact.model.Key;
import net.wimpi.pim.contact.model.Organization;
import net.wimpi.pim.contact.model.OrganizationalIdentity;
import net.wimpi.pim.contact.model.PersonalIdentity;
import net.wimpi.pim.contact.model.PhoneNumber;
import net.wimpi.pim.contact.model.Sound;
import net.wimpi.pim.factory.ContactIOFactory;

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
            ContactIOFactory factory = Pim.getContactIOFactory();
            ContactUnmarshaller unmarshaller = factory.createContactUnmarshaller();
            ContactMarshaller marshaller = factory.createContactMarshaller();
            Contact[] contacts = unmarshaller.unmarshallContacts(stream);
            if (contacts.length == 0) {
                // nothing to be done, the file doesn't contain any contacts
                return;
            }
            else if (contacts.length == 1) {
                processContact(contacts[0], parentMetadata.getModel(), parentMetadata.getDescribedUri());
            }
            else {
                processAddressBook(contacts, parentMetadata, handler, marshaller, accessData, dataSource);
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
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

    private void processAddressBook(Contact[] contacts, RDFContainer parentMetadata,
            SubCrawlerHandler handler, ContactMarshaller marshaller, AccessData accessData, DataSource source) {
        parentMetadata.add(RDF.type, NCO.ContactList);
        for (Contact contact : contacts) {
            String contactHash = getContactHash(contact, marshaller);
            URI contactUri = generateURIForContact(contact, parentMetadata, contactHash);
            RDFContainerFactory factory = handler.getRDFContainerFactory(contactUri.toString());
            RDFContainer container = factory.getRDFContainer(contactUri);
            processContact(contact, container.getModel(), contactUri);
            parentMetadata.add(NCO.containsContact, contactUri);
            container.add(RDF.type, NCO.ContactListDataObject);
            passMetadataToHandler(container, handler, contactHash, accessData, source);
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

    private void processContact(Contact contact, Model model, Resource contactResource) {
        model.addStatement(contactResource, RDF.type, NCO.Contact);
        processPersonalIdentity(contact.getPersonalIdentity(), model, contactResource);
        Resource affiliationResource = processOrganizationIdentity(contact.getOrganizationalIdentity(),
            model, contactResource);
        processCommonProperties(contact, model, contactResource, affiliationResource);

    }

    private void processPersonalIdentity(PersonalIdentity personalIdentity, Model model,
            Resource contactResource) {
        if (personalIdentity == null) {
            return;
        }
        // this property is present in all contacts, regardless of whether they are PersonContacts
        // or OrganizationContacts, the presence of this property cannot tell us anything interesting
        addStringProperty(model, contactResource, NCO.fullname, personalIdentity.getFormattedName());

        // but the presence of the properties below is a different thing.
        // first we may check if any one of them is present and insert an appropriate type
        if (personalIdentity.getAdditionalNameCount() > 0 || personalIdentity.getBirthDate() != null
                || personalIdentity.getFirstname() != null || personalIdentity.getLastname() != null
                || personalIdentity.getNicknameCount() > 0 || personalIdentity.listPrefixes().length > 0
                || personalIdentity.listSuffixes().length > 0) {
            model.addStatement(contactResource, RDF.type, NCO.PersonContact);

        }

        for (int i = 0; i < personalIdentity.getAdditionalNameCount(); i++) {
            model.addStatement(contactResource, NCO.nameAdditional, personalIdentity.getAdditionalName(i));
        }
        addDateProperty(model, contactResource, NCO.birthDate, personalIdentity.getBirthDate());
        addStringProperty(model, contactResource, NCO.nameGiven, personalIdentity.getFirstname());
        for (int i = 0; i < personalIdentity.getAdditionalNameCount(); i++) {
            model.addStatement(contactResource, NCO.nameAdditional, personalIdentity.getAdditionalName(i));
        }
        addStringProperty(model, contactResource, NCO.nameFamily, personalIdentity.getLastname());
        for (int i = 0; i < personalIdentity.getNicknameCount(); i++) {
            model.addStatement(contactResource, NCO.nickname, personalIdentity.getNickname(i));
        }
        processImage(model, contactResource, NCO.photo, personalIdentity.getPhoto());
        for (String prefix : personalIdentity.listPrefixes()) {
            addStringProperty(model, contactResource, NCO.nameHonorificPrefix, prefix);
        }
        for (String prefix : personalIdentity.listSuffixes()) {
            addStringProperty(model, contactResource, NCO.nameHonorificSuffix, prefix);
        }

    }

    private Resource processOrganizationIdentity(OrganizationalIdentity organizationalIdentity, Model model,
            Resource contactResource) {

        // first some sanity checking
        if (organizationalIdentity == null) {
            return null;
        }

        // The question is, how to determine if the OrganizatonalIdentity is non-empty..
        if (organizationalIdentity.getAgent() == null && organizationalIdentity.getOrganization() == null
                && organizationalIdentity.getRole() == null && organizationalIdentity.getTitle() == null) {
            return null;
        }
        // let's hope this sanity-check is enough, that's the price you pay for a higher-level API

        // the first thing we know, that if a Contact is affiliated with an organization, we may infer
        // that it is a PersonContact, this triple needs to be added manually
        model.addStatement(contactResource, RDF.type, NCO.PersonContact);

        Resource affiliationResource = UriUtil.generateRandomResource(model);
        model.addStatement(contactResource, NCO.hasAffiliation, affiliationResource);
        model.addStatement(affiliationResource, RDF.type, NCO.Affiliation);
        addStringProperty(model, affiliationResource, NCO.role, organizationalIdentity.getRole());
        addStringProperty(model, affiliationResource, NCO.title, organizationalIdentity.getTitle());

        // so, the AGENT and ORGANIZATION both require an organizationResource, but we don't need to
        // create it if there are none (is it possible in VCARD at all?)
        if (organizationalIdentity.getAgent() == null && organizationalIdentity.getOrganization() == null) {
            return affiliationResource;
        }
        // now we know we have to create an organization resource

        Resource organizationResource = UriUtil.generateRandomResource(model);
        model.addStatement(organizationResource, RDF.type, NCO.OrganizationContact);
        model.addStatement(affiliationResource, NCO.org, organizationResource);

        Organization organization = organizationalIdentity.getOrganization();
        if (organization != null) {
            addStringProperty(model, organizationResource, NCO.fullname, organization.getName());
            processImage(model, organizationResource, NCO.logo, organization.getLogo());
            for (int i = 0; i < organization.getUnitCount(); i++) {
                // note that the departments are attached to the affiliationResource
                addStringProperty(model, affiliationResource, NCO.department, organization.getUnit(i));
            }
        }

        return affiliationResource;
    }

    private void processCommonProperties(Contact contact, Model model, Resource contactResource,
            Resource affiliationResource) {
        // so, first the addresses
        Address preferredAddress = contact.getPreferredAddress();
        for (Address address : contact.listAddresses()) {
            // let's hope this simple comparison will work as desired...
            if (preferredAddress == address) {
                processAddress(model, address, contactResource, affiliationResource, true);
            }
            else {
                processAddress(model, address, contactResource, affiliationResource, false);
            }
        }

        // then the complex properties
        processCommunications(model, contact.getCommunications(), contactResource, affiliationResource);
        processGeographicalInformation(model, contactResource, NCO.hasLocation, contact
                .getGeographicalInformation());
        processPublicKey(model, contactResource, NCO.key, contact.getPublicKey());
        processSound(model, contactResource, NCO.sound, contact.getSound());

        // and then the simple properties
        addStringProperty(model, contactResource, NCO.contactUID, contact.getUID());
        addUriProperty(model, contactResource, NCO.url, contact.getURL());
        addStringProperty(model, contactResource, NCO.note, contact.getNote());
        addDateTimeProperty(model, contactResource, NIE.contentLastModified, contact.getCurrentRevisionDate());

        // and a list of the getters of Contact that are not supported by NCO
        // contact.getAccessClassification()
        // contact.listCategories();
        // contact.getExtensions();
        // contact.getLastAddedAddress();
        // contact.getPreferredAddress(); - covered by processAddress, no need to cover it here
    }

    private void processAddress(Model model, Address address, Resource contactResource,
            Resource affiliationResource, boolean preferred) {
        if (address != null) {
            Resource addressResource = UriUtil.generateRandomResource(model);
            model.addStatement(addressResource, RDF.type, NCO.PostalAddress);
            addStringProperty(model, addressResource, NCO.locality, address.getCity());
            addStringProperty(model, addressResource, NCO.country, address.getCountry());
            addStringProperty(model, addressResource, NCO.postalcode, address.getPostalCode());
            addStringProperty(model, addressResource, NCO.pobox, address.getPostBox());
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

            // this flag is neutral, it may occur everywhere, we don't do anything with it
            address.isPostal();

            // These flags occur in contacts for organizations, hardly anyone includes all of these on
            // a personal business card, if these flags are tru, this means that this contact is a
            // contact to an organization, so we attach the address to the contact itself
            if (address.isDomestic()) {
                model.addStatement(addressResource, RDF.type, NCO.DomesticDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }
            if (address.isInternational()) {
                model.addStatement(addressResource, RDF.type, NCO.InternationalDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }
            if (address.isParcel()) {
                model.addStatement(addressResource, RDF.type, NCO.ParcelDeliveryAddress);
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // this means that it is a work address of a person, we can attach it to the affiliation
            if (address.isWork()) {
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
            if (address.isHome()) {
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // and there is always the case that an address has no TYPE whatsoever
            if (!address.isDomestic() && !address.isHome() && !address.isInternational()
                    && !address.isParcel() && !address.isPostal() && !address.isWork()) {
                addContactMediumProperty(model, contactResource, NCO.hasPostalAddress, addressResource,
                    preferred);
            }

            // nco doesn't support uid's and labels of addresses
            // address.getUID();
            // address.getLabel()
        }
    }

    private void processCommunications(Model model, Communications communications, Resource contactResource,
            Resource affiliationResource) {
        if (communications != null) {
            EmailAddress preferredAddress = communications.getPreferredEmailAddress();
            for (EmailAddress address : communications.listEmailAddresses()) {
                if (preferredAddress == address) {
                    processEmailAddress(model, address, contactResource, affiliationResource, true);
                }
                else {
                    processEmailAddress(model, address, contactResource, affiliationResource, false);
                }
            }
            for (PhoneNumber number : communications.listPhoneNumbers()) {
                processPhoneNumber(model, number, contactResource, affiliationResource);
            }
        }
    }

    private void processPhoneNumber(Model model, PhoneNumber number, Resource contactResource,
            Resource affiliationResource) {
        if (number != null) {
            Resource numberResource = UriUtil.generateRandomResource(model);
            model.addStatement(numberResource, RDF.type, NCO.PhoneNumber);
            addStringProperty(model, numberResource, NCO.phoneNumber, number.getNumber());
            if (number.isBBS()) {
                model.addStatement(numberResource, RDF.type, NCO.BbsNumber);
            }
            if (number.isCar()) {
                model.addStatement(numberResource, RDF.type, NCO.CarPhoneNumber);
            }
            if (number.isCellular()) {
                model.addStatement(numberResource, RDF.type, NCO.CellPhoneNumber);
            }
            if (number.isFax()) {
                model.addStatement(numberResource, RDF.type, NCO.FaxNumber);
            }
            if (number.isHome()) {
                addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource, number
                        .isPreferred());
            }
            if (number.isISDN()) {
                model.addStatement(numberResource, RDF.type, NCO.IsdnNumber);
            }
            if (number.isMessaging()) {
                model.addStatement(numberResource, RDF.type, NCO.MessagingNumber);
            }
            if (number.isMODEM()) {
                model.addStatement(numberResource, RDF.type, NCO.ModemNumber);
            }
            if (number.isPager()) {
                model.addStatement(numberResource, RDF.type, NCO.PagerNumber);
            }
            if (number.isPCS()) {
                model.addStatement(numberResource, RDF.type, NCO.PcsNumber);
            }
            if (number.isVideo()) {
                model.addStatement(numberResource, RDF.type, NCO.VideoTelephoneNumber);
            }
            if (number.isVoice()) {
                // isn't this nonsense?, if a voice telephone number is marked
                // with this type, then what is a telephone number that is not
                // marked with this type (sign-language videotelephone)
                model.addStatement(numberResource, RDF.type, NCO.VoicePhoneNumber);
            }
            if (number.isWork()) {
                if (affiliationResource != null) {
                    // this means that an affiliation has already been created while processing
                    // the organizational identity
                    addContactMediumProperty(model, affiliationResource, NCO.hasPhoneNumber, numberResource,
                        number.isPreferred());
                }
                else {
                    // this means that no affiliation has been created, we need to create one now
                    // an anonymous affiliation with an anonymous organization
                    affiliationResource = UriUtil.generateRandomResource(model);
                    model.addStatement(affiliationResource, RDF.type, NCO.Affiliation);
                    model.addStatement(contactResource, NCO.hasAffiliation, affiliationResource);
                    addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource,
                        number.isPreferred());
                }
            }

            if (!number.isHome() && !number.isWork()) {
                addContactMediumProperty(model, contactResource, NCO.hasPhoneNumber, numberResource, number
                        .isPreferred());
            }
        }
    }

    private void processEmailAddress(Model model, EmailAddress address, Resource contactResource,
            Resource affiliationResource, boolean preferred) {
        /*
         * This sucks, there is no way to tell if an email is a private email or a business email this sucks
         * ass...
         */
        if (address != null && address.isType(EmailAddress.TYPE_INTERNET)) {
            // we don't support other than internet addresses anyway, I have
            // no idea what are these x.400 addresses
            // TODO solve the x.400 address type issue
            Resource addressResource = UriUtil.generateRandomResource(model);
            model.addStatement(addressResource, RDF.type, NCO.EmailAddress);
            addStringProperty(model, addressResource, NCO.emailAddress, address.getAddress());
            // due to the fact that the VCARD specs suck ass, we always attach the address
            // to the contact and never to the affiliation
            addContactMediumProperty(model, contactResource, NCO.hasEmailAddress, addressResource, preferred);
        }
    }

    private void processGeographicalInformation(Model model, Resource contactResource, URI property,
            GeographicalInformation geo) {
        if (geo != null) {
            Resource geoResource = UriUtil.generateRandomResource(model);
            model.addStatement(geoResource, RDF.type, GEO.Point);
            model.addStatement(geoResource, GEO.lat, geo.getLatitude().toPlainString());
            model.addStatement(geoResource, GEO.long_, geo.getLongitude().toPlainString());
            model.addStatement(contactResource, property, geoResource);
        }
    }

    private void processImage(Model model, Resource contactResource, URI property, Image photo) {
        if (photo != null) {
            Resource imageResource = UriUtil.generateRandomResource(model);
            model.addStatement(imageResource, RDF.type, NEXIF.Photo);
            model.addStatement(imageResource, RDF.type, NFO.Attachment);
            model.addStatement(contactResource, property, imageResource);
            addStringProperty(model, imageResource, NIE.mimeType, photo.getContentType());
        }
    }

    private void processSound(Model model, Resource contactResource, URI property, Sound sound) {
        if (sound != null) {
            Resource soundResource = UriUtil.generateRandomResource(model);
            model.addStatement(soundResource, RDF.type, NFO.Audio);
            model.addStatement(soundResource, RDF.type, NFO.Attachment);
            model.addStatement(contactResource, property, soundResource);
            addStringProperty(model, soundResource, NIE.mimeType, sound.getContentType());
        }
    }

    private void processPublicKey(Model model, Resource contactResource, URI property, Key publicKey) {
        if (publicKey != null) {
            Resource keyResource = UriUtil.generateRandomResource(model);
            model.addStatement(keyResource, RDF.type, NIE.InformationElement);
            model.addStatement(keyResource, RDF.type, NFO.Attachment);
            model.addStatement(contactResource, property, keyResource);
            addStringProperty(model, keyResource, NIE.mimeType, publicKey.getContentType());
        }
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
            // note the + "Z" part, there are no timezones in VCARD and all timestamps are
            // in UTC
            model.addStatement(resource, property, model.createDatatypeLiteral(dateString + "Z", XSD._dateTime));
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
    private URI generateURIForContact(Contact contact, RDFContainer container, String contactHash) {
        String contactIdentifier = null;
        String uid = contact.getUID();
        if (uid != null) {
            contactIdentifier = uid;
        } else {
            contactIdentifier = contactHash;
        }
        return createChildUri(container.getDescribedUri(), contactIdentifier);
    }
    
    private String getContactHash(Contact contact, ContactMarshaller marshaller) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshallContact(stream, contact);
        return StringUtil.sha1Hash(stream.toByteArray());
    }
}
