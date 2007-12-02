/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.fileextractor.impl.TestDefaultFileExtractorRegistry;
import org.semanticdesktop.aperture.fileextractor.impl.TestFileExtractorRegistryImpl;
import org.semanticdesktop.aperture.fileextractor.mp3.MP3FileExtractorTest;

/**
 * Tests all Extractor implementations and related classes.
 */
public class TestFileExtractors extends TestSuite {

    public static Test suite() {
        return new TestFileExtractors();
    }
    
    private TestFileExtractors() {
        super("fileextractors");
        
        addTest(new TestSuite(MP3FileExtractorTest.class));
        
        // test the registries holding the ExtractorFactories
        addTest(new TestSuite(TestFileExtractorRegistryImpl.class));
        addTest(new TestSuite(TestDefaultFileExtractorRegistry.class));
    }
}
