/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.accessor.TestAccessors;
import org.semanticdesktop.aperture.crawler.TestCrawlers;
import org.semanticdesktop.aperture.datasource.TestDataSources;
import org.semanticdesktop.aperture.extractor.TestExtractors;
import org.semanticdesktop.aperture.hypertext.linkextractor.TestLinkExtractors;
import org.semanticdesktop.aperture.mime.TestMimeStuff;
import org.semanticdesktop.aperture.rdf.TestRDFContainers;
import org.semanticdesktop.aperture.security.TestSecurityStuff;
import org.semanticdesktop.aperture.subcrawler.TestSubCrawlers;
import org.semanticdesktop.aperture.util.TestUtils;

public class TestAll extends TestSuite {

    static public Test suite() {
        return new TestAll();
    }

    private TestAll() {
        super("aperture");
        
        addTest(TestRDFContainers.suite());
        addTest(TestAccessors.suite());
        addTest(TestDataSources.suite());
        addTest(TestCrawlers.suite());
        addTest(TestExtractors.suite());
        addTest(TestSubCrawlers.suite());
        addTest(TestLinkExtractors.suite());
        addTest(TestMimeStuff.suite());
        addTest(TestSecurityStuff.suite());
        addTest(TestUtils.suite());
    }
}
