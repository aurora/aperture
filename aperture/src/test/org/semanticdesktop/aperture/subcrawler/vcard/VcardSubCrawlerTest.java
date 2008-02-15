/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.io.InputStream;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

/**
 * A test case for the vcard extractor
 */
public class VcardSubCrawlerTest extends ApertureTestBase {

    private RDFContainer metadata;
    private VcardTestIncrementalSubCrawlerHandler handler;
    
    public void testRfc2426ExampleExtraction() throws Exception {
        VcardSubCrawler subCrawler = new VcardSubCrawler();
        metadata = subCrawl(DOCS_PATH + "vcard-rfc2426.vcf", subCrawler);
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
}

