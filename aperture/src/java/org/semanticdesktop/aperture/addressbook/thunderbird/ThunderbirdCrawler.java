/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.addressbook.AddressbookCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.demork.Demork;
import org.semanticdesktop.demork.Utils;
import org.semanticdesktop.demork.database.Cell;
import org.semanticdesktop.demork.database.Database;
import org.semanticdesktop.demork.database.Row;
import org.semanticdesktop.demork.database.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a crawler for the thunderbird address book.
 */
public class ThunderbirdCrawler extends AddressbookCrawler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public final static String TYPE = "thunderbird";

    private final static String THUNDERBIRD_URI_BASE = "urn:thunderbird:";

    static private List<String> skipProperties = new Vector<String>();
    static {
        skipProperties.add("PreferMailFormat");
        skipProperties.add("PopularityIndex");
        skipProperties.add("RecordKey");
        skipProperties.add("LastModifiedData");
    }

    public ThunderbirdCrawler(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }
    
    public ThunderbirdCrawler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.semanticdesktop.aperture.addressbook.AddressbookCrawler#crawlAddressbook()
     */
    public List crawlAddressbook() throws Exception {

        String abookFile = getAddressBookFile();
        
        if (abookFile == null) {
            throw new NullPointerException("No thunderbirdAddressbookPath option set");
        }

        Demork demork = new Demork();

        String encoding = demork.getEncoding(abookFile);

        String mab = Utils.readWholeFileAsEncoding(abookFile, encoding);

        Database d = demork.inputMork(mab);

        List res = new Vector();

        for (Iterator i = d.tables.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Table t = (Table) d.tables.get(key);
            for (Iterator j = t.rows.keySet().iterator(); j.hasNext();) {
                String rowkey = (String) j.next();
                DataObject o = reportContact((Row) t.rows.get(rowkey));
                if (o != null)
                    res.add(o);
            }
        }
        return res;
    }
    
    public URI getContactListUri() {
        String abookFile = getAddressBookFile();
        if (abookFile == null) {
            return null;
        } else {
            File file = new File(abookFile);
            try {
                return new URIImpl(file.toURI().toURL() + "#ThunderbirdContactList");
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    
    private String getAddressBookFile() {
        return ((ThunderbirdAddressbookDataSource)getDataSource()).getThunderbirdAddressbookPath();
    }


    private Resource affiliationResource;
    private Resource organizationResource;
    
    private void addAffiliation(Resource contactResource, Model model) {
        if (affiliationResource == null && organizationResource == null) {
            affiliationResource = UriUtil.generateRandomResource(model);
            organizationResource = UriUtil.generateRandomResource(model);
            model.addStatement(affiliationResource,RDF.type,NCO.Affiliation);
            model.addStatement(organizationResource,RDF.type,NCO.OrganizationContact);
            model.addStatement(contactResource,NCO.hasAffiliation,affiliationResource);
            model.addStatement(affiliationResource,NCO.org,organizationResource);
        }
    }
    
    private DataObject reportContact(Row row) {
        Hashtable<String, String> values = new Hashtable<String, String>();
        for (Iterator<Cell> i = row.cells.iterator(); i.hasNext();) {
            Cell c = i.next();
            if (!skipProperties.contains(c.column) && !c.atom.equals("")) {
                values.put(c.column, c.atom);
            }
        }
        if (values.size() == 0)
            return null;

        String uris = createURI(row.id);
        URI uri = URIImpl.createURIWithoutChecking(uris);

        RDFContainerFactory rdff = handler.getRDFContainerFactory(null, uris);
        RDFContainer rdf = rdff.getRDFContainer(uri);
        Model model = rdf.getModel();
        
        rdf.add(RDF.type, NCO.Contact);
        rdf.add(RDF.type, NCO.ContactListDataObject);
        affiliationResource = null;
        organizationResource = null;

        for (Iterator i = values.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = (String) values.get(key);

            try {
                if (key.equals("PrimaryEmail")) {
                    // TODO: Which email?
                    Resource emailResource = UriUtil.generateRandomResource(model);
                    model.addStatement(emailResource, RDF.type, NCO.EmailAddress);
                    model.addStatement(emailResource, NCO.emailAddress, value);
                    // TODO get back to it when hasPreferredContactMedium is introduced
                    model.addStatement(emailResource, NCO.contactMediumComment, "Primary Email");
                }
                else if (key.equals("SecondEmail")) {
                    // TODO: Which email?
                    Resource emailResource = UriUtil.generateRandomResource(model);
                    model.addStatement(emailResource, RDF.type, NCO.EmailAddress);
                    model.addStatement(emailResource, NCO.emailAddress, value);
                    // TODO get back to it when hasPreferredContactMedium is introduced
                    model.addStatement(emailResource, NCO.contactMediumComment, "Second Email");
                }
                else if (key.equals("FirstName")) {
                    rdf.add(NCO.nameGiven, rdf.getValueFactory().createLiteral(value));
                    rdf.add(RDF.type, NCO.PersonContact);
                }
                else if (key.equals("LastName")) {
                    rdf.add(NCO.nameFamily, rdf.getValueFactory().createLiteral(value));
                    rdf.add(RDF.type, NCO.PersonContact);
                }
                else if (key.equals("DisplayName")) {
                    // TODO: what if things don't have a fullname?
                    rdf.add(RDFS.label, rdf.getValueFactory().createLiteral(value));
                    rdf.add(NCO.fullname, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("NickName")) {
                    rdf.add(NCO.nickname, rdf.getValueFactory().createLiteral(value));
                    rdf.add(RDF.type, NCO.PersonContact);
                }
                else if (key.equals("WorkPhone")) {
                    addAffiliation(uri, model);
                    addPhoneNumber(affiliationResource, model, value,null);
                }
                else if (key.equals("HomePhone")) {
                    addPhoneNumber(uri, model, value, null);
                }
                else if (key.equals("FaxNumber")) {
                    addPhoneNumber(uri, model, value, NCO.FaxNumber);
                }
                else if (key.equals("PagerNumber")) {
                    addPhoneNumber(uri, model, value, NCO.PagerNumber);
                }
                else if (key.equals("CellularNumber")) {
                    addPhoneNumber(uri, model, value, NCO.CellPhoneNumber);
                }
                else if (key.equals("JobTitle")) {
                    addAffiliation(uri, model);
                    model.addStatement(affiliationResource,NCO.title,value);
                }
                else if (key.equals("Department")) {
                    addAffiliation(uri, model);
                    model.addStatement(affiliationResource,NCO.department,value);
                }
                else if (key.equals("Company")) {
                    addAffiliation(uri, model);
                    model.addStatement(organizationResource,NCO.fullname,value);
                }
                else if (key.equals("_AimScreenName")) {
                    Resource aimResource = UriUtil.generateRandomResource(model);
                    model.addStatement(aimResource,RDF.type,NCO.IMAccount);
                    model.addStatement(aimResource,NCO.imAccountType,"AIM");
                    model.addStatement(aimResource,NCO.imNickname,value);
                    rdf.add(NCO.hasIMAccount,aimResource);
                }
                else if (key.equals("WebPage1")) {
                    rdf.add(NCO.websiteUrl,value);
                }
                else if (key.equals("WebPage2")) {
                    rdf.add(NCO.websiteUrl,value);
                }
                else if (key.equals("Custom1")) {
                    rdf.add(NCO.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom2")) {
                    rdf.add(NCO.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom3")) {
                    rdf.add(NCO.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom4")) {
                    rdf.add(NCO.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Notes")) {
                    rdf.add(NCO.note, rdf.getValueFactory().createLiteral(value));
                }
                
                addAddress(values, uri, model, "Home", NCO.hasPostalAddress);
                addAddress(values, uri, model, "Work", NCO.hasPostalAddress);
                
                model.addStatement(model.createStatement(getContactListUri(), NCO.containsContact, uri));
            }
            catch (ModelException e) {
                logger.error("ModelException while adding statements", e);
            }
        }

        DataObjectBase object = new DataObjectBase(uri, source, rdf);

        return object;
    }

    private void addEmailAddress(Resource contactResource, Model model, String value, URI type, String comment) {
        Resource emailAddressResource = UriUtil.generateRandomResource(model);
        if (type == null) {
            model.addStatement(emailAddressResource,RDF.type,NCO.EmailAddress);
        } else {
            model.addStatement(emailAddressResource,RDF.type,type);
        }
        model.addStatement(contactResource,NCO.hasEmailAddress,emailAddressResource);
        model.addStatement(emailAddressResource,NCO.emailAddress,value);
        if (comment != null) {
            model.addStatement(emailAddressResource,NCO.contactMediumComment,comment);
        }
    }
    
    private void addPhoneNumber(Resource contactResource, Model model, String value, URI type) {
        Resource numberResource = UriUtil.generateRandomResource(model);
        if (type == null) {
            model.addStatement(numberResource,RDF.type,NCO.PhoneNumber);
        } else {
            model.addStatement(numberResource,RDF.type,type);
        }
        model.addStatement(contactResource,NCO.hasPhoneNumber,numberResource);
        model.addStatement(numberResource,NCO.phoneNumber,value);
    }

    private void addAddress(Hashtable values, Resource roleResource, Model model, String type, URI address) throws ModelException {
        URI addressURI = model.createURI(roleResource.toString() + "_" + type + "Address");
        boolean ok = false;
        if (values.containsKey(type + "Address")) {
            ok = true;
            if (values.containsKey(type + "Address2")) {
                Literal literal = model.createPlainLiteral(
                    ((String) values.get(type + "Address")) + values.get(type + "Address2"));
                model.addStatement(addressURI, NCO.streetAddress, literal);
            }
            else {
                Literal literal = model.createPlainLiteral((String) values.get(type + "Address"));
                model.addStatement(addressURI, NCO.streetAddress, literal);
            }
        }
        if (values.containsKey(type + "City")) {
            ok = true;
            model.addStatement(addressURI,NCO.locality,(String) values.get(type + "City"));
        }
        if (values.containsKey(type + "Country")) {
            ok = true;
            model.addStatement(addressURI,NCO.country,(String) values.get(type + "Country"));
        }
        if (values.containsKey(type + "State")) {
            ok = true;
            model.addStatement(addressURI,NCO.region,(String) values.get(type + "State"));
        }
        if (values.containsKey(type + "ZipCode")) {
            ok = true;
            model.addStatement(addressURI,NCO.postalcode,(String) values.get(type + "ZipCode"));
        }
        if (ok) {
            model.addStatement(addressURI,RDF.type,NCO.PostalAddress);
            model.addStatement(roleResource,NCO.hasPostalAddress,addressURI);
        }
    }

    private String createURI(String id) {
        return THUNDERBIRD_URI_BASE + "Person:" + id;
    }

    // Private worker as we are trying to force UTF-8.
    private static String readWholeFile(InputStream is) throws IOException {
        Reader r = new InputStreamReader(is, Charset.forName("utf-8").newDecoder());
        StringWriter sw = new StringWriter(1024);
        char buff[] = new char[1024];
        while (r.ready()) {
            int l = r.read(buff);
            if (l <= 0)
                break;
            sw.write(buff, 0, l);
        }
        r.close();
        sw.close();
        return sw.toString();
    }
}
