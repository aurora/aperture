/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class TestMagicMimeTypeIdentifier extends TestCase {

    protected static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";
    
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
        checkMimeType("plain-text-without-extension", "text/plain", identifier);
		checkMimeType("html-handwritten.html", "text/html", identifier);
		checkMimeType("xml-handwritten.xml", "text/xml", identifier);
		checkMimeType("rtf-word-2000.rtf", "text/rtf", identifier);
		checkMimeType("pdf-openoffice-2.0-writer.pdf", "application/pdf", identifier);
		checkMimeType("mail-thunderbird-1.5.eml", "message/rfc822", identifier);
		checkMimeType("openoffice-2.0-writer.odt", "application/vnd.oasis.opendocument.text", identifier);
		checkMimeType("openoffice-1.1.5-writer.sxw", "application/vnd.sun.xml.writer", identifier);
		checkMimeType("staroffice-5.2-writer.sdw", "application/vnd.stardivision.writer", identifier);
		checkMimeType("microsoft-word-2000.doc", "application/vnd.ms-word", identifier);
		checkMimeType("microsoft-excel-2000.xls", "application/vnd.ms-excel", identifier);
		checkMimeType("microsoft-powerpoint-2000.ppt", "application/vnd.ms-powerpoint", identifier);
		checkMimeType("microsoft-visio.vsd", "application/vnd.visio", identifier);
		checkMimeType("microsoft-publisher-2003.pub", "application/x-mspublisher", identifier);
		checkMimeType("microsoft-works-word-processor-7.0.wps", "application/vnd.ms-works", identifier);
		checkMimeType("microsoft-word-2007beta2.docm", "application/vnd.openxmlformats-officedocument.wordprocessingml", identifier);
		checkMimeType("microsoft-word-2007beta2.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml", identifier);
		checkMimeType("microsoft-word-2007beta2.dotm", "application/vnd.openxmlformats-officedocument.wordprocessingml", identifier);
		checkMimeType("microsoft-word-2007beta2.dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xlam", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xlsb", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xltm", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-excel-2007beta2.xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.potm", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.potx", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.ppsm", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.ppsx", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.pptm", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("microsoft-powerpoint-2007beta2.pptx", "application/vnd.openxmlformats-officedocument.presentationml", identifier);
		checkMimeType("tar-test.tar", "application/x-tar", identifier);
		
		// try throwing some confusing stuff at it, e.g. files with wrong extensions
		checkMimeType("microsoft-word-2000-with-wrong-file-extension.pdf", "application/vnd.ms-office",
			identifier);
		checkMimeType("html-handwritten-with-wrong-file-extension.txt", "text/html", identifier);
        checkMimeType("html-mixed-case-header-and-wrong-extension.txt", "text/html", identifier);

		// The ultimate test: a HTML file using UTF-16 that starts with white space and has a non-HTML file
		// extension. Regular magic number checking would fail due to the UTF-16 Byte Order Mark and the 2
		// byte encoding of the individual chars, meaning that it must use the magic string heuristic to
		// correctly identify this file.
		checkMimeType("html-utf16-leading-whitespace-wrong-extension.doc", "text/html", identifier);

		// one more crucial test: check that an xml file with a UTF-8 BOM and a missing file extension is
		// classified correctly
		checkMimeType("xml-utf8-bom", "text/xml", identifier);
	}
	
	/**
	 * Tests whether the crawler can correctly extract the file name from the subcrawled uri. The docx files
	 * are normal zip archives, therefore the magic number test indicates that they are zip files. To correctly
	 * identify the file as Office 2007 docx, the mime type identifier needs to take the extension into account
	 * which means that it has to extract the correct file name.
	 * 
	 * @throws Exception
	 */
	public void testSubCrawledUri() throws Exception {
	    MagicMimeTypeIdentifierFactory factory = new MagicMimeTypeIdentifierFactory();
        MimeTypeIdentifier identifier = factory.get();
	    InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "microsoft-word-2007beta2.docx",this.getClass());
	    byte[] bytes = IOUtil.readBytes(stream, identifier.getMinArrayLength());
	    String uri = "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx";
	    String determinedType = identifier.identify(bytes, null, new URIImpl(uri));
	    assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml", determinedType);
	}

	private void checkMimeType(String resourceName, String mimeType, MimeTypeIdentifier identifier)
			throws IOException {
		InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + resourceName,TestMagicMimeTypeIdentifier.class);
		byte[] bytes = IOUtil.readBytes(stream, identifier.getMinArrayLength());
		String determinedType = identifier.identify(bytes, resourceName, null);
		assertEquals(mimeType, determinedType);
	}
	
	public void testNullArray() {
		MagicMimeTypeIdentifierFactory factory = new MagicMimeTypeIdentifierFactory();
		MimeTypeIdentifier identifier = factory.get();
		
		String fileType = identifier.identify(null, "test.txt", null);
		assertEquals("text/plain", fileType);
		
		String uriType = identifier.identify(null, null, URIImpl.createURIWithoutChecking("file:test.html"));
		assertEquals("text/html", uriType);
	}
}
