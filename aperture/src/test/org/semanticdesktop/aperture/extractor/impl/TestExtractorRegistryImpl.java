/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.impl;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;

public class TestExtractorRegistryImpl extends TestCase {

    public void testBasics() {
        ExtractorRegistryImpl registry = new ExtractorRegistryImpl();
        ExtractorFactory factory1 = new DummyFactory("text/plain");
        ExtractorFactory factory2 = new DummyFactory("text/html");
        
        FileExtractorFactory ffactory1 = new DummyFileExtractorFactory("audio/mpeg");
        FileExtractorFactory ffactory2 = new DummyFileExtractorFactory("audio/wav");
        
        registry.add(factory1);
        registry.add(factory2);
        
        registry.add(ffactory1);
        registry.add(ffactory2);
        
        assertEquals(2, registry.getAllExtractorFactories().size());
        assertEquals(1, registry.getExtractorFactories("text/html").size());
        
        registry.remove(factory2);

        assertEquals(1, registry.getAllExtractorFactories().size());
        assertEquals(0, registry.getExtractorFactories("text/html").size());
        assertEquals(1, registry.getExtractorFactories("text/plain").size());
        
        assertEquals(2, registry.getAllFileExtractorFactories().size());
        assertEquals(1, registry.getFileExtractorFactories("audio/mpeg").size());
        
        registry.remove(ffactory1);

        assertEquals(1, registry.getAllFileExtractorFactories().size());
        assertEquals(0, registry.getFileExtractorFactories("audio/mpeg").size());
        assertEquals(1, registry.getFileExtractorFactories("audio/wav").size());
        
    }
    
    private static class DummyFactory implements ExtractorFactory {

        private String mimeType;
        
        public DummyFactory(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public Extractor get() {
            return null;
        }

        public Set getSupportedMimeTypes() {
            return Collections.singleton(mimeType);
        }
    }
    
    private static class DummyFileExtractorFactory implements FileExtractorFactory {

        private String mimeType;
        
        public DummyFileExtractorFactory(String mimeType) {
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
