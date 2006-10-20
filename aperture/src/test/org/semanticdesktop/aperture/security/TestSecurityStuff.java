/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security;

import org.semanticdesktop.aperture.security.trustmanager.standard.TestStandardTrustManager;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSecurityStuff extends TestSuite {

    public static Test suite() {
        return new TestSecurityStuff();
    }
    
    public TestSecurityStuff() {
        super("security");
        addTest(new TestSuite(TestStandardTrustManager.class));
    }
}

