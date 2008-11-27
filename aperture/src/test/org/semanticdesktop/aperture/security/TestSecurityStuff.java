/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.security;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.security.trustmanager.standard.TestStandardTrustManager;

public class TestSecurityStuff extends TestSuite {

    public static Test suite() {
        return new TestSecurityStuff();
    }
    
    public TestSecurityStuff() {
        super("security");
        addTest(new TestSuite(TestStandardTrustManager.class));
    }
}

