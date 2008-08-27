/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.addressbook.AbstractAddressbookCrawlerTest;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdCrawlerTest;
import org.semanticdesktop.aperture.crawler.filesystem.TestFileSystemCrawler;
import org.semanticdesktop.aperture.crawler.ical.TestIcalCrawler;
import org.semanticdesktop.aperture.crawler.ical.TestIcalCrawlerIncremental;
import org.semanticdesktop.aperture.crawler.impl.TestDefaultCrawlerRegistry;
import org.semanticdesktop.aperture.crawler.ical.DurationConversionTest;
import org.semanticdesktop.aperture.crawler.mbox.TestMboxCrawler;
import org.semanticdesktop.aperture.crawler.mbox.TestMboxCrawlerMultiFolder;

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
        addTest(new TestSuite(DurationConversionTest.class));
        addTest(new TestSuite(TestMboxCrawler.class));
        addTest(new TestSuite(TestMboxCrawlerMultiFolder.class));
        addTest(new TestSuite(ThunderbirdCrawlerTest.class));
        addTest(new TestSuite(AbstractAddressbookCrawlerTest.class));
    }
}
