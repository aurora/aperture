/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.tar;

import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;

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
    
    public void testTarSubCrawlerIncrementalCombination() throws Exception {
        testCrawlerIncremental(new TarSubCrawlerFactory(), "TestTarSubCrawlerCombination.tmpDir", "tar-test.tar", ".tar",9);
    }
}


