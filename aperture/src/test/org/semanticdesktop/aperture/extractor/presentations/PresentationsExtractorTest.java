/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.presentations;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class PresentationsExtractorTest extends ExtractorTestBase {

	// tests only for full-text extraction
	public void testWordPerfectBasedExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(DOCS_PATH + "corel-presentations-3.0.shw", extractor);

		// check the extraction results
		checkStatement(NIE.plainTextContent, "example", container);
		validate(container);
		container.dispose();
	}

	// tests full-text and metadata extraction
	public void testOfficeBasedExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(DOCS_PATH + "corel-presentations-x3.shw", extractor);

		// check the extraction results
		checkStatement(NIE.plainTextContent, "Presentation", container);
		checkStatement(NIE.title, "Example", container);
		checkStatement(NIE.subject, "Testing", container);
		checkStatement(NIE.description, "comments", container);
		checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
		checkStatement(NIE.keyword, "test", container);
		checkStatement(NIE.keyword, "rdf", container);
		validate(container);
		container.dispose();
	}
}
