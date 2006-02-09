/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import org.semanticdesktop.aperture.accessor.base.TestAccessDataBase;
import org.semanticdesktop.aperture.accessor.file.TestFileAccessor;
import org.semanticdesktop.aperture.accessor.impl.TestDefaultDataAccessorRegistry;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAccessors extends TestSuite {

    public static Test suite() {
        return new TestAccessors();
    }
    
    private TestAccessors() {
        super("accessors");
        
        addTest(new TestSuite(TestFileAccessor.class));
        addTest(new TestSuite(TestDefaultDataAccessorRegistry.class));
        addTest(new TestSuite(TestAccessDataBase.class));
    }
}
