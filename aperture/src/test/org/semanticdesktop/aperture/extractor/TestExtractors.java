/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.extractor.impl.TestExtractorRegistryImpl;
import org.semanticdesktop.aperture.extractor.plaintext.PlainTextExtractorTest;

/**
 * Tests all Extractor implelementations and related classes.
 */
public class TestExtractors extends TestSuite {

    public static Test suite() {
        return new TestExtractors();
    }
    
    private TestExtractors() {
        super("extractors");
        
        addTest(new TestSuite(PlainTextExtractorTest.class));
        
        addTest(new TestSuite(TestExtractorRegistryImpl.class));
    }
}
