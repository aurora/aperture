/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.access;

import org.semanticdesktop.aperture.access.file.TestFileAccessor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAccessors extends TestSuite {

    public static Test suite() {
        return new TestAccessors();
    }
    
    private TestAccessors() {
        super("accessors");
        
        addTest(new TestSuite(TestFileAccessor.class));
    }
}
