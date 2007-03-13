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

import org.ontoware.aifbcommons.collection.ClosableIterable;
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
import org.semanticdesktop.aperture.rdf.MultipleValuesException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.UpdateException;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default RDFContainer implementation, wrapping and editing a RDF2Go Model.
 * 
 * <p>
 * RDFContainerImpl can wrap a dedicated Model or can alternatively wrap a Model shared with other
 * RDFContainerImpls. Depending on this mode, the dispose method will or will not close the underlying Model.
 */
public class RDFContainerImpl implements RDFContainer {

    private Logger logger = LoggerFactory.getLogger(RDFContainerImpl.class.getName());

    /**
     * The ValueFactory that transforms Java native types to RDF2Go RDF datastructures.
     */
    private ValueFactory valueFactory;

    /**
     * The RDF2Go Model wrapped and edited by this RDFContainerImpl. This Model may or may not be shared with
     * other RDFContainerImpls.
     */
    private Model model;

    /**
     * The URI that is described in the contents of this RDFContainerImpl.
     */
    private URI describedUri;

    /**
     * Flag that indicates whether this RDFContainer holds the Model exclusively or whether it is shared with
     * other RDFContainerImpls. This setting influences the behaviour of the dispose method: shared Models are
     * not closed by this RDFContainerImpl.
     */
    private boolean modelShared;

    /**
     * Flag that maintains whether this RDFContainerImpl has been disposed already.
     */
    private boolean disposed;

    /**
     * Create a new RDFContainerImpl that will manage statements concerning the specified URI.
     * 
     * @param model A non-shared Model to store statements in and retrieve statements from.
     * @param describedUri The URI that typically will serve as subject in most statements.
     */
    public RDFContainerImpl(Model model, String describedUri) {
        init(model, false);
        try {
            this.describedUri = valueFactory.createURI(describedUri);
        }
        catch (ModelException e) {
            // this is so destructive that there's no point in logging it, we throw a RTE instead
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new RDFContainerImpl that will manage statements concerning the specified URI.
     * 
     * @param model A non-shared Model to store statements in and retrieve statements from.
     * @param describedUri The URI that typically will serve as subject in most statements.
     */
    public RDFContainerImpl(Model model, URI describedUri) {
        init(model, false);
        this.describedUri = describedUri;
    }

    /**
     * Create a new RDFContainerImpl that will manage statements concerning the specified URI.
     * 
     * @param model A Model to store statements in and retrieve statements from.
     * @param describedUri The URI that typically will serve as subject in most statements.
     * @param modelShared Indicates if the specified Model is shared with other RDFContainerImpls. When set to
     *            'false', the model will be closed when this container is disposed.
     */
    public RDFContainerImpl(Model model, URI describedUri, boolean modelShared) {
        init(model, modelShared);
        this.describedUri = describedUri;
    }

    /**
     * Create a new RDFContainerImpl that will manage statements concerning the specified URI.
     * 
     * @param model A Model to store statements in and retrieve statements from.
     * @param describedUri The URI that typically will serve as subject in most statements.
     * @param modelShared Indicates if the specified Model is shared with other RDFContainerImpls. When set to
     *            'false', the model will be closed when this container is disposed.
     */
    public RDFContainerImpl(Model model, String describedUri, boolean modelShared) {
        init(model, modelShared);
        try {
            this.describedUri = valueFactory.createURI(describedUri);
        }
        catch (ModelException e) {
            // this is so destructive that there's no point in logging it, we throw a RTE instead
            throw new RuntimeException(e);
        }
    }

    private void init(Model model, boolean shared) {
        this.model = model;
        this.modelShared = shared;

        this.valueFactory = new ValueFactoryImpl(model);
        this.disposed = false;
    }

    public URI getDescribedUri() {
        return describedUri;
    }

    public Model getModel() {
        return model;
    }

    public void put(URI property, String value) {
        checkState();
        try {
            replaceInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void put(URI property, Date value) {
        checkState();
        String date = DateUtil.dateTime2String(value);
        try {
            replaceInternal(property, valueFactory.createLiteral(date, XSD._dateTime));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void put(URI property, Calendar value) {
        checkState();
        put(property, value.getTime());
    }

    public void put(URI property, boolean value) {
        checkState();
        try {
            replaceInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void put(URI property, int value) {
        checkState();
        try {
            replaceInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void put(URI property, long value) {
        checkState();
        try {
            replaceInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void put(URI property, Node value) {
        checkState();
        replaceInternal(property, value);
    }

    public void add(URI property, String value) {
        checkState();
        try {
            addInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void add(URI property, Date value) {
        checkState();
        String date = DateUtil.dateTime2String(value);
        try {
            addInternal(property, valueFactory.createLiteral(date, XSD._dateTime));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void add(URI property, Calendar value) {
        checkState();
        add(property, value.getTime());
    }

    public void add(URI property, boolean value) {
        checkState();
        try {
            addInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void add(URI property, int value) {
        checkState();
        try {
            addInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
    }

    public void add(URI property, long value) {
        checkState();
        try {
            addInternal(property, valueFactory.createLiteral(value));
        }
        catch (ModelException e) {
            logger.error("ModelException while storing value, ignoring", e);
        }
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
            catch (ModelRuntimeException me) {
                logger.error("Could not remove a statement from the model", me);
            }
        }
    }

    public Collection getAll(URI property) {
        checkState();
        // determine all matching Statements
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(describedUri, property,Variable.ANY);
            // put their values in a new Collection
            ArrayList<Node> result = new ArrayList<Node>();
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                result.add(statement.getObject());
            }
            return result;
        }
        catch (ModelRuntimeException me) {
            logger.error("Could not find statements", me);
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
        catch (ModelRuntimeException e) {
            logger.error("cannot add statement", e);
            throw new UpdateException("cannot add statement", e);
        }
    }

    public void remove(Statement statement) {
        checkState();
        try {
            model.removeStatement(statement);
        }
        catch (ModelRuntimeException e) {
            logger.error("cannot remove statement", e);
            throw new UpdateException("cannot remove statement", e);
        }
    }

    private void addInternal(URI property, Node object) {
        try {
            model.addStatement(describedUri, property, object);
        }
        catch (ModelRuntimeException e) {
            logger.error("cannot add statement", e);
            throw new UpdateException("cannot add statement", e);
        }
    }

    private void replaceInternal(URI property, Node object) throws MultipleValuesException {

        try {
            // remove any existing statements with this property, throw an exception when there is more
            // than one such statement
            ClosableIterator<? extends Statement> statements = model.findStatements(describedUri, property,
                Variable.ANY);
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
        catch (ModelRuntimeException me) {
            logger.error("cannot update statement", me);
            throw new UpdateException("cannot update statement", me);
        }
    }

    private Node getInternal(URI property) {
        ClosableIterator<? extends Statement> statements = null;
        try {
            statements = model.findStatements(describedUri, property,Variable.ANY);
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
        catch (ModelRuntimeException me) {
            logger.error("Could not find statements", me);
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

    /**
     * Disposes this RDFContainerImpl. When the Model shared flag is set toResults of invoking any methods
     * after disposal is undefined.
     */
    public void dispose() {
        this.disposed = true;
        if (!modelShared) {
            model.close();
        }
    }

    /**
     * Returns whether this RDFContainerImpl has been disposed.
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Returns whether the wrapped Model is shared with other RDFContainerImpls.
     */
    public boolean isModelShared() {
        return modelShared;
    }

    private void checkState() {
        if (disposed) {
            throw new IllegalStateException("Trying to perform an operation after disposal");
        }
    }
}
