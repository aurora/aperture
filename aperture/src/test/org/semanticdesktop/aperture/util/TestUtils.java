/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.util;

import junit.framework.Test;
import junit.framework.TestSuite;

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
        addTest(new TestSuite(XmlSafeModelTests.class));
        addTest(new TestSuite(UriUtilTest.class));
        addTest(new TestSuite(DateUtilTest.class));
        addTest(new TestSuite(HttpClientUtilTest.class));
    }
}
