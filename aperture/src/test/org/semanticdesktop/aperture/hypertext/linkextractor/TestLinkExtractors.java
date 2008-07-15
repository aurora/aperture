/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.hypertext.linkextractor.html.HtmlLinkExtractorTest;
import org.semanticdesktop.aperture.hypertext.linkextractor.impl.TestDefaultLinkExtractorRegistry;

public class TestLinkExtractors extends TestSuite {

    public static Test suite() {
        return new TestLinkExtractors();
    }
    
    public TestLinkExtractors() {
        super("link extractors");
        
        addTest(new TestSuite(HtmlLinkExtractorTest.class));
        
        addTest(new TestSuite(TestDefaultLinkExtractorRegistry.class));
    }
}

