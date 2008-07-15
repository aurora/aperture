/*
 * Copyright (c) 2005 - 2008 Aduna.
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
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class PlainTextExtractorTest extends ExtractorTestBase {

	public void testRegularExtraction() throws ExtractorException, IOException {
		// apply the extractor on a text file
		RDFContainer container = getStatements(DOCS_PATH + "plain-text.txt");
		String text = container.getString(NIE.plainTextContent);
		assertTrue((text.indexOf("plain text") != -1));
        validate(container);
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
		RDFContainer container = getStatements(DOCS_PATH + fileName);
		String text = container.getString(NIE.plainTextContent);
		assertEquals("test", text);
        validate(container);
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
		RDFContainer container = getStatements(DOCS_PATH + fileName);
		assertEquals(null, container.getString(NIE.plainTextContent));
        validate(container);
		container.dispose();
	}

	private RDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
		// apply the extractor on a text file containing a null character
		ExtractorFactory factory = new PlainTextExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(resourceName, extractor);
		return container;
	}
}
