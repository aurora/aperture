/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.impl;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;

public class TestDataOpenerRegistryImpl extends TestCase {

    public void testBasics() {
        DataOpenerRegistryImpl registry = new DataOpenerRegistryImpl();
        DataOpenerFactory factory1 = new DummyFactory("file");
        DataOpenerFactory factory2 = new DummyFactory("http");
        registry.add(factory1);
        registry.add(factory2);
        
        assertEquals(2, registry.getAll().size());
        assertEquals(1, registry.get("file").size());
        
        registry.remove(factory2);

        assertEquals(1, registry.getAll().size());
        assertEquals(0, registry.get("http").size());
        assertEquals(1, registry.get("file").size());
    }
    
    private static class DummyFactory implements DataOpenerFactory {

        private String scheme;
        
        public DummyFactory(String scheme) {
            this.scheme = scheme;
        }
        
        public DataOpener get() {
            return null;
        }

        public Set getSupportedSchemes() {
            return Collections.singleton(scheme);
        }
    }
}
