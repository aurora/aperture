/*
 * Copyright (c) 2005 - 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.impl.sesame2.ModelImplSesame;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;

/**
 * An implementation of RDFContainer that uses a Sesame non-inferencing in-memory repository.
 * 
 * <p>
 * Note that when you specify a Repository to SesameRDFContainer's constructor, you are self responsible for
 * committing statements when the Repository is not in auto-commit mode. The put, get and remove methods will
 * perform a lazy commit in order to see the most recent set of statements, but the add methods will never
 * commit themselves.
 */
public class SesameRDFContainer implements RDFContainer {

	private RDF2GoRDFContainer container;

	/**
	 * Create a new SesameRDFContainer that will manage statements concerning the specified URI.
	 * 
	 * @param describedUri The URI that typically will serve as object in most statements.
	 */
	public SesameRDFContainer(String describedUri) {
		createContainer(URIImpl.create(describedUri),null,false);
	}

	/**
	 * Create a new SesameRDFContainer that will manage statements concerning the specified URI.
	 * 
	 * @param describedUri The URI that typically will serve as object in most statements.
	 */
	public SesameRDFContainer(URI describedUri) {
		createContainer(describedUri,null,false);	
	}
	
	public SesameRDFContainer(URI describedUri, URI context) {
		createContainer(describedUri,context,false);
	}

	/**
	 * Create a new SesameRDFContainer that will manage statements concerning the specified URI. All
	 * statements will be stored in and retrieved from the specified Repository.
	 * 
	 * @param model The Model to store statements in and retrieve statements from.
	 * @param describedUri The URI that typically will serve as object in most statements.
	 */
	public SesameRDFContainer(ModelImplSesame model, URI describedUri) {
		this.container = new RDF2GoRDFContainer(model,describedUri);
	}
	
	private void createContainer(URI describedUri, URI context,boolean shared) {
		try {
			ModelImplSesame model = new ModelImplSesame(context,false);
			container = new RDF2GoRDFContainer(model,describedUri,shared);
		} catch (ModelException me) {
			throw new RuntimeException(me);
		}
	}

	public URI getDescribedUri() {
		return container.getDescribedUri();
	}

	public Model getModel() {
		return container.getModel();
	}

	public URI getContext() {
		return ((ModelImplSesame)container.getModel()).getContextURI();
	}
	
	public void put(URI property, String value) {
		container.put(property, value);
	}

	public void put(URI property, Date value) {
		container.put(property, value);
	}

	public void put(URI property, Calendar value) {
		container.put(property, value);
	}

	public void put(URI property, boolean value) {
		container.put(property, value);
	}

	public void put(URI property, int value) {
		container.put(property, value);	}

	public void put(URI property, long value) {
		container.put(property, value);
	}

	public void put(URI property, Node value) {
		container.put(property, value);
	}

	public void add(URI property, String value) {
		container.add(property, value);
	}

	public void add(URI property, Date value) {
		container.add(property, value);
	}

	public void add(URI property, Calendar value) {
		container.add(property, value);
	}

	public void add(URI property, boolean value) {
		container.add(property, value);
	}

	public void add(URI property, int value) {
		container.add(property, value);
	}

	public void add(URI property, long value) {
		container.add(property, value);
	}

	public void add(URI property, Node value) {
		container.add(property, value);
	}

	public String getString(URI property) {
		return container.getString(property);
	}

	public Date getDate(URI property) {
		return container.getDate(property);
	}

	public Calendar getCalendar(URI property) {
		return container.getCalendar(property);
	}

	public Boolean getBoolean(URI property) {
		return container.getBoolean(property);
	}

	public Integer getInteger(URI property) {
		return container.getInteger(property);
	}

	public Long getLong(URI property) {
		return container.getLong(property);
	}

	public URI getURI(URI property) {
		return container.getURI(property);
	}

	public Node getNode(URI property) {
		return container.getNode(property);
	}

	public void remove(URI property) {
		container.remove(property);
	}

	public Collection getAll(URI property) {
		return container.getAll(property);
	}

	public void add(Statement statement) {
		container.add(statement);
	}

	public void remove(Statement statement) {
		container.remove(statement);
	}

	public ValueFactory getValueFactory() {
		return container.getValueFactory();
	}
	
	public void dispose() {
		container.dispose();
	}
	
	public boolean isModelShared() {
		return container.isModelShared();
	}
	
	public boolean isDisposed() {
		return container.isDisposed();
	}

}
