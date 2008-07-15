/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.accessor.base.CountingInputStreamTest;
import org.semanticdesktop.aperture.accessor.base.TestFileAccessData;
import org.semanticdesktop.aperture.accessor.base.TestFileDataObject;
import org.semanticdesktop.aperture.accessor.base.TestModelAccessData;
import org.semanticdesktop.aperture.accessor.base.TestAccessDataImpl;
import org.semanticdesktop.aperture.accessor.base.TestSynchronizedAccessData;
import org.semanticdesktop.aperture.accessor.base.TestNativeStoreModelAccessData;
import org.semanticdesktop.aperture.accessor.file.TestFileAccessor;
import org.semanticdesktop.aperture.accessor.impl.TestDefaultDataAccessorRegistry;

public class TestAccessors extends TestSuite {

    public static Test suite() {
        return new TestAccessors();
    }
    
    private TestAccessors() {
        super("accessors");
        
        addTest(new TestSuite(TestFileAccessor.class));
        addTest(new TestSuite(TestDefaultDataAccessorRegistry.class));
        addTest(new TestSuite(CountingInputStreamTest.class));
        addTest(new TestSuite(TestFileAccessData.class));
        addTest(new TestSuite(TestFileDataObject.class));
        addTest(new TestSuite(TestModelAccessData.class));
        addTest(new TestSuite(TestSynchronizedAccessData.class));
        addTest(new TestSuite(TestAccessDataImpl.class));
        addTest(new TestSuite(TestNativeStoreModelAccessData.class));
    }
}
