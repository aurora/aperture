/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.rdf.MultipleValuesException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.UpdateException;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.util.DateUtil;

/**
 * An implementation of RDFContainer that uses an RDF2Go Model backed by Sesame.
 */
public class RDFContainerImpl implements RDFContainer {

	private static final Logger LOGGER = Logger.getLogger(RDFContainerImpl.class.getName());

	private ValueFactory valueFactory;

	private Model model;

	private URI describedUri;

	private boolean modelShared;

	private boolean disposed;

	/**
	 * Create a new RDF2Go RDFContainer that will manage statements concerning the specified URI.
	 * 
	 * @param describedUri The URI that typically will serve as object in most statements.
	 */
	public RDFContainerImpl(Model model, String describedUri) {
		init(model, false);
		this.describedUri = valueFactory.createURI(describedUri);
	}

	/**
	 * This has been the default to support backwards compatibility. Many bundles expect to get access to all
	 * classes available from the VM boot classpath without specifying the constraint in their bundle manifest
	 * (i.e. Import-Package or Require-Bundle). This is not the default behavior of OSGi R4. The OSGi
	 * specification mandates that a bundle declare all package depenencies using either Import-Package or
	 * Require-Bundle. The one exception to this rule is the java.* packages which is always delegated to the
	 * boot classpath. All other packages dependencies must be declared in the bundle's manifest file. Create
	 * a new SesameRDFContainer that will manage statements concerning the specified URI. All statements will
	 * be stored in and retrieved from the specified Repository.
	 * 
	 * @param model The Model to store statements in and retrieve statements from.
	 * @param describedUri The URI that typically will serve as object in most statements.
	 */
	public RDFContainerImpl(Model model, URI describedUri) {
		init(model, false);
		this.describedUri = describedUri;
	}

	/**
	 * Create a new SesameRDFContainer that will manage statements concerning the specified URI. All
	 * statements will be stored in and retrieved from the specified Repository.
	 * 
	 * @param model The Model to store statements in and retrieve statements from.
	 * @param describedUri The URI that typically will serve as object in most statements.
	 * @param modelShared Indicates if this container should 'own' the model instance. If this parameter is
	 *            set to true, the model will be closed when this container is disposed(). In this case this
	 *            model must not be shared between multiple RDFContainers. Unexpected results may occur. If
	 *            this parameter is set to false, the model will not be closed on disposal, and may be shared
	 *            between multiple RDFContainers.
	 */
	public RDFContainerImpl(Model model, URI describedUri, boolean modelShared) {
		init(model, modelShared);
		this.describedUri = describedUri;
	}

	/**
	 * Create a new SesameRDFContainer that will manage statements concerning the specified URI. All
	 * statements will be stored in and retrieved from the specified Repository.
	 * 
	 * @param model The Model to store statements in and retrieve statements from.
	 * @param describedUri The URI that typically will serve as object in most statements.
	 * @param modelShared Indicates if this container should 'own' the model instance. If this parameter is
	 *            set to true, the model will be closed when this container is disposed(). In this case this
	 *            model must not be shared between multiple RDFContainers. Unexpected results may occur. If
	 *            this parameter is set to false, the model will not be closed on disposal, and may be shared
	 *            between multiple RDFContainers.
	 */
	public RDFContainerImpl(Model model, String describedUri, boolean modelShared) {
		init(model, modelShared);
		this.describedUri = valueFactory.createURI(describedUri);
	}

	public URI getDescribedUri() {
		return describedUri;
	}

	public Model getModel() {
		return model;
	}

	public void put(URI property, String value) {
		checkState();
		replaceInternal(property, valueFactory.createLiteral(value));
	}

	public void put(URI property, Date value) {
		checkState();
		String date = DateUtil.dateTime2String(value);
		replaceInternal(property, valueFactory.createLiteral(date, XSD._dateTime));
	}

	public void put(URI property, Calendar value) {
		checkState();
		put(property, value.getTime());
	}

	public void put(URI property, boolean value) {
		checkState();
		replaceInternal(property, valueFactory.createLiteral(value));
	}

	public void put(URI property, int value) {
		checkState();
		replaceInternal(property, valueFactory.createLiteral(value));
	}

	public void put(URI property, long value) {
		checkState();
		replaceInternal(property, valueFactory.createLiteral(value));
	}

	public void put(URI property, Node value) {
		checkState();
		replaceInternal(property, value);
	}

	public void add(URI property, String value) {
		checkState();
		addInternal(property, valueFactory.createLiteral(value));
	}

	public void add(URI property, Date value) {
		checkState();
		String date = DateUtil.dateTime2String(value);
		addInternal(property, valueFactory.createLiteral(date, XSD._dateTime));
	}

	public void add(URI property, Calendar value) {
		checkState();
		add(property, value.getTime());
	}

	public void add(URI property, boolean value) {
		checkState();
		addInternal(property, valueFactory.createLiteral(value));
	}

	public void add(URI property, int value) {
		checkState();
		addInternal(property, valueFactory.createLiteral(value));
	}

	public void add(URI property, long value) {
		checkState();
		addInternal(property, valueFactory.createLiteral(value));
	}

	public void add(URI property, Node node) {
		checkState();
		addInternal(property, node);
	}

	public String getString(URI property) {
		checkState();
		Node node = getInternal(property);
		if (node instanceof Literal) {
		    return ((Literal) node).getValue();
		}
		else {
			return null;
		}
	}

