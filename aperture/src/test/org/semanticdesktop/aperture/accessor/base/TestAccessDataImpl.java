/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.FileUtil;

public class TestAccessDataImpl extends TestCase {


    public void testInputOutput() throws IOException {
    	AccessData accessData = new AccessDataImpl();
    	AccessDataTest.test(accessData);
    }
    
    public void testTouchedUntouched() throws IOException {
        AccessData accessData = new AccessDataImpl();
        AccessDataTest.testTouchedAndUntouched(accessData);
    }
    
    public void testTouchRecursively() throws IOException {
        AccessData accessData = new AccessDataImpl();
        AccessDataTest.testTouchRecursively(accessData);
    }
    
    public void testGetAggregatedIDsClosure() throws IOException {
        AccessData accessData = new AccessDataImpl();
        AccessDataTest.testGetAggregationClosure(accessData);
    }
}
