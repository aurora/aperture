/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import java.io.IOException;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.subcrawler.zip.ZipSubCrawlerFactory;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * A test case for the gzip subcrawler
 */
public class GZipSubCrawlerTest extends SubCrawlerTestBase {
    
    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testGZipTest() throws Exception {
        GZipSubCrawler subCrawler = new GZipSubCrawler();
        CompressorSubCrawlerHandler handler = new CompressorSubCrawlerHandler();
        metadata = subCrawl("gzip-txt-gziptest.txt.gz", subCrawler, handler);
        doBasicCompressorTest(metadata.getModel(), "gzip-txt-gziptest.txt.gz", "gzip-txt-gziptest.txt","gzip");
 
        // the extracted text is as follows, this means that the decompression works
        assertEquals("This is a text file for Aperture\n", handler.getExtractedString());
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testGZipTestIncremental() throws Exception {
        testCrawlerIncremental(new GZipSubCrawlerFactory(), "TestGZipSubCrawlerCombination.tmpDir", "gzip-txt-gziptest.txt.gz", ".gz",1);
    }
}


