/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.io.InputStream;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.UriOrVariable;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A test case for the vcard extractor
 */
public class VcardSubCrawlerTest extends ApertureTestBase {

    private RDFContainer metadata;
    private VcardTestIncrementalSubCrawlerHandler handler;
    
    public void testRfc2426ExampleExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        assertStatementCount(2, model, Variable.ANY, RDF.type, NCO.PersonContact);        
        assertNewModUnmodDel(handler, 2, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testOutlookExampleExtraction() throws Exception {        
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmodDel(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testKontactExampleExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-kontact.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmodDel(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testDirkExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmodDel(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testSapVcardsExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-vCards-SAP.vcf", subCrawler);
        assertNewModUnmodDel(handler, 30, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testFrankDawsonNames() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        assertStatementCount(2, model, Variable.ANY, RDF.type, NCO.PersonContact);
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        assertSingleValueProperty(model, frankDawsonContact, NCO.nameFamily, "Dawson");
        assertSingleValueProperty(model, frankDawsonContact, NCO.nameGiven, "Frank");
    }
    
    public void testUrl() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        Resource url = findSingleObjectResource(model, frankDawsonContact, NCO.url);
        assertTrue(url.toString().equals("http://home.earthlink.net/~fdawson"));
    }
    
    public void testTelephoneNumbers() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        Resource affiliation = findSingleObjectResource(model, frankDawsonContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, RDF.type, NCO.Affiliation);
        
        
        List<Resource> telephoneNumbers = findObjectResourceList(model, affiliation, NCO.hasPhoneNumber);
        assertEquals(2, telephoneNumbers.size());
        assertSparqlQuery(model,
            "PREFIX nco: <" + NCO.NS_NCO + "> " +
            "SELECT ?number " +
            "WHERE" +
            "  { " + affiliation.toSPARQL() + " nco:hasPhoneNumber ?phoneNumber . " +
            "    ?phoneNumber a nco:PhoneNumber ." +
            "    ?phoneNumber nco:phoneNumber ?number ." +
            "    FILTER (regex(?number,\"\\\\+1-919-676-9515\"))" + // weird, four slashes are necessary...
            "  }");
        assertSparqlQuery(model,
            "PREFIX nco: <" + NCO.NS_NCO + "> " +
            "SELECT ?number " +
            "WHERE" +
            "  { " + affiliation.toSPARQL() + " nco:hasPhoneNumber ?phoneNumber . " +
            "    ?phoneNumber a nco:PhoneNumber ." +
            "    ?phoneNumber nco:phoneNumber ?number ." +
            "    FILTER (regex(?number,\"\\\\+1-919-676-9564\"))" +
            "  }");
    }
    
    public void testEmailAddresses() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        List<Resource> emails = findObjectResourceList(model,frankDawsonContact, NCO.hasEmailAddress);
        assertEquals(2, emails.size());
        assertSparqlQuery(model,
            "PREFIX nco: <" + NCO.NS_NCO + "> " +
            "SELECT ?email " +
            "WHERE" +
            "  { " + frankDawsonContact.toSPARQL() + " nco:hasEmailAddress ?email . " +
            "    ?email a nco:EmailAddress ." +
            "    ?email nco:emailAddress ?address ." +
            "    FILTER (regex(?address,\"Frank_Dawson@Lotus.com\"))" +
            "  }");
        assertSparqlQuery(model,
            "PREFIX nco: <" + NCO.NS_NCO + "> " +
            "SELECT ?email " +
            "WHERE" +
            "  { " + frankDawsonContact.toSPARQL() + " nco:hasEmailAddress ?email . " +
            "    ?email a nco:EmailAddress ." +
            "    ?email nco:emailAddress ?address ." +
            "    FILTER (regex(?address,\"fdawson@earthlink.net\"))" +
            "  }");
    }
    
    public void testWorkPostalAddress() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        Resource affiliation = findSingleObjectResource(model, frankDawsonContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, RDF.type, NCO.Affiliation);
        model.writeTo(System.out);
        Resource address = findSingleObjectResource(model, affiliation, NCO.hasPostalAddress);
        assertMultiValueProperty(model, address, RDF.type, NCO.PostalAddress);
        assertMultiValueProperty(model, address, RDF.type, NCO.ParcelDeliveryAddress);
        assertSingleValueProperty(model, address, NCO.streetAddress, "6544 Battleford Drive");
        assertSingleValueProperty(model, address, NCO.locality, "Raleigh");
        assertSingleValueProperty(model, address, NCO.region, "NC");
    }
    
    public void testOrganization() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource frankDawsonContact = findContact(model, "Frank Dawson");
        Resource affiliation = findSingleObjectResource(model, frankDawsonContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, RDF.type, NCO.Affiliation);
        Resource organization = findSingleObjectResource(model, affiliation, NCO.org);
        assertSingleValueProperty(model, organization, RDF.type, NCO.OrganizationContact);
        assertSingleValueProperty(model, organization, NCO.fullname, "Lotus Development Corporation");
    }
    
    public void testNote() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource dirkContact = findContact(model, "Dirk");
        assertSingleValueProperty(model, dirkContact, NCO.note, "The canonical Dirk\r\n");
        model.writeTo(System.out);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testRev() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource dirkContact = findContact(model, "Dirk");
        assertSingleValueProperty(model, dirkContact, NIE.contentLastModified, "2007-11-09T10:46:02Z", XSD._dateTime);
        model.writeTo(System.out);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }

    private RDFContainer subCrawl(String string, VcardSubCrawler subCrawler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(string, this.getClass());
        handler = new VcardTestIncrementalSubCrawlerHandler();
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl("uri:dummyuri"));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
    }
    
    private void assertNewModUnmodDel(VcardTestIncrementalSubCrawlerHandler subCrawlerHandler, int newObjects,
            int changedObjects, int unchangedObjects, int deletedObjects) {
        assertEquals(subCrawlerHandler.getNewObjects().size(), newObjects);
        assertEquals(subCrawlerHandler.getChangedObjects().size(), changedObjects);
        assertEquals(subCrawlerHandler.getUnchangedObjects().size(), unchangedObjects);
        assertEquals(subCrawlerHandler.getDeletedObjects().size(), deletedObjects);
    }
    
    private void assertStatementCount(int count, Model model, ResourceOrVariable subject, UriOrVariable predicate, NodeOrVariable object) {
        int result = 0;
        ClosableIterator<? extends Statement> iter = null;
        try {
            iter = model.findStatements(subject, predicate, object);
            while (iter.hasNext()) {
                result++;
                iter.next();
            }
        } finally {
            closeIterator(iter);
        }
        assertEquals(count,result);
    }
    
    public Resource findContact(Model model, String fullname) {
        QueryResultTable table = model.sparqlSelect(
                "PREFIX nco: <" + NCO.NS_NCO + "> " +
                "SELECT ?contact " +
                "WHERE" +
                "  { ?contact a nco:Contact ." +
                "    ?contact nco:fullname ?name . " +
                "    FILTER (regex(?name,\"" + fullname + "\"))" +
                "  }");
        ClosableIterator<QueryRow> iterator = null;
        try {
            iterator = table.iterator();
            assertTrue(iterator.hasNext());;
            QueryRow row = iterator.next();
            Node node = row.getValue("contact");
            return node.asResource();
        } finally {
            closeIterator(iterator);
        }
    }
}

