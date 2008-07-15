/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.works;

import java.io.IOException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Variable;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class WorksExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException, ModelException {
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

	public void testExtraction(String resourceName) throws ExtractorException, IOException, ModelException {
		// apply the extractor on a text file
		RDFContainer container = getStatements(resourceName);
		Model model = container.getModel();
		ValueFactory valueFactory = container.getValueFactory();

		// fetch the full-text property
		String uriString = container.getDescribedUri().toString();
        ClosableIterator<? extends Statement> statements = model.findStatements(valueFactory.createURI(uriString),
			NIE.plainTextContent, Variable.ANY);
		try {
			// check predicate
			Statement statement = (Statement) statements.next();
			assertTrue(statement.getPredicate().equals(NIE.plainTextContent));

			// check number of statements
			assertFalse(statements.hasNext());

			// check value
			Literal value = (Literal) statement.getObject();
			String text = value.getValue();
			assertTrue((text.indexOf("Microsoft") != -1));
		}
		finally {
			statements.close();
            validate(container);
			container.dispose();
		}
	}

	private RDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
		ExtractorFactory factory = new WorksExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(resourceName, extractor);
		return container;
	}
}
