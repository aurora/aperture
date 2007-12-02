/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.impl;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.fileextractor.FileExtractor;
import org.semanticdesktop.aperture.fileextractor.FileExtractorFactory;

/**
 * Basic tests for the FileExtractorRegistryImpl class
 */
public class TestFileExtractorRegistryImpl extends TestCase {

    public void testBasics() {
        FileExtractorRegistryImpl registry = new FileExtractorRegistryImpl();
        FileExtractorFactory factory1 = new DummyFactory("text/plain");
        FileExtractorFactory factory2 = new DummyFactory("text/html");
        registry.add(factory1);
        registry.add(factory2);
        
        assertEquals(2, registry.getAll().size());
        assertEquals(1, registry.get("text/html").size());
        
        registry.remove(factory2);

        assertEquals(1, registry.getAll().size());
        assertEquals(0, registry.get("text/html").size());
        assertEquals(1, registry.get("text/plain").size());
    }
    
    private static class DummyFactory implements FileExtractorFactory {

        private String mimeType;
        
        public DummyFactory(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public FileExtractor get() {
            return null;
        }

        public Set getSupportedMimeTypes() {
            return Collections.singleton(mimeType);
        }
    }
}
