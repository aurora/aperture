/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.quattro;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class QuattroExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException, ModelException {
		testExtraction(DOCS_PATH + "corel-quattro-pro-7.wb3");
		testExtraction(DOCS_PATH + "corel-quattro-pro-x3.qpw");
	}

	public void testExtraction(String resourceName) throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new QuattroExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(resourceName, extractor);

		// check the extraction results
		checkStatement(DATA.fullText, "Quattro", container);
		
		container.dispose();
	}
}
