/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class PlainTextExtractorTest extends ExtractorTestBase {

	public void testRegularExtraction() throws ExtractorException, IOException {
		// apply the extractor on a text file
		RDF2GoRDFContainer container = getStatements(DOCS_PATH + "plain-text.txt");
		String text = container.getString(DATA.fullText);
		assertTrue((text.indexOf("plain text") != -1));
		container.dispose();
	}

	public void testUtfBOMHandling() throws ExtractorException, IOException {
		testContainsTestString("plain-text-ansi.txt");
		testContainsTestString("plain-text-utf8.txt");
		testContainsTestString("plain-text-utf16le.txt");
		testContainsTestString("plain-text-utf16be.txt");
	}

	private void testContainsTestString(String fileName) throws ExtractorException, IOException {
		// assert that the extracted text exactly equals "test" (i.e. not just contains "test"), so that we
		// are sure there are no garbage chars resulting from the presence of UTF Byte Order Marks
		RDF2GoRDFContainer container = getStatements(DOCS_PATH + fileName);
		String text = container.getString(DATA.fullText);
		assertEquals("test", text);
		container.dispose();
	}

	public void testFailingExtraction() throws ExtractorException, IOException {
		// apply the extractor on a text file containing a null char (i.e. that is not plain text) and make
		// sure that no text was extracted
		testFailingExtraction("plain-text-with-null-character.txt");

		// apply the extractor on an empty file and make sure that no text was extracted
		testFailingExtraction("plain-text-empty.txt");
	}

	public void testFailingExtraction(String fileName) throws ExtractorException, IOException {
		RDF2GoRDFContainer container = getStatements(DOCS_PATH + fileName);
		assertEquals(null, container.getString(DATA.fullText));
		container.dispose();
	}

	private RDF2GoRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
		// apply the extractor on a text file containing a null character
		ExtractorFactory factory = new PlainTextExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(resourceName, extractor);
		return container;
	}
}
