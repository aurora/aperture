/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-Style license
 */
package org.semanticdesktop.aperture.integration;

import junit.framework.Test;
import junit.framework.TestSuite;


public class IntegrationTestAll extends TestSuite {
    
    static public Test suite() {
        return new IntegrationTestAll();
    }
    
    private IntegrationTestAll() {
        super("aperture-integration");
        
        addTest(new TestSuite(ApertureBundlesStartTest.class));
    }

}

