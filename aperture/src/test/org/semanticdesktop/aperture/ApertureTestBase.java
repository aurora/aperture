/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;

public class ApertureTestBase extends TestCase {

	public static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";

	public Model createModel() {
		try {
			return RDF2Go.getModelFactory().createModel();
		}
		catch (ModelException me) {
			return null;
		}
	}
    
    protected RDFContainer createRDFContainer(String uri) {
        return createRDFContainer(URIImpl.createURIWithoutChecking(uri));
    }
    
    protected RDFContainer createRDFContainer(URI uri) {
        Model newModel = createModel();
        return new RDF2GoRDFContainer(newModel,uri);
    }

	public void checkStatement(URI property, String substring, RDFContainer container) 
			throws ModelException {
		// setup some info
		String uriString = container.getDescribedUri().toString();
		Model model = container.getModel();
		ValueFactory valueFactory = container.getValueFactory();
		boolean encounteredSubstring = false;

		// loop over all statements that have the specified property uri as predicate
		ClosableIterable<? extends Statement> iterable = model.findStatements(valueFactory.createURI(uriString), property,
			Variable.ANY);
		ClosableIterator<? extends Statement> statements = iterable.iterator();
		try {
			while (statements.hasNext()) {
				// check the property type
				Statement statement = (Statement) statements.next();
				assertTrue(statement.getPredicate().equals(property));

				// see if it has a Literal containing the specified substring
				Node object = statement.getObject();
				if (object instanceof Literal) {
					String value = ((Literal) object).getValue();
					if (value.indexOf(substring) >= 0) {
						encounteredSubstring = true;
						break;
					}
				}
			}
		}
		finally {
			statements.close();
		}

		// see if any of the found properties contains the specified substring
		assertTrue(encounteredSubstring);
	}

	public void checkStatement(URI property, URI value, RDF2GoRDFContainer container) 
			throws ModelException {
		URI subject = container.getDescribedUri(); 
		checkStatement(subject, property, value, container);
	}

	public void checkStatement(URI subject, URI property, Node value, RDF2GoRDFContainer container) 
			throws ModelException {
		checkStatement(subject, property, value, container.getModel());
	}

	public void checkStatement(URI subject, URI property, Node value, Model model) throws ModelException {
		boolean encounteredValue = false;

		// loop over all statements that have the specified property uri as predicate
		ClosableIterable<? extends Statement> iterable = model.findStatements(subject,property,Variable.ANY);
		ClosableIterator<? extends Statement> statements = iterable.iterator();
		try {
			while (statements.hasNext()) {
				// check the property type
				Statement statement = (Statement) statements.next();
				assertTrue(statement.getPredicate().equals(property));

				// see if it has a Literal containing the specified substring
				Node object = statement.getObject();
				if (object.equals(value)) {
					encounteredValue = true;
					break;
				}
			}
		}
		finally {
			statements.close();
		}

		// see if any of the found properties contains the specified substring
		assertTrue(encounteredValue);
	}
}
