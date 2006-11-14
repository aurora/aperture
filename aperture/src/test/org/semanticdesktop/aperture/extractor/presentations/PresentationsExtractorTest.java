/*
 * Copyright (c) 2006 Aduna.
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
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class PresentationsExtractorTest extends ExtractorTestBase {

	// tests only for full-text extraction
	public void testWordPerfectBasedExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(DOCS_PATH + "corel-presentations-3.0.shw", extractor);

		// check the extraction results
		checkStatement(DATA.fullText, "example", container);
		
		container.dispose();
	}

	// tests full-text and metadata extraction
	public void testOfficeBasedExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new PresentationsExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(DOCS_PATH + "corel-presentations-x3.shw", extractor);

		// check the extraction results
		checkStatement(DATA.fullText, "Presentation", container);
		checkStatement(DATA.title, "Example", container);
		checkStatement(DATA.subject, "Testing", container);
		checkStatement(DATA.description, "comments", container);
		checkStatement(DATA.creator, "Fluit", container);
		checkStatement(DATA.keyword, "test", container);
		checkStatement(DATA.keyword, "rdf", container);
		
		container.dispose();
	}
}
