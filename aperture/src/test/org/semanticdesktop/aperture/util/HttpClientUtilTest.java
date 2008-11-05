/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import junit.framework.TestCase;

/**
 * Some tests for the {@link HttpClientUtil} class.
 */
public class HttpClientUtilTest extends TestCase {

    public void testFormUrlEncode() {
        testEncode("Antoni+My%C5%82ka", "Antoni My\u0142ka");
        testEncode("Antoni+My%C5%82%C5%82ka", "Antoni My\u0142\u0142ka");
        testEncode("/D_/Installers/installer+2005.1+rc1/icon-16x16.gif",
            "/D_/Installers/installer 2005.1 rc1/icon-16x16.gif");

        // %6F is a small letter o
        // a single character from the basic ASCII set
        // %C5%82 is a polish letter l with a stroke
        // the codepoint is \u0142, utf-8 c5 82
        // %EF%BA%BA is an arabic letter SAD FINAL form http://www.utf8-chartable.de/unicode-utf8-table.pl
        // the codepoint is \ufeba, the utf-8 represenation is ef ba ba
        // this tests if the encode method can operate with, two-byte and three-byte escape sequences
        testEncode("Antoni+My%C5%82kfd%EF%BA%BAfa", "Antoni My\u0142kfd\ufebafa");
    }

    private void testEncode(String encoded, String plain) {
        assertEquals(encoded, HttpClientUtil.formUrlEncode(plain, "/"));
    }

    /**
     * Tests whether some basic cases of url-encoded strings are corectly decoded.
     */
    public void testFormUrlDecode() {
        testDecode("Antoni My\u0142ka", "Antoni+My%C5%82ka");// a space and a polish diacritic mark
        testDecode("/D_/Installers/installer 2005.1 rc1/icon-16x16.gif",
            "/D_/Installers/installer+2005.1+rc1/icon-16x16.gif");
        testDecode("Antoni My\u0142\u0142ka", "Antoni+My%C5%82%C5%82ka");// two diacritic marks one after
                                                                         // another
        // // %6F is a small letter o
        // a single character from the basic ASCII set
        // %C5%82 is a polish letter l with a stroke
        // the codepoint is \u0142, utf-8 c5 82
        // %EF%BA%BA is an arabic letter SAD FINAL form http://www.utf8-chartable.de/unicode-utf8-table.pl
        // the codepoint is \ufeba, the utf-8 represenation is ef ba ba
        // this tests if the decode method can operate with one-byte, two-byte and three-byte escape sequences
        testDecode("Antoni My\u0142kfd\ufebafa", "Ant%6Fni+My%C5%82kfd%EF%BA%BAfa");

    }

    private void testDecode(String decoded, String encoded) {
        assertEquals(decoded, HttpClientUtil.formUrlDecode(encoded));
    }
}
