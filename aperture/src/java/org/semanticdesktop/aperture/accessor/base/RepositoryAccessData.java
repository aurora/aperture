/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.RValue;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailUpdateException;
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

	/**
	 * The Repository holding the context information.
	 */
	private Repository repository;

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
	public RepositoryAccessData(Repository repository, Resource context) {
		if (repository == null) {
			throw new IllegalArgumentException("repository cannot be null");
		}

		this.repository = repository;
		this.context = context;
	}

	public void clear() throws IOException {
		try {
			repository.clearContext(context);
		}
		catch (SailUpdateException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}
	}

	public String get(String id, String key) {
		commit();

		URI idURI = new URIImpl(id);
		URI keyURI = toURI(key);
		CloseableIterator<RStatement> iterator = null;

		// only returns a value when there is exactly one matching statement
		try {
			iterator = repository.getStatements(idURI, keyURI, null, context);
			if (iterator.hasNext()) {
				RStatement statement = iterator.next();
				if (!iterator.hasNext()) {
					RValue value = statement.getObject();
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
		CloseableIterator<RStatement> iterator = null;
		HashSet<String> result = null;

		try {
			iterator = repository.getStatements(idURI, DATA.linksTo, null, context);
			while (iterator.hasNext()) {
				RStatement statement = iterator.next();
				RValue value = statement.getObject();
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

	/**
	 * Warning: expensive operation, as this implementation queries for all unique subjects used in this
	 * RepositoryAccessData's context.
	 */
	public int getSize() {
		return getStoredIDs().size();
	}

	public Set<String> getStoredIDs() {
		commit();

		CloseableIterator<RStatement> iterator = null;
		HashSet<String> result = new HashSet<String>();

		try {
			iterator = repository.getStatements(null, null, null, context);
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
		CloseableIterator<RStatement> iterator = null;

		try {
			iterator = repository.getStatements(idURI, null, null, context);
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
			URI dataType = (predicate == DATA.dateAsNumber || predicate == DATA.byteSize) ? XMLSchema.LONG
					: XMLSchema.STRING;
			Literal object = new LiteralImpl(value, dataType);
			add(new StatementImpl(subject, predicate, object));
		}
	}

	public void putReferredID(String id, String referredID) {
		URI subject = new URIImpl(id);
		URI object = new URIImpl(referredID);
		add(new StatementImpl(subject, DATA.linksTo, object));
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
		Statement statement = new StatementImpl(subject, DATA.linksTo, object);

		try {
			repository.remove(statement, context);
		}
		catch (SailUpdateException e) {
			LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
		}
	}

	public void removeReferredIDs(String id) {
		remove(new URIImpl(id), DATA.linksTo);
	}
	
	public void store() throws IOException {
		commit();
	}

	private void commit() {
		try {
			repository.commit();
		}
		catch (SailUpdateException e) {
			LOGGER.log(Level.SEVERE, "Exception while commiting repository", e);
		}
	}

	private URI toURI(String key) {
		if (key == AccessData.DATE_KEY) {
			return DATA.dateAsNumber;
		}
		else if (key == AccessData.BYTE_SIZE_KEY) {
			return DATA.byteSize;
		}
		else if (key == AccessData.REDIRECTS_TO_KEY) {
			return DATA.redirectsTo;
		}
		else {
			return new URIImpl(URI_PREFIX + key);
		}
	}

	private void add(Statement statement) {
		try {
			repository.add(statement, context);
		}
		catch (SailUpdateException e) {
			LOGGER.log(Level.SEVERE, "Exception while adding statement", e);
		}
	}

	private void remove(URI subject, URI predicate) {
		commit();

		// for now we gather all statements in a collection and remove them one-by-one, this will become
		// easier (just pass a CloseableIterator to the remove method) once Sesame issue SES-252 is fixed
		ArrayList<RStatement> statements = new ArrayList<RStatement>();
		repository.getStatements(subject, predicate, null, context, statements);

		try {
			for (RStatement statement : statements) {
				repository.remove(statement, context);
			}
		}
		catch (SailUpdateException e) {
			LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
		}
	}
}
