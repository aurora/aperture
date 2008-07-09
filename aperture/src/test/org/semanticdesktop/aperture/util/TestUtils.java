/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.subcrawler.impl.TestDefaultSubCrawlerRegistry;
import org.semanticdesktop.aperture.subcrawler.impl.TestSubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.subcrawler.vcard.VcardSubCrawlerTest;
import org.semanticdesktop.aperture.subcrawler.vcard.TestVcardFileCrawlerCombination;

/**
 * Tests all utils
 */
public class TestUtils extends TestSuite {

    public static Test suite() {
        return new TestUtils();
    }
    
    private TestUtils() {
        super("utils");
        
        // test the various utils
        addTest(new TestSuite(InferenceUtilTest.class));
        addTest(new TestSuite(XmlSafetyTest.class));
        addTest(new TestSuite(WriterOutputStreamTest.class));
    }
}
