/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.addressbook.AddressbookCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA_GEN;
import org.semanticdesktop.aperture.vocabulary.VCARD;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.semanticdesktop.aperture.addressbook.AddressbookCrawler#crawlAddressbook()
     */
    public List crawlAddressbook() throws Exception {

        String abookFile = ConfigurationUtil.getBasepath(getDataSource().getConfiguration());

        Demork demork = new Demork();

        String encoding = demork.getEncoding(abookFile);

        System.out.println(encoding);

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

        rdf.add(RDF.type, VCARD.VCard);

        for (Iterator i = values.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = (String) values.get(key);

            try {
                if (key.equals("PrimaryEmail")) {
                    // TODO: Which email?
                    rdf.add(DATA_GEN.emailAddress, rdf.getValueFactory().createLiteral(value));
                    rdf.add(VCARD.email, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("SecondEmail")) {
                    // TODO: Which email?
                    rdf.add(DATA_GEN.emailAddress, rdf.getValueFactory().createLiteral(value));
                    rdf.add(VCARD.email, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("FirstName")) {
                    rdf.add(VCARD.nameGiven, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("LastName")) {
                    rdf.add(VCARD.nameFamily, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("DisplayName")) {
                    // TODO: what if things don't have a fullname?
                    rdf.add(RDFS.label, rdf.getValueFactory().createLiteral(value));
                    rdf.add(VCARD.fullname, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("NickName")) {
                    rdf.add(VCARD.nameAdditional, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("WorkPhone")) {
                    rdf.add(VCARD.telWork, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("HomePhone")) {
                    rdf.add(VCARD.telHome, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("FaxNumber")) {
                    rdf.add(VCARD.telFax, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("PagerNumber")) {
                    rdf.add(VCARD.telPager, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("CellularNumber")) {
                    rdf.add(VCARD.telCell, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("JobTitle")) {
                    rdf.add(VCARD.title, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Department")) {
                    rdf.add(VCARD.org, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Company")) {
                    rdf.add(VCARD.org, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("_AimScreenName")) {
                    rdf.add(VCARD.nameAdditional, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("WebPage1")) {
                    rdf.add(DATA_GEN.homepage, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("WebPage2")) {
                    rdf.add(DATA_GEN.homepage, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom1")) {
                    rdf.add(VCARD.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom2")) {
                    rdf.add(VCARD.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom3")) {
                    rdf.add(VCARD.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Custom4")) {
                    rdf.add(VCARD.note, rdf.getValueFactory().createLiteral(value));
                }
                else if (key.equals("Notes")) {
                    rdf.add(VCARD.note, rdf.getValueFactory().createLiteral(value));
                }
                
                addAddress(values, rdf, "Home", VCARD.addressHome);
                addAddress(values, rdf, "Work", VCARD.addressWork);
            }
            catch (ModelException e) {
                logger.error("ModelException while adding statements", e);
            }
        }

        DataObjectBase object = new DataObjectBase(uri, source, rdf);

        return object;
    }

    private void addAddress(Hashtable values, RDFContainer rdf, String type, URI address) throws ModelException {
        /*
         * } else if (key.equals("HomeAddress")) { } else if (key.equals("HomeAddress2")) { } else if
         * (key.equals("HomeCity")) { HomeState, HomeZipCode, HomeCountry }
         */
        URI a = rdf.getValueFactory().createURI(rdf.getDescribedUri().toString() + "_" + type + "Address");
        boolean ok = false;
        if (values.containsKey(type + "Address")) {
            ok = true;
            if (values.containsKey(type + "Address2")) {
                Literal literal = rdf.getValueFactory().createLiteral(
                    ((String) values.get(type + "Address")) + values.get(type + "Address2"));
                rdf.add(rdf.getValueFactory().createStatement(a, VCARD.streetAddress, literal));
            }
            else {
                Literal literal = rdf.getValueFactory().createLiteral((String) values.get(type + "Address"));
                rdf.add(rdf.getValueFactory().createStatement(a, VCARD.streetAddress, literal));
            }
        }
        if (values.containsKey(type + "City")) {
            ok = true;
            rdf.add(rdf.getValueFactory().createStatement(a, VCARD.locality,
                rdf.getValueFactory().createLiteral(((String) values.get(type + "City")))));
        }
        if (values.containsKey(type + "Country")) {
            ok = true;
            rdf.add(rdf.getValueFactory().createStatement(a, VCARD.country,
                rdf.getValueFactory().createLiteral(((String) values.get(type + "Country")))));
        }
        if (values.containsKey(type + "State")) {
            ok = true;
            rdf.add(rdf.getValueFactory().createStatement(a, VCARD.region,
                rdf.getValueFactory().createLiteral(((String) values.get(type + "City")))));
        }
        if (values.containsKey(type + "ZipCode")) {
            ok = true;
            rdf.add(rdf.getValueFactory().createStatement(a, VCARD.postalcode,
                rdf.getValueFactory().createLiteral(((String) values.get(type + "ZipCode")))));
        }
        if (ok) {
            rdf.add(address, a);
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
