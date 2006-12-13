/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;

/**
 * This class contains utility methods that facilitate the generation of various values from an RDF2Go
 * model. The interface is deliberately similar to that of a ValueFactory, but this class can be used
 * in situations when the code has to work directly with a model, without it being wrapped in an RDFContainer.
 */
public class ModelUtil {
	
	private static final Logger LOGGER = Logger.getLogger(ModelUtil.class.getName());
	
	public static Literal createLiteral(Model model, String label) {
		try {
			return model.createPlainLiteral(label);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a plain literal from '" + label + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, String label, URI datatype) {
		try {
			return model.createDatatypeLiteral(label, datatype);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + label + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, boolean value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._boolean);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, long value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._long);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, int value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._integer);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, short value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._short);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, byte value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._byte);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, double value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._double);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Literal createLiteral(Model model, float value) {
		try {
			return model.createDatatypeLiteral(String.valueOf(value), XSD._float);
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't create a datatype literal '" + value + "'", me);
			return null;
		}
	}

	public static Statement createStatement(Model model, Resource subject, URI predicate, Node object) {
		return new StatementImpl(model, subject, predicate, object);
	}

	public static URI createURI(Model model, String uri) {
		try {
			return model.createURI(uri);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Illegal URI: " + uri, e);
		}
	}
	
	public static URI createURI(Model model, String namespaceUri, String localName) {
		return createURI(model, namespaceUri + "#" + localName);
	}

	public static BlankNode createBlankNode(Model model) {
		return model.createBlankNode();
	}
	
	/** 
	 * 
	 * @param node the starting node
	 * @param model the model
	 * @param backwards - if this is true we will also traverse nodes (null,null,RESOURCE)
	 * @return a list of statements
	 */
	public static List<Statement> getCBD(Resource node, Model model, boolean backwards) {
		List<Statement> res;
		res = new Vector<Statement>();
		ClosableIterator<? extends Statement> i = null;
		try {
			ClosableIterable<? extends Statement> iterable = model.findStatements(node, Variable.ANY, Variable.ANY);
			i = iterable.iterator();
			while (i.hasNext()) {
				Statement s = i.next();
				res.add(s);
				if (s.getObject() instanceof BlankNode) 
					res.addAll(getCBD( (Resource) s.getObject(),model,backwards));
			}
			i.close();
			
			if (backwards) {
				iterable = model.findStatements(Variable.ANY, Variable.ANY, node);
				i = iterable.iterator();
				while (i.hasNext()) {
					Statement s = i.next();
					res.add(s);
					if (s.getSubject() instanceof BlankNode) 
						res.addAll(getCBD( (Resource) s.getObject(),model,backwards));
				}
				i.close();
			}
		} catch (ModelException me) {
			LOGGER.log(Level.SEVERE,"Couldn't get CBD",me);
			return null;
		} finally {
			if ( i != null) {
				i.close();
			}
		}
		
			
		return res;
	}

	public static boolean hasStatement(Model model, Resource subject, URI predicate, Node object) {
		boolean result = false;
		ClosableIterator<? extends Statement> iterator = null;
		try {
			ClosableIterable<? extends Statement> iterable = model.findStatements(subject, predicate, object);
			iterator = iterable.iterator();
			if (iterator.hasNext()) {
				result = true;
			} else {
				result = false;
			}
		} catch (ModelException me) {
			LOGGER.log(Level.SEVERE,"Couldn't determine if the given statement is in the model",me);
			throw new RuntimeException(me);
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}
}

