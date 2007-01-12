/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.rdf2go;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.rdf.ValueFactory;

public class RDF2GoValueFactory implements ValueFactory {

	private static final Logger LOGGER = Logger.getLogger(RDF2GoValueFactory.class.getName());

	private Model model;

	public RDF2GoValueFactory(Model model) {
		this.model = model;
	}

	public Literal createLiteral(String label) {
		try {
			return model.createPlainLiteral(label);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a plain literal from '" + label + "'", me);
			return null;
		}
	}

	public Literal createLiteral(String label, URI datatype) {
		try {
			return model.createDatatypeLiteral(label, datatype);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + label + "'", me);
			return null;
		}
	}

	public Literal createLiteral(boolean value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._boolean);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(long value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._long);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(int value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._integer);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(short value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._short);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(byte value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._byte);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(double value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._double);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Literal createLiteral(float value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._float);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public Statement createStatement(Resource subject, URI predicate, Node object) {
		return model.createStatement(subject, predicate, object); 
	}

	public URI createURI(String uri) {
		try {
			return model.createURI(uri);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Illegal URI: " + uri, e);
		}
	}
	
	public URI createURI(String namespaceUri, String localName) {
		return createURI(namespaceUri + "#" + localName);
	}

	public BlankNode createBlankNode() {
		return model.createBlankNode();
	}
}
