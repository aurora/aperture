/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestUrlPatterns extends ApertureTestBase {

    public void testSubstringPatterns() {
        SubstringPattern pattern = new SubstringPattern("test", SubstringCondition.STARTS_WITH);
        assertTrue(pattern.matches("test.java"));
        assertFalse(pattern.matches("java.test"));
        
        pattern.setCondition(SubstringCondition.ENDS_WITH);
        assertTrue(pattern.matches("java.test"));
        assertFalse(pattern.matches("test.java"));
        
        pattern.setCondition(SubstringCondition.CONTAINS);
        assertTrue(pattern.matches("java-test.doc"));
        assertFalse(pattern.matches("java-src.doc"));

        pattern.setCondition(SubstringCondition.DOES_NOT_CONTAIN);
        assertFalse(pattern.matches("java-test.doc"));
        assertTrue(pattern.matches("java-src.doc"));
    }
    
    public void testRegExpPattern() {
        RegExpPattern pattern = new RegExpPattern(".*/CVS/.*");
        assertTrue(pattern.matches("src/CVS/Entries"));
        assertFalse(pattern.matches("src/test.java"));
    }
}
