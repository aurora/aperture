/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import junit.framework.TestCase;

import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Tests for the {@link UriUtil} class
 */
public class UriUtilTest extends TestCase {
    
    /**
     * Tests the {@link UriUtil#getLastPathElement} method
     */
    public void testGetLastPathElement() {
        assertName("file.txt",
            "file://localhost/folder/file.txt");
        assertName("file.txt",
            "zip:tar:gz:file://localhost/folder/file.tar.gz!/file.tar!/archive.zip!/subfolder/file.txt");
        assertName("file.txt",
            "zip:tar:gz:file://localhost/folder/file.tar.gz!/file.tar!/archive.zip!/subfolder/file.txt#fragmentId");
    }
    
    private void assertName(String name, String uri) {
        assertEquals(name,UriUtil.getFileName(new URIImpl(uri)));
    }
}

