/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.bzip2;

import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;

/**
 * A test case for the bzip2 subcrawler
 */
public class BZip2SubCrawlerTest extends SubCrawlerTestBase {
    
    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testBZip2Test() throws Exception {
        BZip2SubCrawler subCrawler = new BZip2SubCrawler();
        CompressorSubCrawlerHandler handler = new CompressorSubCrawlerHandler();
        metadata = subCrawl("bzip2-txt-bziptest.txt.bz2", subCrawler, handler);
        doBasicCompressorTest(metadata.getModel(), "bzip2-txt-bziptest.txt.bz2", "bzip2-txt-bziptest.txt","bzip2");
 
        // the extracted text is as follows, this means that the decompression works
        assertEquals("This is a text file for Aperture\n", handler.getExtractedString());
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testBZip2TestIncremental() throws Exception {
        testCrawlerIncremental(new BZip2SubCrawlerFactory(), "TestBZip2SubCrawlerCombination.tmpDir", "bzip2-txt-bziptest.txt.bz2", ".bz2",1);
    }
}


