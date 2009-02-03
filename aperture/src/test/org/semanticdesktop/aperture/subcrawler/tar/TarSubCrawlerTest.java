/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.tar;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A test case for the tar subcrawler
 */
public class TarSubCrawlerTest extends SubCrawlerTestBase {
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testTarTest() throws Exception {
        TarSubCrawler subCrawler = new TarSubCrawler();
        RDFContainer metadata = subCrawl("tar-test.tar", subCrawler, new TestBasicSubCrawlerHandler());
        doBasicArchiverTests(metadata.getModel(), "tar-test.tar", "tar");
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    /**
     * Tests if the content of a tar can actually be used.
     * @throws Exception
     */
    public void testTarredPdf() throws Exception {
        TarSubCrawler subCrawler = new TarSubCrawler();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler(new DefaultExtractorRegistry());        
        RDFContainer metadata = subCrawl("pdf-openoffice-2.0-writer.pdf.tar", subCrawler, handler);
        URI uri = new URIImpl("tar:uri:dummyuri/pdf-openoffice-2.0-writer.pdf.tar!/pdf-openoffice-2.0-writer.pdf");
        RDFContainer container = new RDFContainerImpl(handler.getModel(),uri);
        checkStatement(NIE.plainTextContent, "is an example document created with OpenOffice 2.0", container);
        handler.close();
    }
    
    public void testTarSubCrawlerIncrementalCombination() throws Exception {
        testCrawlerIncremental(new TarSubCrawlerFactory(), "TestTarSubCrawlerCombination.tmpDir", "tar-test.tar", ".tar",9);
    }
}
