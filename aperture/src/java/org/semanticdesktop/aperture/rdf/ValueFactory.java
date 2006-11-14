/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

/**
 * Creates Literals, Uris, Blank nodes and statements
 */
public interface ValueFactory {	
	
	/**
	 * Creates a new URI from the supplied string-representation.
	 * 
	 * @param uri
	 *        A string-representation of a URI.
	 * @return An object representing the URI.
	 * @throws IlllegalArgumentException
	 *         If the supplied string does not resolve to a legal (absolute) URI.
	 */
	public URI createURI(String uri);
	
	/**
	 * Creates a new URI from the namespace uri and local name;
	 * 
	 * @param namespaceUri
	 *        A string-representation of a namespace URI.
	 * @param localName
	 * 		  A string representation of the local name
	 * @return An object representing the URI.
	 * @throws IlllegalArgumentException
	 *         If the supplied string does not resolve to a legal (absolute) URI.
	 */
	public URI createURI(String namespaceUri, String localName);

	/**
	 * Creates a new bNode.
	 * 
	 * @return An object representing the bNode.
	 */
	public BlankNode createBlankNode();

	/**
	 * Creates a new literal with the supplied label.
	 * 
	 * @param label
	 *        The literal's label.
	 */
	public Literal createLiteral(String label);

	/**
	 * Creates a new literal with the supplied label and datatype.
	 * 
	 * @param label
	 *        The literal's label.
	 * @param datatype
	 *        The literal's datatype, or <tt>null</tt> if the literal doesn't
	 *        have a datatype.
	 */
	public Literal createLiteral(String label, URI datatype);

	/**
	 * Creates a new <tt>xsd:boolean</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:boolean</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(boolean value);

	/**
	 * Creates a new <tt>xsd:long</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:long</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(long value);

	/**
	 * Creates a new <tt>xsd:int</tt>-typed literal representing the specified
	 * value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:int</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(int value);

	/**
	 * Creates a new <tt>xsd:short</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:short</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(short value);

	/**
	 * Creates a new <tt>xsd:byte</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:byte</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(byte value);

	/**
	 * Creates a new <tt>xsd:double</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:double</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(double value);

	/**
	 * Creates a new <tt>xsd:float</tt>-typed literal representing the
	 * specified value.
	 * 
	 * @param value
	 *        The value for the literal
	 * @return An <tt>xsd:float</tt>-typed literal for the specified value.
	 */
	public Literal createLiteral(float value);

	/**
	 * Creates a new statement with the supplied subject, predicate and object.
	 * 
	 * @param subject
	 *        The statement's subject.
	 * @param predicate
	 *        The statement's predicate.
	 * @param object
	 *        The statement's object.
	 * @return The created statement.
	 */
	public Statement createStatement(Resource subject, URI predicate, Node object);

}

