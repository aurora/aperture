/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.subcrawler.base.AbstractArchiverSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.base.AbstractSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.bzip2.BZip2SubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.gzip.GZipSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.impl.TestDefaultSubCrawlerRegistry;
import org.semanticdesktop.aperture.subcrawler.impl.TestSubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.subcrawler.mime.MimeSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.tar.TarSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.vcard.TestVcardFileCrawlerCombination;
import org.semanticdesktop.aperture.subcrawler.vcard.VcardSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.zip.ZipSubCrawlerTest;

/**
 * Tests all SubCrawler implementations and related classes.
 */
public class TestSubCrawlers extends TestSuite {

    public static Test suite() {
        return new TestSubCrawlers();
    }
    
    private TestSubCrawlers() {
        super("subcrawlers");
        
        // test the abstract subcrawler
        addTest(new TestSuite(AbstractSubCrawlerTest.class));
        addTest(new TestSuite(AbstractArchiverSubCrawlerTest.class));
        addTest(new TestSuite(TestSubCrawlerUtil.class));
        addTest(new TestSuite(TestSubCrawlerUtilIntegration.class));
        
        // test the various sub crawlers
        addTest(new TestSuite(VcardSubCrawlerTest.class));
        addTest(new TestSuite(TestVcardFileCrawlerCombination.class));
        addTest(new TestSuite(ZipSubCrawlerTest.class));
        addTest(new TestSuite(GZipSubCrawlerTest.class));
        addTest(new TestSuite(TarSubCrawlerTest.class));
        addTest(new TestSuite(BZip2SubCrawlerTest.class));
        addTest(new TestSuite(MimeSubCrawlerTest.class));
           
        // test the registries holding the SubCrawlerFactories
        addTest(new TestSuite(TestSubCrawlerRegistryImpl.class));
        addTest(new TestSuite(TestDefaultSubCrawlerRegistry.class));
    }
}
