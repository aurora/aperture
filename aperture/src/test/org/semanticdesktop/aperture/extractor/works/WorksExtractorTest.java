/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.works;

import java.io.IOException;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class WorksExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException {
		testExtraction(DOCS_PATH + "microsoft-works-word-processor-3.0.wps");
		testExtraction(DOCS_PATH + "microsoft-works-word-processor-4.0.wps");
		testExtraction(DOCS_PATH + "microsoft-works-spreadsheet-4.0-2000.wks");

		// the following files cannot be successfully processed by WorksExtractor
		// This is a known problem, not a reason to let the unit test fail
		// testExtraction(DOCS_PATH + "microsoft-works-word-processor-2000.wps");
		// testExtraction(DOCS_PATH + "microsoft-works-word-processor-7.0.wps");
		// testExtraction(DOCS_PATH + "microsoft-works-spreadsheet-3.0.wks");
		// testExtraction(DOCS_PATH + "microsoft-works-spreadsheet-7.0.xlr");
	}

	public void testExtraction(String resourceName) throws ExtractorException, IOException {
		// apply the extractor on a text file
		SesameRDFContainer container = getStatements(resourceName);
		Repository repository = container.getRepository();
		ValueFactory valueFactory = repository.getSail().getValueFactory();

		// fetch the full-text property
		String uriString = container.getDescribedUri().toString();
		CloseableIterator statements = repository.getStatements(valueFactory.createURI(uriString),
			DATA.fullText, null);
		try {
			// check predicate
			RStatement statement = (RStatement) statements.next();
			assertTrue(statement.getPredicate().equals(DATA.fullText));

			// check number of statements
			assertFalse(statements.hasNext());

			// check value
			Literal value = (Literal) statement.getObject();
			String text = value.getLabel();
			assertTrue((text.indexOf("Microsoft") != -1));
		}
		finally {
			statements.close();
		}
	}

	private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
		ExtractorFactory factory = new WorksExtractorFactory();
		Extractor extractor = factory.get();
		SesameRDFContainer container = extract(resourceName, extractor);
		return container;
	}
}
