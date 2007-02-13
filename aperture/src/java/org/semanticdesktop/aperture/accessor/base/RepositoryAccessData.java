/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Connection;
import org.openrdf.repository.Repository;
import org.openrdf.sail.SailException;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * RepositoryAccessData provides an AccessData implementation storing its information to and retrieving it
 * from a Repository.
 * 
 * <p>
 * This implementation assumes that IDs used to store data are valid URIs and that keys contain only
 * characters that can be used in URIs.
 * 
 * <p>
 * The AccessData.DATE_KEY, AccessData.BYTE_SITE_KEY and AccessData.REDIRECTS_TO_KEY keys are mapped to
 * Aperture DATA predicates. In that case the value must be a long encoded as a String or, in the last case, a
 * URL encoded as a String.
 */
public class RepositoryAccessData implements AccessData {

	private static final Logger LOGGER = Logger.getLogger(RepositoryAccessData.class.getName());

	/**
	 * Used as a prefix to derive URIs from AccessData key names.
	 */
	public static final String URI_PREFIX = "urn:accessdata:";

	private Connection connection;

	/**
	 * The context Resource used to store all access data in.
	 */
	private Resource context;

	/**
	 * Creates a new RepositoryAccessData instance.
	 * 
	 * @param repository The Repository used to store all access data in and retrieve all data from. This
	 *            cannot be null.
	 * @param context The context used to mark all statements handled by this RepositoryAccessData. This is
	 *            allowed to be null.
	 */
	public RepositoryAccessData(Repository repository, Resource context) throws SailException {
		if (repository == null) {
			throw new IllegalArgumentException("repository cannot be null");
		}

		this.context = context;
		this.connection = repository.getConnection();
		this.connection.setAutoCommit(true);
	}
	
	public void shutDown() {
		try {
			connection.close();
		} catch (SailException se) {
			throw new RuntimeException(se);
		}
	}
	
	public void setAutoCommit(boolean value) {
		try {
			connection.setAutoCommit(value);
		} catch (SailException se) {
			throw new RuntimeException(se);
		}
	}

	public void clear() throws IOException {
		try {
			connection.clearContext(context);
		}
		catch (SailException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}
	}

	public String get(String id, String key) {
		commit();

		URI idURI = new URIImpl(id);
		URI keyURI = toURI(key);
		CloseableIterator<? extends Statement> iterator = null;

		// only returns a value when there is exactly one matching statement
		try {
			iterator = connection.getStatements(idURI, keyURI, null, context, false);
			if (iterator.hasNext()) {
				Statement statement = iterator.next();
				if (!iterator.hasNext()) {
					Value value = statement.getObject();
					if (value instanceof Literal) {
						return ((Literal) value).getLabel();
					}
					else if (value instanceof URI) {
						return value.toString();
					}
				}
			}

			return null;
		}
		finally {
			iterator.close();
		}
	}

	public Set<String> getReferredIDs(String id) {
		commit();

		URI idURI = new URIImpl(id);
		CloseableIterator<? extends Statement> iterator = null;
		HashSet<String> result = null;

		try {
			iterator = connection.getStatements(idURI, toSesameURI(DATA.linksTo), null, context, false);
			while (iterator.hasNext()) {
				Statement statement = iterator.next();
				Value value = statement.getObject();
				if (value instanceof URI) {
					if (result == null) {
						result = new HashSet<String>();
					}
					result.add(((URI) value).toString());
				}
			}
		}
		finally {
			iterator.close();
		}

		return result;
	}

	private URI toSesameURI(org.ontoware.rdf2go.model.node.URI rdf2goUri) {
		return new URIImpl(rdf2goUri.toString());
	}

	/**
	 * Warning: expensive operation, as this implementation queries for all unique subjects used in this
	 * RepositoryAccessData's context.
	 */
	public int getSize() {
		return getStoredIDs().size();
	}

	public Set<String> getStoredIDs() {
		commit();

		CloseableIterator<? extends Statement> iterator = null;
		HashSet<String> result = new HashSet<String>();

		try {
			iterator = connection.getStatements(null, null, null, context, false);
			while (iterator.hasNext()) {
				result.add(iterator.next().getSubject().toString());
			}
		}
		finally {
			iterator.close();
		}

		return result;
	}

	public void initialize() throws IOException {
	// no-op
	}

	public boolean isKnownId(String id) {
		commit();

		URI idURI = new URIImpl(id);
		CloseableIterator<? extends Statement> iterator = null;

		try {
			iterator = connection.getStatements(idURI, null, null, context, false);
			return iterator.hasNext();
		}
		finally {
			iterator.close();
		}
	}

	public void put(String id, String key, String value) {
		// remove any previous statements with different values
		URI subject = new URIImpl(id);
		URI predicate = toURI(key);
		remove(subject, predicate);

		// add the new statement
		if (predicate == DATA.redirectsTo) {
			add(new StatementImpl(subject, predicate, new URIImpl(value)));
		}
		else {
			URI dataType = (predicate == DATA.date || predicate == DATA.byteSize) ? XMLSchema.LONG
					: XMLSchema.STRING;
			Literal object = new LiteralImpl(value, dataType);
			add(new StatementImpl(subject, predicate, object));
		}
	}

	public void putReferredID(String id, String referredID) {
		URI subject = new URIImpl(id);
		URI object = new URIImpl(referredID);
		add(new StatementImpl(subject, toSesameURI(DATA.linksTo), object));
	}

	public void remove(String id, String key) {
		remove(new URIImpl(id), toURI(key));
	}

	public void remove(String id) {
		remove(new URIImpl(id), null);
	}

	public void removeReferredID(String id, String referredID) {
		commit();
		
		URI subject = new URIImpl(id);
		URI object = new URIImpl(referredID);
		Statement statement = new StatementImpl(subject, toSesameURI(DATA.linksTo), object);

		try {
			connection.remove(statement, context);
		}
		catch (SailException e) {
			LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
		}
	}

	public void removeReferredIDs(String id) {
		remove(new URIImpl(id), toSesameURI(DATA.linksTo));
	}
	
	public void store() throws IOException {
		commit();
	}

	private void commit() {
		try {
			connection.commit();
		}
		catch (SailException e) {
			LOGGER.log(Level.SEVERE, "Exception while commiting repository", e);
		}
	}

	private URI toURI(String key) {
		if (key == AccessData.DATE_KEY) {
			return toSesameURI(DATA.date);
		}
		else if (key == AccessData.BYTE_SIZE_KEY) {
			return toSesameURI(DATA.byteSize);
		}
		else if (key == AccessData.REDIRECTS_TO_KEY) {
			return toSesameURI(DATA.redirectsTo);
		}
		else {
			return new URIImpl(URI_PREFIX + key);
		}
	}

	private void add(Statement statement) {
		try {
			connection.add(statement, context);
		}
		catch (SailException e) {
			LOGGER.log(Level.SEVERE, "Exception while adding statement", e);
		}
	}

	private void remove(URI subject, URI predicate) {
		commit();

		// for now we gather all statements in a collection and remove them one-by-one, this will become
		// easier (just pass a CloseableIterator to the remove method) once Sesame issue SES-252 is fixed
		// ArrayList<Statement> statements = new ArrayList<Statement>();
		
		CloseableIterator<? extends Statement> statements = null; 

		try {
			statements = connection.getStatements(subject, predicate, null, context, false);
			connection.remove(statements);
		}
		catch (SailException e) {
			LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
		}
	}
}
