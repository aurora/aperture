/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.impl;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.opener.email.EmailOpenerFactory;

public class TestDataOpenerRegistryImpl extends TestCase {

    public void testBasics() {
        DataOpenerRegistryImpl registry = new DataOpenerRegistryImpl();
        DataOpenerFactory factory1 = new DummyFactory("file");
        DataOpenerFactory factory2 = new DummyFactory("http");
        DataOpenerFactory factory3 = new DummyFactory("email");
        registry.add(factory1);
        registry.add(factory2);
        registry.add(factory3);
        
        assertEquals(3, registry.getAll().size());
        assertEquals(1, registry.get("file").size());
        
        registry.remove(factory2);

        assertEquals(2, registry.getAll().size());
        assertEquals(0, registry.get("http").size());
        assertEquals(1, registry.get("file").size());
        assertEquals(1, registry.get("email").size());
      
    }
    
    public void testEmailFactory(){
        DataOpenerRegistryImpl registry = new DataOpenerRegistryImpl();
        EmailOpenerFactory eof = new EmailOpenerFactory();
        registry.add(eof);
        assertEquals(1, registry.get("email").size());
        assertEquals(1, registry.get("imap").size());
        assertEquals(1, registry.get("msgid").size());
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
