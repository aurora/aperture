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
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * ModelAccessData provides an AccessData implementation storing its information to and retrieving it from a
 * Model.
 * 
 * <p>
 * This implementation assumes that IDs used to store data are valid URIs and that keys contain only
 * characters that can be used in URIs.
 * 
 * <p>
 * Due to the fact that RDF2Go doesn't support contexts. The accessData will need to have an entire model to
 * itself.
 * <p>
 * The AccessData.DATE_KEY, AccessData.BYTE_SITE_KEY and AccessData.REDIRECTS_TO_KEY keys are mapped to
 * Aperture DATA predicates. In that case the value must be a long encoded as a String or, in the last case, a
 * URL encoded as a String.
 */
public class ModelAccessData implements AccessData {

    private static final Logger LOGGER = Logger.getLogger(ModelAccessData.class.getName());

    /**
     * Used as a prefix to derive URIs from AccessData key names.
     */
    public static final String URI_PREFIX = "urn:accessdata:";

    /**
     * The Model holding the context information.
     */
    private Model model;

    /**
     * Creates a new ModelAccessData instance.
     * 
     * @param model The Model used to store all access data in and retrieve all data from. This cannot be
     *            null.
     */
    public ModelAccessData(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }

        this.model = model;
    }

    public void clear() throws IOException {
        try {
            model.removeAll();
        }
        catch (ModelException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    public String get(String id, String key) {
        commit();

        URI idURI = ModelUtil.createURI(model, id);
        URI keyURI = toURI(key);
        ClosableIterator<? extends Statement> iterator = null;

        // only returns a value when there is exactly one matching statement
        try {
            ClosableIterable<? extends Statement> iterable = model
                    .findStatements(idURI, keyURI, Variable.ANY);
            iterator = iterable.iterator();
            if (iterator.hasNext()) {
                Statement statement = iterator.next();
                if (!iterator.hasNext()) {
                    Node value = statement.getObject();
                    if (value instanceof Literal) {
                        return ((Literal) value).getValue();
                    }
                    else if (value instanceof URI) {
                        return value.toString();
                    }
                }
            }
            return null;
        }
        catch (ModelException me) {
            LOGGER.log(Level.SEVERE, "Couldn't get value for id: '" + id + "' key: '" + key + "'", me);
            return null;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    public Set<String> getReferredIDs(String id) {
        commit();

        URI idURI = ModelUtil.createURI(model, id);
        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = null;

        try {
            ClosableIterable<? extends Statement> iterable = model.findStatements(idURI, DATA.linksTo,
                Variable.ANY);
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Node value = statement.getObject();
                if (value instanceof URI) {
                    if (result == null) {
                        result = new HashSet<String>();
                    }
                    result.add(((URI) value).toString());
                }
            }
        }
        catch (ModelException me) {
            LOGGER.log(Level.SEVERE, "Couldn't get referred id's", me);
            return null;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return result;
    }

    /**
     * Warning: expensive operation, as this implementation queries for all unique subjects used in this
     * ModelAccessData's context.
     */
    public int getSize() {
        return getStoredIDs().size();
    }

    public Set<String> getStoredIDs() {
        commit();

        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = new HashSet<String>();

        try {
            ClosableIterable<? extends Statement> iterable = model.findStatements(Variable.ANY, Variable.ANY,
                Variable.ANY);
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next().getSubject().toString());
            }
        }
        catch (ModelException me) {

        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return result;
    }

    public void initialize() throws IOException {
    // no-op
    }

    public boolean isKnownId(String id) {
        commit();

        URI idURI = ModelUtil.createURI(model, id);
        ClosableIterator<? extends Statement> iterator = null;

        try {
            ClosableIterable<? extends Statement> iterable = model.findStatements(idURI, Variable.ANY,
                Variable.ANY);
            iterator = iterable.iterator();
            return iterator.hasNext();
        }
        catch (ModelException me) {
            LOGGER.log(Level.SEVERE, "Couldn't determine if an id is known", me);
            return false;
        }
        finally {
            iterator.close();
        }
    }

    public void put(String id, String key, String value) {
        // remove any previous statements with different values
        URI subject = ModelUtil.createURI(model, id);
        URI predicate = toURI(key);
        remove(subject, predicate);

        // add the new statement
        if (predicate == DATA.redirectsTo) {
            add(ModelUtil.createStatement(model, subject, predicate, ModelUtil.createURI(model, value)));
        }
        else {
            URI dataType = (predicate == DATA.dateAsNumber || predicate == DATA.byteSize) ? XSD._long
                    : XSD._string;
            Literal object = ModelUtil.createLiteral(model, value, dataType);
            add(ModelUtil.createStatement(model, subject, predicate, object));
        }
    }

    public void putReferredID(String id, String referredID) {
        URI subject = ModelUtil.createURI(model, id);
        URI object = ModelUtil.createURI(model, referredID);
        add(ModelUtil.createStatement(model, subject, DATA.linksTo, object));
    }

    public void remove(String id, String key) {
        remove(ModelUtil.createURI(model, id), toURI(key));
    }

    public void remove(String id) {
        remove(ModelUtil.createURI(model, id), null);
    }

    public void removeReferredID(String id, String referredID) {
        commit();

        URI subject = ModelUtil.createURI(model, id);
        URI object = ModelUtil.createURI(model, referredID);
        Statement statement = ModelUtil.createStatement(model, subject, DATA.linksTo, object);

        try {
            model.removeStatement(statement);
        }
        catch (ModelException e) {
            LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
        }
    }

    public void removeReferredIDs(String id) {
        remove(ModelUtil.createURI(model, id), DATA.linksTo);
    }

    public void store() throws IOException {
        commit();
    }

    private void commit() {
    // this does nothing for the time being...
    // TODO investigate into using the Model in manual commit mode
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
            return ModelUtil.createURI(model, URI_PREFIX + key);
        }
    }

    private void add(Statement statement) {
        try {
            model.addStatement(statement);
        }
        catch (ModelException e) {
            LOGGER.log(Level.SEVERE, "Exception while adding statement", e);
        }
    }

    private void remove(URI subject, URI predicate) {
        commit();
        ClosableIterator<Statement> iterator = null;

        try {
            // ClosableIterable<Statement> iterable = model.findStatements(subject, predicate, Variable.ANY);
            // iterator = iterable.iterator();
            // model.removeAll(iterator);
            model.removeStatement(subject, predicate, Variable.ANY);
        }
        catch (ModelException e) {
            LOGGER.log(Level.SEVERE, "Exception while removing statement", e);
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }
}
