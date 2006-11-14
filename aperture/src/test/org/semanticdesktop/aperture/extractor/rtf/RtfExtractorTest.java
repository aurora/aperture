/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.io.IOException;

import org.ontoware.aifbcommons.collection.ClosableIterable;
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
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class RtfExtractorTest extends ExtractorTestBase {

	public void testRegularExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on a text file
		RDF2GoRDFContainer container = getStatements(DOCS_PATH + "rtf-word-2000.rtf");
		Model model = container.getModel();
		ValueFactory valueFactory = container.getValueFactory();

		// fetch the full-text property
		String uriString = container.getDescribedUri().toString();
		ClosableIterable<Statement> iterable = model.findStatements(valueFactory.createURI(uriString),
			DATA.fullText, Variable.ANY);
		ClosableIterator<Statement> statements = iterable.iterator();
		try {
			// check predicate
			Statement statement = (Statement) statements.next();
			assertTrue(statement.getPredicate().equals(DATA.fullText));

			// check number of statements
			assertFalse(statements.hasNext());

			// check value
			Literal value = (Literal) statement.getObject();
			String text = value.getValue();
			assertTrue((text.indexOf("RTF") != -1));
		}
		finally {
			statements.close();
			container.dispose();
		}
	}

	private RDF2GoRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
		// apply the extractor on a text file containing a null character
		ExtractorFactory factory = new RtfExtractorFactory();
		Extractor extractor = factory.get();
		RDF2GoRDFContainer container = extract(resourceName, extractor);
		return container;
	}
}
