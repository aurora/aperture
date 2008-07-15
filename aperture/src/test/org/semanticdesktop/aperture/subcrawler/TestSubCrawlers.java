/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.subcrawler.impl.TestDefaultSubCrawlerRegistry;
import org.semanticdesktop.aperture.subcrawler.impl.TestSubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.subcrawler.vcard.VcardSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.vcard.TestVcardFileCrawlerCombination;

/**
 * Tests all SubCrawler implementations and related classes.
 */
public class TestSubCrawlers extends TestSuite {

    public static Test suite() {
        return new TestSubCrawlers();
    }
    
    private TestSubCrawlers() {
        super("subcrawlers");
        
        // test the various sub crawlers
        addTest(new TestSuite(VcardSubCrawlerTest.class));
        addTest(new TestSuite(TestVcardFileCrawlerCombination.class));
           
        // test the registries holding the SubCrawlerFactories
        addTest(new TestSuite(TestSubCrawlerRegistryImpl.class));
        addTest(new TestSuite(TestDefaultSubCrawlerRegistry.class));
    }
}
