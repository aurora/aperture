/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A test case for the vcard subcrawler
 */
public class VcardSubCrawlerTest extends SubCrawlerTestBase {

    private RDFContainer metadata;
    private TestBasicSubCrawlerHandler handler;
    
    public void testRfc2426ExampleExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Model model = metadata.getModel();
        assertStatementCount(2, model, Variable.ANY, RDF.type, NCO.PersonContact);        
        assertNewModUnmod(handler, 2, 0, 0, 0);
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    /**
     * The vcard-rfc2426.vcf contains more than one vcard, therefore the vcards inside will get a proper
     * vcard: uri. This test checks this. It uses an iterator because at the time of writing the jpim library
     * generated its own uids in a really crappy way that changed with each crawl.
     * 
     * @throws Exception
     */
    public void testRfc2426VcardUris() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
        Iterator<String> id = handler.getNewObjects().iterator();
        assertTrue(id.next().startsWith("vcard:uri:dummyuri!/"));
        assertTrue(id.next().startsWith("vcard:uri:dummyuri!/"));
        metadata.dispose();
        metadata = null;
    }
    
    public void testOutlookExampleExtraction() throws Exception {        
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmod(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testKontactExampleExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-kontact.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmod(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testDirkExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        // note that NO additional data objects have been reported, this
        // file contains only one contact
        assertNewModUnmod(handler, 0, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testSapVcardsExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-vCards-SAP.vcf", subCrawler);
        assertNewModUnmod(handler, 30, 0, 0, 0);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    /**
     * The vcard-vCards-SAP.vcf contains more than one vcard, therefore the vcards inside will get a proper
     * vcard: uri. This test checks this. It uses an iterator because at the time of writing the jpim library
     * generated its own uids in a really crappy way that changed with each crawl.
     * 
     * @throws Exception
     */
    public void testSapVcardsUris() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-vCards-SAP.vcf", subCrawler);
        Iterator<String> id = handler.getNewObjects().iterator();
        for (int i = 0; i < 30; i++) {
            assertTrue(id.next().startsWith("vcard:uri:dummyuri!/"));
        }
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
    
    public void testAntoniNames() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        assertSingleValueProperty(model, antoniContact, NCO.nameFamily, "Mylka");
        assertSingleValueProperty(model, antoniContact, NCO.nameGiven, "Antoni");
        assertSingleValueProperty(model, antoniContact, NCO.nameAdditional, "Jozef");
        assertSingleValueProperty(model, antoniContact, NCO.nameHonorificPrefix, "Herr");
        assertSingleValueProperty(model, antoniContact, NCO.nameHonorificSuffix, "jun.");
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
        
        
        Set<Resource> telephoneNumbers = findObjectResourceSet(model, affiliation, NCO.hasPhoneNumber);
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
        Set<Resource> emails = findObjectResourceSet(model,frankDawsonContact, NCO.hasEmailAddress);
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
        assertTrue(frankDawsonContact.toString().startsWith("vcard:"));
        Resource affiliation = findSingleObjectResource(model, frankDawsonContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, RDF.type, NCO.Affiliation);
        Resource address = findSingleObjectResource(model, affiliation, NCO.hasPostalAddress);
        assertMultiValueProperty(model, address, RDF.type, NCO.PostalAddress);
        assertMultiValueProperty(model, address, RDF.type, NCO.ParcelDeliveryAddress);
        assertSingleValueProperty(model, address, NCO.streetAddress, "6544 Battleford Drive");
        assertSingleValueProperty(model, address, NCO.locality, "Raleigh");
        assertSingleValueProperty(model, address, NCO.region, "NC");
    }
    
    public void testHomePostalAddress() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        Resource address = findSingleObjectResource(model, antoniContact, NCO.hasPostalAddress);
        assertMultiValueProperty(model, address, RDF.type, NCO.PostalAddress);
        
        assertSingleValueProperty(model, address, NCO.streetAddress, "Budryka 2/1110");
        assertSingleValueProperty(model, address, NCO.locality, "Krakow");
        assertSingleValueProperty(model, address, NCO.region, "malopolskie");
        assertSingleValueProperty(model, address, NCO.postalcode, "30-072");
        assertSingleValueProperty(model, address, NCO.country, "Polen");
    }
    
    public void testRole() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        Resource affiliation = findSingleObjectResource(model, antoniContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, NCO.role, "Software-Developer");
    }
    
    public void testTitle() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        Resource affiliation = findSingleObjectResource(model, antoniContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, NCO.title, "Intern");
    }
    
    public void testNickname() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        assertSingleValueProperty(model, antoniContact, NCO.nickname, "Ant");
    }
    
    public void testBday() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        assertSingleValueProperty(model, antoniContact, NCO.birthDate, "1980-01-18", XSD._date);
    }
    
    public void testOrganization() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-outlook2003.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource antoniContact = findContact(model, "Antoni Jozef Mylka jun.");
        Resource affiliation = findSingleObjectResource(model, antoniContact, NCO.hasAffiliation);
        assertSingleValueProperty(model, affiliation, RDF.type, NCO.Affiliation);
        assertSingleValueProperty(model, affiliation, NCO.department, "Knowledge-Management");
        Resource organization = findSingleObjectResource(model, affiliation, NCO.org);
        assertSingleValueProperty(model, organization, RDF.type, NCO.OrganizationContact);
        assertSingleValueProperty(model, organization, NCO.fullname, "DFKI");
    }
    
    public void testNote() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource dirkContact = findContact(model, "Dirk");
        assertEquals("uri:dummyuri",dirkContact.toString());
        assertSingleValueProperty(model, dirkContact, NCO.note, "The canonical Dirk\r\n");
        metadata.dispose();
        metadata = null;
    }
    
    public void testRev() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-dirk.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource dirkContact = findContact(model, "Dirk");
        assertSingleValueProperty(model, dirkContact, NIE.contentLastModified, "2007-11-09T10:46:02Z", XSD._dateTime);
        metadata.dispose();
        metadata = null;
    }
    
    public void testRev2() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-antoni-kontact.vcf", subCrawler);
        Model model = metadata.getModel();
        Resource dirkContact = findContact(model, "Antoni Mylka");
        assertSingleValueProperty(model, dirkContact, NIE.contentLastModified, "2008-01-28T15:50:16Z", XSD._dateTime);
        metadata.dispose();
        metadata = null;
    }
    
    // this test failes because of a very controversial feature in jpim
    // TODO finish this after the uid issue is resolved
//    public void testUid() throws Exception {
//        VcardSubCrawler subCrawler = new VcardSubCrawler();
//        metadata = subCrawl(DOCS_PATH + "vcard-antoni-kontact.vcf", subCrawler);
//        Model model = metadata.getModel();
//        Resource dirkContact = findContact(model, "Antoni Mylka");
//        assertSingleValueProperty(model, dirkContact, NCO.contactUID, "BHTRsCvcmd");
//        metadata.dispose();
//        metadata = null;
//    }

    private RDFContainer subCrawl(String string, VcardSubCrawler subCrawler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(string, this.getClass());
        handler = new TestBasicSubCrawlerHandler();
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl("uri:dummyuri"));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
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

