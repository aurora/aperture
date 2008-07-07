/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import info.aduna.xml.XMLUtil;

import java.io.CharArrayWriter;
import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;

import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.aperture.rdf.RDFContainer;

import com.sun.xml.internal.ws.util.xml.XmlUtil;


public class XmlSafetyUtils {
    
    /**
     * Strips all invalid XML Character data chars from the given string. If all characters are valid
     * character data chars, this method will return the same String.
     * 
     * @param in the input string
     * @return the input string with all characters that are invalid in xml removed
     */
    public static String makeXmlSafe(String in) {
        return XMLUtil.removeInvalidCharacterDataChars(in);
    }
    
    public static String makeXmlSafe(String in, int off, int len) {
        if (off == 0 && len == in.length()) {
            return makeXmlSafe(in);
        } 
        StringBuilder buffer = new StringBuilder(len);
        for (int i = off; i <= len+1; i++) {
            char c = in.charAt(i);
            if (XMLUtil.isValidCharacterDataChar(c)) {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }
    
    /**
     * Removes all non-valid XML character data chars from the specified char array. If all characters are
     * valid character data chars, this method will return the same array. If the method encounters any
     * invalid characters it will return a new array whose contents will be copied from s, with the
     * invalid characters ommited (e.g. the length will be equal to s.length - n where n is the number
     * of XML-invalid characters).
     */
    public static char[] makeXmlSafe(char [] s) {
        return makeXmlSafe(s, 0, s.length);
    }
    
    /**
     * Removes all non-valid XML character data chars from the specified char array. If all characters are
     * valid character data chars, this method will return the same array. If the method encounters any
     * invalid characters it will return a portion cut from the input array, begginining with the offset
     * off, whose length will be equal to (len - n) where n is the number of XML-invalid characters.
     */
    public static char[] makeXmlSafe(char [] s, int off, int len) {
        // first check if there are any invalid chars
        boolean hasInvalidChars = false;

        int length = s.length;
        for (int i = off; i < len; i++) {
            if (!XMLUtil.isValidCharacterDataChar(s[i])) {
                hasInvalidChars = true;
                break;
            }
        }

        if (hasInvalidChars) {
            CharArrayWriter buffer = new CharArrayWriter();
            for (int i = off; i < len; i++) {
                char c = s[i];
                if (XMLUtil.isValidCharacterDataChar(c)) {
                    buffer.append(c);
                }
            }

            return buffer.toCharArray();
        }
        else {
            return s;
        }
    }
    
    public static Writer wrapXmlSafeWriter(Writer writer) {
        return new XmlSafeWriter(writer);
    }
    
    public static OutputStream wrapXmlSafeOutputStream(OutputStream ostream) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static Model wrapXmlSafeModel(Model model) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public static RDFContainer wrapXmlSafeRDFContainer(RDFContainer container) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public static org.semanticdesktop.aperture.accessor.RDFContainerFactory 
        wrapXmlSafeAccessorRDFContainerFactory(
        org.semanticdesktop.aperture.accessor.RDFContainerFactory factory) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public static org.semanticdesktop.aperture.rdf.RDFContainerFactory
        wrapXmlSafeRDFContainerFactory(
        org.semanticdesktop.aperture.rdf.RDFContainerFactory factory) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

class XmlSafeWriter extends FilterWriter {
    protected XmlSafeWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] ncbuf = XmlSafetyUtils.makeXmlSafe(cbuf, off, len);
        if (ncbuf == cbuf) {
            super.write(cbuf, off, len);
        } else {
            super.write(ncbuf,0,ncbuf.length);
        }
    }

    @Override
    public void write(int c) throws IOException {
        if (XMLUtil.isValidCharacterDataChar(c)) {
            super.write(c);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        String nstr = XmlSafetyUtils.makeXmlSafe(str,off,len);
        if (nstr == str) {
            super.write(str, off, len);
        } else {
            super.write(nstr,0,nstr.length());
        }
    }  
}

class XmlSafeOutputStream extends FilterOutputStream {
    public XmlSafeOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
    }
}
