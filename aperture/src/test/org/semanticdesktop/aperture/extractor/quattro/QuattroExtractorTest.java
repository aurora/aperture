/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.quattro;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class QuattroExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException {
		testExtraction(DOCS_PATH + "corel-quattro-pro-7.wb3");
		testExtraction(DOCS_PATH + "corel-quattro-pro-x3.qpw");
	}

	public void testExtraction(String resourceName) throws ExtractorException, IOException {
		// apply the extractor on an example file
		ExtractorFactory factory = new QuattroExtractorFactory();
		Extractor extractor = factory.get();
		SesameRDFContainer container = extract(resourceName, extractor);

		// check the extraction results
		checkStatement(AccessVocabulary.FULL_TEXT, "Quattro", container);
	}
}
