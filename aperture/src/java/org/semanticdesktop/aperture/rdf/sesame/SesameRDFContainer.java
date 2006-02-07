/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.rdf.MultipleValuesException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.DateUtil;

/**
 * An implementation of RDFContainer that uses a Sesame non-inferencing in-memory repository.
 */
public class SesameRDFContainer implements RDFContainer {

    private static final Logger LOGGER = Logger.getLogger(SesameRDFContainer.class.getName());

    private Repository repository;

    private ValueFactory valueFactory;

    private URI describedUri;

    private Resource context;

    /**
     * Create a new SesameRDFContainer that will manage statements concerning the specified URI.
     * 
     * @param describedUri The URI that typically will serve as object in most statements.
     */
    public SesameRDFContainer(String describedUri) {
        createRepository();
        this.describedUri = valueFactory.createURI(describedUri);
    }

    /**
     * Create a new SesameRDFContainer that will manage statements concerning the specified URI.
     * 
     * @param describedUri The URI that typically will serve as object in most statements.
     */
    public SesameRDFContainer(URI describedUri) {
        createRepository();
        this.describedUri = describedUri;
    }

    /**
     * Create a new SesameRDFContainer that will manage statements concerning the specified URI. All
     * statements will be stored in and retrieved from the specified Repository.
     * 
     * @param repository The Repository to store statements in and retrieve statements from.
     * @param describedUri The URI that typically will serve as object in most statements.
     */
    public SesameRDFContainer(Repository repository, URI describedUri) {
        this.repository = repository;
        this.describedUri = describedUri;
        valueFactory = repository.getSail().getValueFactory();
    }

    private void createRepository() {
        MemoryStore memoryStore = new MemoryStore();

        repository = new Repository(memoryStore);
        try {
            repository.initialize();
        }
        catch (SailInitializationException e) {
            // should never happen, indicates an internal error and therefore wrapped in a
            // RuntimeException rather than an UpdateException
            throw new RuntimeException(e);
        }

        // cannot happen before repository is initialized
        valueFactory = memoryStore.getValueFactory();
    }

    public URI getDescribedUri() {
        return describedUri;
    }

    public Object getModel() {
        return repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setContext(Resource context) {
        this.context = context;
    }

    public Resource getContext() {
        return context;
    }

    public void put(URI property, String value) {
        replaceInternal(property, valueFactory.createLiteral(value));
    }

    public void put(URI property, Date value) {
        String date = DateUtil.dateTime2String(value);
        replaceInternal(property, valueFactory.createLiteral(date, XMLSchema.DATETIME));
    }

    public void put(URI property, Calendar value) {
        put(property, value.getTime());
    }

    public void put(URI property, boolean value) {
        String val = value ? "true" : "false";
        replaceInternal(property, valueFactory.createLiteral(val, XMLSchema.BOOLEAN));
    }

    public void put(URI property, int value) {
        String val = Integer.toString(value);
        replaceInternal(property, valueFactory.createLiteral(val, XMLSchema.INT));
    }

    public void put(URI property, long value) {
        String val = Long.toString(value);
        replaceInternal(property, valueFactory.createLiteral(val, XMLSchema.LONG));
    }

    public void put(URI property, Value value) {
        replaceInternal(property, value);
    }

    public void add(URI property, String value) {
        addInternal(property, valueFactory.createLiteral(value));
    }

    public void add(URI property, Date value) {
        String date = DateUtil.dateTime2String(value);
        addInternal(property, valueFactory.createLiteral(date, XMLSchema.DATETIME));
    }

    public void add(URI property, Calendar value) {
        add(property, value.getTime());
    }

    public void add(URI property, boolean value) {
        String val = value ? "true" : "false";
        addInternal(property, valueFactory.createLiteral(val, XMLSchema.BOOLEAN));
    }

    public void add(URI property, int value) {
        String val = Integer.toString(value);
        addInternal(property, valueFactory.createLiteral(val, XMLSchema.INT));
    }

    public void add(URI property, long value) {
        String val = Long.toString(value);
        addInternal(property, valueFactory.createLiteral(val, XMLSchema.LONG));
    }

    public void add(URI property, Value value) {
        addInternal(property, value);
    }

    public String getString(URI property) {
        Value value = getInternal(property);
        if (value instanceof Literal) {
            return ((Literal) value).getLabel();
        }
        else {
            return null;
        }
    }

    public Date getDate(URI property) {
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
        return new Boolean(getString(property));
    }

    public Integer getInteger(URI property) {
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
        Value value = getInternal(property);
        if (value instanceof URI) {
            return (URI) value;
        }
        else {
            return null;
        }
    }

    public Value getValue(URI property) {
        return getInternal(property);
    }

    public void remove(URI property) {
        // note: this also throws a MultipleValueException when there are multiple values
        Value value = getInternal(property);
        if (value != null) {
            try {
                repository.remove(describedUri, property, value, context);
            }
            catch (SailUpdateException e) {
                LOGGER.log(Level.INFO, "cannot remove statement", e);
                throw new UpdateException("cannot remove statement", e);
            }
        }
    }

    public Collection getAll(URI property) {
        // determine all matching Statements
        CloseableIterator iterator = repository.getStatements(describedUri, property, null);
        
        try {
	        // put their values in a new Collection
	        ArrayList result = new ArrayList();
	        while (iterator.hasNext()) {
	            RStatement statement = (RStatement) iterator.next();
	            result.add(statement.getObject());
	        }
	        return result;
        }
        finally {
        	iterator.close();
        }
    }

    public void add(Statement statement) {
        try {
            repository.add(statement, context);
        }
        catch (SailUpdateException e) {
            LOGGER.log(Level.INFO, "cannot add statement", e);
            throw new UpdateException("cannot add statement", e);
        }
    }

    public void remove(Statement statement) {
        try {
            repository.remove(statement, context);
        }
        catch (SailUpdateException e) {
            LOGGER.log(Level.INFO, "cannot add statement", e);
            throw new UpdateException("cannot add statement", e);
        }
    }

    private void addInternal(URI property, Value object) {
        try {
            repository.add(describedUri, property, object, context);
        }
        catch (SailUpdateException e) {
            LOGGER.log(Level.INFO, "cannot add statement", e);
            throw new UpdateException("cannot add statement", e);
        }
    }

    private void replaceInternal(URI property, Value object) throws MultipleValuesException {
        try {
            // remove any existing statements with this property, throw an exception when there is more
            // than one such statement
            CloseableIterator statements = repository.getStatements(describedUri, property, null);
            try {
	            if (statements.hasNext()) {
	                RStatement statement = (RStatement) statements.next();
	                if (statements.hasNext()) {
	                    throw new MultipleValuesException(describedUri, property);
	                }
	                
	                repository.remove(statement, context);
	            }
            }
            finally {
            	statements.close();
            }
            
            // add the new statement
            repository.add(describedUri, property, object, context);
        }
        catch (SailUpdateException e) {
            LOGGER.log(Level.INFO, "cannot update statement", e);
            throw new UpdateException("cannot update statement", e);
        }
    }

    private Value getInternal(URI property) {
        CloseableIterator statements = repository.getStatements(describedUri, property, null);

        try {
            Value result = null;
            
	        if (statements.hasNext()) {
	            RStatement firstStatement = (RStatement) statements.next();
	            if (statements.hasNext()) {
	                throw new MultipleValuesException(describedUri, property);
	            }
	            result = firstStatement.getObject();
	        }

	        return result;
        }
        finally {
        	statements.close();
        }
    }
}
