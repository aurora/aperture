/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.impl;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

public class TestSubCrawlerRegistryImpl extends TestCase {

    public void testBasics() {
        SubCrawlerRegistryImpl registry = new SubCrawlerRegistryImpl();
        SubCrawlerFactory factory1 = new DummyFactory("text/plain");
        SubCrawlerFactory factory2 = new DummyFactory("text/html");
        
        registry.add(factory1);
        registry.add(factory2);
        
        assertEquals(2, registry.getAll().size());
        assertEquals(1, registry.get("text/html").size());
        
        registry.remove(factory2);

        assertEquals(1, registry.getAll().size());
        assertEquals(0, registry.get("text/html").size());
        assertEquals(1, registry.get("text/plain").size());        
    }
    
    private static class DummyFactory implements SubCrawlerFactory {

        private String mimeType;
        
        public DummyFactory(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public SubCrawler get() {
            return null;
        }

        public Set getSupportedMimeTypes() {
            return Collections.singleton(mimeType);
        }
    }
    
    private static class DummySubCrawlerFactory implements SubCrawlerFactory {

        private String mimeType;
        
        public DummySubCrawlerFactory(String mimeType) {
            this.mimeType = mimeType;
        }

        public Set getSupportedMimeTypes() {
            return Collections.singleton(mimeType);
        }

        public SubCrawler get() {
            return null;
        }
    }
}
