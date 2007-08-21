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

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.vocabulary.MAD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        catch (ModelRuntimeException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    public String get(String id, String key) {
        commit();

        ClosableIterator<? extends Statement> iterator = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);
            URI keyURI = toURI(key);

            // only returns a value when there is exactly one matching statement
            iterator = model.findStatements(idURI, keyURI, Variable.ANY);
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
            logger.error("Could not get value for id: '" + id + "' key: '" + key + "'", me);
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

        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);

            iterator = model.findStatements(idURI, MAD.linksTo, Variable.ANY);
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
            logger.error("Could not get referred id's", me);
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
            iterator = model.findStatements(Variable.ANY, Variable.ANY,Variable.ANY);
            while (iterator.hasNext()) {
                result.add(iterator.next().getSubject().toString());
            }
        }
        catch (ModelRuntimeException me) {
            logger.warn("Couldn't get stored IDs", me);
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

        ClosableIterator<? extends Statement> iterator = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);

            iterator = model.findStatements(idURI, Variable.ANY,Variable.ANY);
            return iterator.hasNext();
        }
        catch (ModelException me) {
            logger.error("Could not determine if an ID is known: " + id, me);
            return false;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    public void put(String id, String key, String value) {
        try {
            // remove any previous statements with different values
            URI subject = ModelUtil.createURI(model, id);
            URI predicate = toURI(key);
            remove(subject, predicate);

            // add the new statement
            if (predicate == MAD.redirectsTo) {
                add(ModelUtil.createStatement(model, subject, predicate, ModelUtil.createURI(model, value)));
            }
            else {
                URI dataType = (predicate == MAD.dateAsNumber || predicate == MAD.byteSize) ? XSD._long
                        : XSD._string;
                Literal object = ModelUtil.createLiteral(model, value, dataType);
                add(ModelUtil.createStatement(model, subject, predicate, object));
            }
        }
        catch (ModelException e) {
            logger.error("Could not store info for ID " + id, e);
        }
    }

    public void putReferredID(String id, String referredID) {
        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, referredID);
            add(ModelUtil.createStatement(model, subject, MAD.linksTo, object));
        }
        catch (ModelException e) {
            logger.error("Could not store referred ID for ID " + id, e);
        }
    }

    public void remove(String id, String key) {
        try {
            remove(ModelUtil.createURI(model, id), toURI(key));
        }
        catch (ModelException e) {
            logger.error("Could not remove value for ID " + id, e);
        }
    }

    public void remove(String id) {
        try {
            remove(ModelUtil.createURI(model, id), null);
        }
        catch (ModelException e) {
            logger.error("Could not remove info about ID " + id, e);
        }
    }

    public void removeReferredID(String id, String referredID) {
        commit();

        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, referredID);
            Statement statement = ModelUtil.createStatement(model, subject, MAD.linksTo, object);

            model.removeStatement(statement);
        }
        catch (ModelException e) {
            logger.error("Could not remove referred ID for ID " + id, e);
        }
    }

    public void removeReferredIDs(String id) {
        try {
            remove(ModelUtil.createURI(model, id), MAD.linksTo);
        }
        catch (ModelException e) {
            logger.error("Could not remove referred IDs for ID " + id, e);
        }
    }

    public void store() throws IOException {
        commit();
    }

    private void commit() {}

    private URI toURI(String key) throws ModelException {
        if (key == AccessData.DATE_KEY) {
            return MAD.dateAsNumber;
        }
        else if (key == AccessData.BYTE_SIZE_KEY) {
            return MAD.byteSize;
        }
        else if (key == AccessData.REDIRECTS_TO_KEY) {
            return MAD.redirectsTo;
        }
        else {
            return ModelUtil.createURI(model, URI_PREFIX + key);
        }
    }

    private void add(Statement statement) {
        try {
            model.addStatement(statement);
        }
        catch (ModelRuntimeException e) {
            logger.error("Exception while adding statement", e);
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
        catch (ModelRuntimeException e) {
            logger.error("Exception while removing statement", e);
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }
}
