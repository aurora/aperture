/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.CharArrayWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * Tests the methods from the XmlSafetyUtil class.
 */
public class XmlSafetyTest extends TestCase {

    public void testMakeXmlSafeString() {
        assertEquals(XmlSafetyUtils.makeXmlSafe("A\u0000nton\u0001i\u0002"), "Antoni");
        assertTrue(XmlSafetyUtils.makeXmlSafe("Antoni") == "Antoni");
    }

    private void assertArrayEquals(char[] a1, char[] a2) {
        assertTrue(a1.length == a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertTrue(a1[i] == a2[i]);
        }
    }

    public void testMakeXmlSafeCharArray() {
        char[] a1 = new char[] { 'A', 'n', 't', 'o', 'n', 'i' };
        assertTrue(XmlSafetyUtils.makeXmlSafe(a1) == a1);

        char[] a2 = new char[] { 'A', 'n', '\u0010', 't', 'o', '\u000B', 'n', 'i' };
        assertArrayEquals(XmlSafetyUtils.makeXmlSafe(a2), a1);
    }

    public void testXmlSafeWriter() throws Exception {
        CharArrayWriter caw = new CharArrayWriter();
        Writer xmlSafeWriter = XmlSafetyUtils.wrapXmlSafeWriter(caw);
        xmlSafeWriter.write("Antoni");
        assertTrue(caw.toCharArray().length == 6);

        xmlSafeWriter.write('\u0000');
        xmlSafeWriter.write('\u0001');
        xmlSafeWriter.write('\u0019');
        assertTrue(caw.toCharArray().length == 6);

        xmlSafeWriter.write(new char[] { 'A', 'p', 'e', 'r', 't', 'u', 'r', 'e' });
        xmlSafeWriter.write(new char[] { '\u0004', '\u0005'});
        assertTrue(caw.toCharArray().length == 14);
        xmlSafeWriter.write("xxApe\u0003rtur\u0015exx",2,10);
        assertTrue(caw.toCharArray().length == 22);

        assertArrayEquals(caw.toCharArray(), new char[] { 'A', 'n', 't', 'o', 'n', 'i', 'A', 'p', 'e', 'r',
                't', 'u', 'r', 'e', 'A', 'p', 'e', 'r', 't', 'u', 'r', 'e' });

    }
}
