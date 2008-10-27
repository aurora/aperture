/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.StringReader;
import java.io.Writer;

import junit.framework.TestCase;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.LanguageTagLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.vocabulary.XSD;

/**
 * Tests the methods from the XmlSafetyUtil class.
 */
public class XmlSafetyTest extends TestCase {

    /**
     * Tests the makeXmlSafe(string) method
     */
    public void testMakeXmlSafeString() {
        assertEquals(XmlSafetyUtils.makeXmlSafe("A\u0000nton\u0001i\u0002"), "A nton i ");
        assertTrue(XmlSafetyUtils.makeXmlSafe("Antoni") == "Antoni");
    }

    private void assertArrayEquals(char[] a1, char[] a2) {
        assertTrue(a1.length == a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertTrue(a1[i] == a2[i]);
        }
    }

    /**
     * Tests the makeXmlSafe(char [] array) method.
     */
    public void testMakeXmlSafeCharArray() {
        char[] a1 = new char[] { 'A', 'n', 't', 'o', 'n', 'i' };
        assertTrue(XmlSafetyUtils.makeXmlSafe(a1) == a1);

        char[] a2 = new char[] { 'A', 'n', '\u0010', 't', 'o', '\u000B', 'n', 'i' };
        char[] a3 = new char[] { 'A', 'n', ' ', 't', 'o', ' ', 'n', 'i' };
        assertArrayEquals(XmlSafetyUtils.makeXmlSafe(a2), a3);
    }
    
    /**
     * Tests the {@link XmlSafetyUtils#makeXmlSafe(Model, org.ontoware.rdf2go.model.node.Node)} method.
     */
    public void testMakeXmlSafeNode() {
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        URI uri = model.createURI("http://example.org/uri");
        assertTrue(uri == XmlSafetyUtils.makeXmlSafe(model, uri));
        BlankNode bnode = model.createBlankNode();
        assertTrue(bnode == XmlSafetyUtils.makeXmlSafe(model, bnode));
        
        PlainLiteral plainLiteralOk = new PlainLiteralImpl("Some plain literal");
        PlainLiteral plainLiteralBad = new PlainLiteralImpl("Bad \u0000\u0019literal\u0008");
        assertTrue(plainLiteralOk == XmlSafetyUtils.makeXmlSafe(model, plainLiteralOk));
        // this line checks if the type is ok, if not, we get a ClassCastException
        PlainLiteral plainLiteralBadFixed = (PlainLiteral)XmlSafetyUtils.makeXmlSafe(model, plainLiteralBad);
        assertTrue(plainLiteralBad != plainLiteralBadFixed);
        assertEquals("Bad   literal ",plainLiteralBadFixed.getValue());
        
        DatatypeLiteral ddLiteralOk = new DatatypeLiteralImpl("Some datatype literal",XSD._string);
        DatatypeLiteral ddLiteralBad = new DatatypeLiteralImpl("Bad \u0000\u0019literal\u0008", XSD._string);
        assertTrue(ddLiteralOk == XmlSafetyUtils.makeXmlSafe(model,ddLiteralOk));
        // this line checks if the type is ok, if not, we get a ClassCastException
        DatatypeLiteral ddLiteralBadFixed = (DatatypeLiteral)XmlSafetyUtils.makeXmlSafe(model, ddLiteralBad);
        assertTrue(ddLiteralBad != ddLiteralBadFixed);
        assertEquals("Bad   literal ",ddLiteralBadFixed.getValue());
        
        LanguageTagLiteral ltLiteralOk = new LanguageTagLiteralImpl("Some plain literal","en");
        LanguageTagLiteral ltLiteralBad = new LanguageTagLiteralImpl("Bad \u0000\u0019literal\u0008","en");
        assertTrue(ltLiteralOk == XmlSafetyUtils.makeXmlSafe(model, ltLiteralOk));
        // this line checks if the type is ok, if not, we get a ClassCastException
        LanguageTagLiteral ltLiteralBadFixed = (LanguageTagLiteral)XmlSafetyUtils.makeXmlSafe(model, ltLiteralBad);
        assertTrue(ltLiteralBad != ltLiteralBadFixed);
        assertEquals("Bad   literal ",ltLiteralBadFixed.getValue());
        
    }

    /**
     * Tests the XmlSafeWriter
     * @throws Exception
     */
    public void testXmlSafeWriter() throws Exception {
        CharArrayWriter caw = new CharArrayWriter();
        Writer xmlSafeWriter = XmlSafetyUtils.wrapXmlSafeWriter(caw);
        xmlSafeWriter.write("Antoni");
        assertTrue(caw.toCharArray().length == 6);

        xmlSafeWriter.write('\u0000');
        xmlSafeWriter.write('\u0001');
        xmlSafeWriter.write('\u0019');
        assertTrue(caw.toCharArray().length == 9);

        xmlSafeWriter.write(new char[] { 'A', 'p', 'e', 'r', 't', 'u', 'r', 'e' });
        xmlSafeWriter.write(new char[] { '\u0004', '\u0005'});
        assertTrue(caw.toCharArray().length == 19);
        xmlSafeWriter.write("xxApe\u0003rtur\u0015exx",2,10);
        assertTrue(caw.toCharArray().length == 29);

        assertArrayEquals(caw.toCharArray(),
            new char[] { 'A', 'n', 't', 'o', 'n', 'i', ' ', ' ', ' ', 'A', 'p', 'e', 'r', 't', 'u', 'r', 'e',
                    ' ', ' ', 'A', 'p', 'e', ' ', 'r', 't', 'u', 'r', ' ', 'e' });
    }
    
    /**
     * Tests the xml safe reader
     * @throws Exception
     */
    public void testXmlSafeReader() throws Exception {
        String testString = "This \u0005is\u0006 a faulty\u0015string";
        char[] testArray = new char[] { 'T', 'h', 'i', 's', ' ', '\u0005', 'i', 's', '\u0006', ' ', 'a', ' ',
                'f', 'a', 'u', 'l', 't', 'y', '\u0015', 's', 't', 'r', 'i', 'n', 'g' };
        StringReader sr = new StringReader(testString);
        CharArrayReader car = new CharArrayReader(testArray);
        
        String s1 = IOUtil.readString(XmlSafetyUtils.wrapXmlSafeReader(sr));
        String s2 = IOUtil.readString(XmlSafetyUtils.wrapXmlSafeReader(car));
        
        assertEquals("This  is  a faulty string",s1);
        assertEquals("This  is  a faulty string",s2);
        assertEquals(s2,s1);
    }
}
