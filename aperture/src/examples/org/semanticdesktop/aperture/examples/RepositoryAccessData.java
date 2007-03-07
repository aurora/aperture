/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
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

    /**
     * Used as a prefix to derive URIs from AccessData key names.
     */
    public static final String URI_PREFIX = "urn:accessdata:";

    /**
     * The single RepositoryConnection that will be used for all access to the Repository.
     */
    private RepositoryConnection connection;

    /**
     * The context Resource used to label all statements managed by this RepositoryAccessData. This enables
     * the use of a Repository that is shared with other components.
     */
    private Resource context;

    /**
     * Creates a new RepositoryAccessData instance.
     * 
     * @param repository The Repository used for storing all access data.
     * @param context The context used to mark all statements handled by this RepositoryAccessData. This is
     *            allowed to be null.
     * @throws RepositoryException When setup of a RepositoryConnection fails.
     */
    public RepositoryAccessData(Repository repository, Resource context) throws RepositoryException {
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
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAutoCommit(boolean value) {
        try {
            connection.setAutoCommit(value);
        }
        catch (RepositoryException se) {
            throw new RuntimeException(se);
        }
    }

    public void clear() throws IOException {
        try {
            connection.clear(context);
        }
        catch (RepositoryException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    public String get(String id, String key) {
        commit();

        URI idURI = new URIImpl(id);
        URI keyURI = toURI(key);
        RepositoryResult<Statement> resultIterator = null;

        // only returns a value when there is exactly one matching statement
        try {
            resultIterator = connection.getStatements(idURI, keyURI, null, false, context);
            if (resultIterator.hasNext()) {
                Statement statement = resultIterator.next();
                if (!resultIterator.hasNext()) {
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
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                resultIterator.close();
            }
            catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Set<String> getReferredIDs(String id) {
        commit();

        RepositoryResult<Statement> resultIterator = null;
        HashSet<String> result = null;

        try {
            resultIterator = connection.getStatements(new URIImpl(id), toSesameURI(DATA.linksTo), null, false, context);
            while (resultIterator.hasNext()) {
                Statement statement = resultIterator.next();
                Value value = statement.getObject();
                if (value instanceof URI) {
                    if (result == null) {
                        result = new HashSet<String>();
                    }
                    result.add(((URI) value).toString());
                }
            }
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                resultIterator.close();
            }
            catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
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
        commit();

        RepositoryResult<Statement> resultIterator = null;
        int result = 0;
            
        try {
            resultIterator = connection.getStatements(null, null, null, false, context);
            while (resultIterator.hasNext()) {
                resultIterator.next();
                result++;
            }
            
            return result;
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                resultIterator.close();
            }
            catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Set<String> getStoredIDs() {
        commit();

        RepositoryResult<Statement> resultIterator = null;
        HashSet<String> result = new HashSet<String>();

        try {
            resultIterator = connection.getStatements(null, null, null, false, context);
            while (resultIterator.hasNext()) {
                result.add(resultIterator.next().getSubject().toString());
            }
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                resultIterator.close();
            }
            catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    public void initialize() throws IOException {
    // no-op
    }

    public boolean isKnownId(String id) {
        commit();

        try {
            return connection.hasStatement(new URIImpl(id), null, null, false, context);
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
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
        add(new StatementImpl(new URIImpl(id), toSesameURI(DATA.linksTo), new URIImpl(referredID)));
    }

    public void remove(String id, String key) {
        remove(new URIImpl(id), toURI(key));
    }

    public void remove(String id) {
        remove(new URIImpl(id), null);
    }

    public void removeReferredID(String id, String referredID) {
        commit();

        try {
            connection.remove(new URIImpl(id), toSesameURI(DATA.linksTo), new URIImpl(referredID), context);
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
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
        catch (RepositoryException e) {
            throw new RuntimeException(e);
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
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private void remove(URI subject, URI predicate) {
        commit();

        try {
            connection.remove(subject, predicate, null, context);
        }
        catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
