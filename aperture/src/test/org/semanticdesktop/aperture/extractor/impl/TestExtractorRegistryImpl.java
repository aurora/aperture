/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.impl;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

import junit.framework.TestCase;

public class TestExtractorRegistryImpl extends TestCase {

    public void testBasics() {
        ExtractorRegistryImpl registry = new ExtractorRegistryImpl();
        ExtractorFactory factory1 = new DummyFactory("text/plain");
        ExtractorFactory factory2 = new DummyFactory("text/html");
        registry.add(factory1);
        registry.add(factory2);
        
        assertEquals(2, registry.getAll().size());
        assertEquals(1, registry.get("text/html").size());
        
        registry.remove(factory2);

        assertEquals(1, registry.getAll().size());
        assertEquals(0, registry.get("text/html").size());
        assertEquals(1, registry.get("text/plain").size());
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
}