	public Date getDate(URI property) {
		checkState();
		String value = getString(property);
		if (value == null) {
			return null;
		}
		else {
			try {
				return DateUtil.string2DateTime(value);
			}
			catch (ParseException e) {
				// illegal date: interpret as no date available
				return null;
			}
		}
	}

	public Calendar getCalendar(URI property) {
		checkState();
		Date date = getDate(property);
		if (date == null) {
			return null;
		}
		else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}
	}

	public Boolean getBoolean(URI property) {
		checkState();
		String value = getString(property);
		if (value == null) {
			return null;
		}
		else {
			return new Boolean(value);
		}
	}

	public Integer getInteger(URI property) {
		checkState();
		String value = getString(property);
		if (value == null) {
			return null;
		}
		else {
			try {
				return new Integer(value);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
	}

	public Long getLong(URI property) {
		checkState();
		String value = getString(property);
		if (value == null) {
			return null;
		}
		else {
			try {
				return new Long(value);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
	}

	public URI getURI(URI property) {
		checkState();
		Node node = getInternal(property);
		if (node instanceof URI) {
		    return node.asURI();
		}
		else {
			return null;
		}
	}

	public Node getNode(URI property) {
		checkState();
		return getInternal(property);
	}

	public void remove(URI property) {
		checkState();
		// note: this also throws a MultipleValueException when there are multiple values
		Node node = getInternal(property);
		if (node != null) {
			try {
				model.removeStatement(describedUri, property, node);
			}
			catch (ModelException me) {
				LOGGER.log(Level.SEVERE, "Couldn't remove a statement from the model", me);
			}
		}
	}

	public Collection getAll(URI property) {
		checkState();
		// determine all matching Statements
		ClosableIterator<? extends Statement> iterator = null;
		try {
			ClosableIterable<? extends Statement> iterable = model.findStatements(describedUri, property, Variable.ANY);
			iterator = iterable.iterator();
			// put their values in a new Collection
			ArrayList<Node> result = new ArrayList<Node>();
			while (iterator.hasNext()) {
				Statement statement = iterator.next();
				result.add(statement.getObject());
			}
			return result;
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "Couldn't find statements", me);
			return null;
		}
		finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}

	public void add(Statement statement) {
		checkState();
		try {
			model.addStatement(statement);
		}
		catch (ModelException e) {
			LOGGER.log(Level.INFO, "cannot add statement", e);
			throw new UpdateException("cannot add statement", e);
		}
	}

	public void remove(Statement statement) {
		checkState();
		try {
			model.removeStatement(statement);
		}
		catch (ModelException e) {
			LOGGER.log(Level.INFO, "cannot remove statement", e);
			throw new UpdateException("cannot remove statement", e);
		}
	}

	private void addInternal(URI property, Node object) {
		try {
			model.addStatement(describedUri, property, object);
		}
		catch (ModelException e) {
			LOGGER.log(Level.INFO, "cannot add statement", e);
			throw new UpdateException("cannot add statement", e);
		}
	}

	private void replaceInternal(URI property, Node object) throws MultipleValuesException {

		try {
			// remove any existing statements with this property, throw an exception when there is more
			// than one such statement
			ClosableIterable<? extends Statement> iterable = model.findStatements(describedUri, property, Variable.ANY);
			ClosableIterator<? extends Statement> statements = iterable.iterator();
			Statement statementToRemove = null;

			try {
				if (statements.hasNext()) {
					statementToRemove = statements.next();
					if (statements.hasNext()) {
						throw new MultipleValuesException(describedUri, property);
					}
				}
			}
			finally {
				statements.close();
			}

			// do this after the iterator has closed or the remove may result in a deadlock
			if (statementToRemove != null) {
				model.removeStatement(statementToRemove);
			}

			// add the new statement
			model.addStatement(describedUri, property, object);
		}
		catch (ModelException me) {
			LOGGER.log(Level.INFO, "cannot update statement", me);
			throw new UpdateException("cannot update statement", me);
		}
	}

	private Node getInternal(URI property) {
		ClosableIterator<? extends Statement> statements = null;
		try {
			ClosableIterable<? extends Statement> iterable = model.findStatements(describedUri, property, Variable.ANY);
			statements = iterable.iterator();
			Node result = null;

			if (statements.hasNext()) {
				Statement firstStatement = (Statement) statements.next();
				if (statements.hasNext()) {
					throw new MultipleValuesException(describedUri, property);
				}
				result = firstStatement.getObject();
			}

			return result;
		}
		catch (ModelException me) {
			LOGGER.log(Level.SEVERE, "couldn't find statements", me);
			return null;
		}
		finally {
			if (statements != null) {
				statements.close();
			}
		}
	}

	public ValueFactory getValueFactory() {
		checkState();
		return valueFactory;
	}

	public void dispose() {
		this.disposed = true;
		if (!modelShared) {
			model.close();
		}
	}

	public boolean isDisposed() {
		return disposed;
	}

	public boolean isModelShared() {
		return modelShared;
	}

	private void checkState() {
		if (disposed) {
			throw new IllegalStateException("Trying to perform an operation after disposal");
		}
	}

	private void init(Model model, boolean shared) {
		this.model = model;
		this.valueFactory = new ValueFactoryImpl(model);
		this.modelShared = shared;
		this.disposed = false;
	}
}
