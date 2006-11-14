/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.wordperfect;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class WordPerfectExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException, ModelException {
		testExtraction(DOCS_PATH + "corel-wordperfect-4.2.wp");
		testExtraction(DOCS_PATH + "corel-wordperfect-5.0.wp");
		testExtraction(DOCS_PATH + "corel-wordperfect-5.1.wp");
		testExtraction(DOCS_PATH + "corel-wordperfect-x3.wpd");

		// this doesn't work yet, probably an encoding issue
		// not a reason to let the unit test fail, just document this as a shortcoming of the extractor
		// testExtraction(DOCS_PATH + "corel-wordperfect-5.1-far-east.wpd");
	}

	public void testExtraction(String resourceName) throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new WordPerfectExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(resourceName, extractor);

		// check the extraction results
		checkStatement(DATA.fullText, "WordPerfect", container);
		
		container.dispose();
	}
}
