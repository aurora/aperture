/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDomainBoundaries extends ApertureTestBase {

    private DomainBoundaries boundaries;
    
    public void setUp() {
        boundaries = new DomainBoundaries();
    }

    public void tearDown() {
        boundaries = null;
    }
    
    public void testEmptyBoundaries() {
        assertTrue(boundaries.inDomain("http://example.com"));
    }
    
    public void testIncludePatterns() {
        boundaries.addIncludePattern(new SubstringPattern(".java", new SubstringCondition.EndsWith()));
        assertTrue(boundaries.inDomain("file:test.java"));
        assertFalse(boundaries.inDomain("file:test.doc"));
    }
    
    public void testExcludePatterns() {
        boundaries.addExcludePattern(new SubstringPattern(".java", new SubstringCondition.EndsWith()));
        assertFalse(boundaries.inDomain("file:test.java"));
        assertTrue(boundaries.inDomain("file:test.doc"));        
    }
    
    public void testBothPatterns() {
        boundaries.addIncludePattern(new SubstringPattern(".java", new SubstringCondition.EndsWith()));
        boundaries.addExcludePattern(new SubstringPattern("test", new SubstringCondition.Contains()));
        assertFalse(boundaries.inDomain("file:test.doc"));
        assertTrue(boundaries.inDomain("file:aperture.java"));
        assertFalse(boundaries.inDomain("file:test.java"));
        assertFalse(boundaries.inDomain("file:test/aperture.java"));
    }
}
