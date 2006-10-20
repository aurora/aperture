/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.crawler.filesystem.TestFileSystemCrawler;
import org.semanticdesktop.aperture.crawler.ical.TestIcalCrawler;
import org.semanticdesktop.aperture.crawler.ical.TestIcalCrawlerIncremental;
import org.semanticdesktop.aperture.crawler.impl.TestDefaultCrawlerRegistry;

/**
 * Tests all Crawler implementations and related classes.
 */
public class TestCrawlers extends TestSuite {

    public static Test suite() {
        return new TestCrawlers();
    }
    
    private TestCrawlers() {
        super("crawlers");
        addTest(new TestSuite(TestFileSystemCrawler.class));
        addTest(new TestSuite(TestDefaultCrawlerRegistry.class));
        addTest(new TestSuite(TestIcalCrawler.class));
        addTest(new TestSuite(TestIcalCrawlerIncremental.class));
    }
}
