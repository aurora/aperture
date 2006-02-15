/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class TestMagicMimeTypeIdentifier extends ApertureTestBase {

    public void testXmlFileParsing() {
        // test whether the Java class file description contains the right byte sequence (ca fe ba be)
        byte[] requiredBytes = new byte[] { (byte) 202, (byte) 254, (byte) 186, (byte) 190 };

        MagicMimeTypeIdentifierFactory factory = new MagicMimeTypeIdentifierFactory();
        MagicMimeTypeIdentifier identifier = (MagicMimeTypeIdentifier) factory.get();

        ArrayList descriptions = identifier.getMimeTypeDescriptions();
        int nrDescriptions = descriptions.size();

        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) descriptions.get(i);

            if ("application/x-java-class".equals(description.getMimeType())) {
                ArrayList magicNumbers = description.getMagicNumbers();
                MagicNumber number = (MagicNumber) magicNumbers.get(0);
                byte[] magicBytes = number.getMagicBytes();

                assertEquals(requiredBytes.length, magicBytes.length);

                for (int j = 0; j < requiredBytes.length; j++) {
                    assertEquals(requiredBytes[j], magicBytes[j]);
                }

                return;
            }
        }

        // java class mime type description not encountered!
        fail();
    }

    public void testIdentification() throws IOException {
        MagicMimeTypeIdentifierFactory factory = new MagicMimeTypeIdentifierFactory();
        MimeTypeIdentifier identifier = factory.get();

        // some regular checks
        checkMimeType("plain-text.txt", "text/plain", identifier);
        checkMimeType("html-handwritten.html", "text/html", identifier);
        checkMimeType("pdf-openoffice-2.0-writer.pdf", "application/pdf", identifier);
        checkMimeType("openoffice-2.0-writer.odt", "application/vnd.oasis.opendocument.text", identifier);
        checkMimeType("openoffice-1.1.5-writer.sxw", "application/vnd.sun.xml.writer", identifier);
        checkMimeType("staroffice-5.2-writer.sdw", "application/vnd.stardivision.writer", identifier);
        checkMimeType("microsoft-word-2000.doc", "application/vnd.ms-word", identifier);
        checkMimeType("microsoft-works-word-processor-7.0.wps", "application/vnd.ms-works", identifier);

        // try throwing some confusing stuff at it, e.g. files with wrong extensions (lacking a
        // Word-specific magic number, it should still be able to see that the word document
        // is an MS office document)
        checkMimeType("microsoft-word-2000-with-wrong-file-extension.pdf", "application/vnd.ms-office", identifier);
        checkMimeType("html-handwritten-with-wrong-file-extension.txt", "text/html", identifier);
    }

    private void checkMimeType(String resourceName, String mimeType, MimeTypeIdentifier identifier) throws IOException {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + resourceName);
        byte[] bytes = IOUtil.readBytes(stream, identifier.getMinArrayLength());
        String determinedType = identifier.identify(bytes, resourceName, null);
        assertEquals(mimeType, determinedType);
    }
}
