/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.presentations;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class PresentationsExtractorTest extends ExtractorTestBase {

	// tests only for full-text extraction
	public void testWordPerfectBasedExtraction() throws ExtractorException, IOException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		SesameRDFContainer container = extract(DOCS_PATH + "corel-presentations-3.0.shw", extractor);

		// check the extraction results
		checkStatement(AccessVocabulary.FULL_TEXT, "example", container);
	}

	// tests full-text and metadata extraction
	public void testOfficeBasedExtraction() throws ExtractorException, IOException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		SesameRDFContainer container = extract(DOCS_PATH + "corel-presentations-x3.shw", extractor);

		// check the extraction results
		checkStatement(AccessVocabulary.FULL_TEXT, "Presentation", container);
		checkStatement(AccessVocabulary.TITLE, "Example", container);
		checkStatement(AccessVocabulary.SUBJECT, "Testing", container);
		checkStatement(AccessVocabulary.DESCRIPTION, "comments", container);
		checkStatement(AccessVocabulary.CREATOR, "Fluit", container);
		checkStatement(AccessVocabulary.KEYWORD, "test", container);
		checkStatement(AccessVocabulary.KEYWORD, "rdf", container);
	}
}
