/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.RDFUtil;

/**
 * An implementation of RDFContainer that uses a Sesame non-inferencing in-memory repository.
 */
public class SesameRDFContainer implements RDFContainer {

    private static final Logger LOGGER = Logger.getLogger(SesameRDFContainer.class.getName());

    private Repository repository;

    private ValueFactory valfac;

    private URI describedUri;

    public SesameRDFContainer(String uri) {
        initRepository();
        describedUri = valfac.createURI(uri);
    }

    public SesameRDFContainer(URI uri) {
        initRepository();
        describedUri = uri;
    }

    private void initRepository() {
        MemoryStore memoryStore = new MemoryStore();

        repository = new Repository(memoryStore);
        try {
            repository.initialize();
        }
        catch (SailInitializationException e) {
            // should never happen, indicates an internal error
            throw new RuntimeException(e);
        }

        // cannot happen before repository is initialized
        valfac = memoryStore.getValueFactory();
    }

    public URI getDescribedUri() {
        return describedUri;
    }

    public Repository getRepository() {
        return repository;
    }

    public void put(URI property, String value) {
        addInternal(describedUri, property, valfac.createLiteral(value));
    }

    public void put(URI property, Date value) {
        String date = RDFUtil.dateTime2String(value);
        addInternal(describedUri, property, valfac.createLiteral(date, XMLSchema.DATETIME));
    }

    public void put(URI property, Calendar value) {
        put(property, value.getTime());
    }

    public void put(URI property, boolean value) {
        String val = value ? "true" : "false";
        addInternal(describedUri, property, valfac.createLiteral(val, XMLSchema.BOOLEAN));
    }

    public void put(URI property, int value) {
        String val = Integer.toString(value);
        addInternal(describedUri, property, valfac.createLiteral(val, XMLSchema.INT));
    }

    public void put(URI property, long value) {
        String val = Long.toString(value);
        addInternal(describedUri, property, valfac.createLiteral(val, XMLSchema.LONG));
    }

    public void put(URI property, URI value) {
        addInternal(describedUri, property, value);
    }

    public String getString(URI property) {
        return getString(describedUri, property);
    }

    public Date getDate(URI property) {
        return getDate(describedUri, property);
    }

    public Calendar getCalendar(URI property) {
        return getCalendar(describedUri, property);
    }

    public Boolean getBoolean(URI property) {
        return getBoolean(describedUri, property);
    }

    public Integer getInteger(URI property) {
        return getInteger(describedUri, property);
    }

    public Long getLong(URI property) {
        return getLong(describedUri, property);
    }

    public URI getURI(URI property) {
        return getURI(describedUri, property);
    }

    public void add(URI subject, URI property, String value) {
        addInternal(subject, property, valfac.createLiteral(value));
    }

    public void add(URI subject, URI property, Date value) {
        String date = RDFUtil.dateTime2String(value);
        addInternal(subject, property, valfac.createLiteral(date, XMLSchema.DATETIME));
    }

    public void add(URI subject, URI property, Calendar value) {
        add(subject, property, value.getTime());
    }

    public void add(URI subject, URI property, boolean value) {
        String val = value ? "true" : "false";
        addInternal(subject, property, valfac.createLiteral(val, XMLSchema.BOOLEAN));
    }

    public void add(URI subject, URI property, int value) {
        String val = Integer.toString(value);
        addInternal(subject, property, valfac.createLiteral(val, XMLSchema.INT));
    }

    public void add(URI subject, URI property, long value) {
        String val = Long.toString(value);
        addInternal(subject, property, valfac.createLiteral(val, XMLSchema.LONG));
    }

    public void add(URI subject, URI property, URI value) {
        addInternal(subject, property, value);
    }

    public String getString(URI subject, URI property) {
        Value value = getInternal(subject, property);
        if (value instanceof Literal) {
            return ((Literal) value).getLabel();
        }
        else {
            return null;
        }
    }

    public Date getDate(URI subject, URI property) {
        String value = getString(subject, property);
        if (value == null) {
            return null;
        }
        else {
            try {
                return RDFUtil.string2DateTime(value);
            }
            catch (ParseException e) {
                // illegal date: interpret as no date available
                return null;
            }
        }
    }

    public Calendar getCalendar(URI subject, URI property) {
        Date date = getDate(subject, property);
        if (date == null) {
            return null;
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
    }

    public Boolean getBoolean(URI subject, URI property) {
        return new Boolean(getString(subject, property));
    }

    public Integer getInteger(URI subject, URI property) {
        String value = getString(subject, property);
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

    public Long getLong(URI subject, URI property) {
        String value = getString(subject, property);
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

    public URI getURI(URI subject, URI property) {
        Value value = getInternal(subject, property);
        if (value instanceof URI) {
            return (URI) value;
        }
        else {
            return null;
        }
    }

    private void addInternal(Resource subject, URI property, Value object) {
        try {
            repository.add(subject, property, object);
        }
        catch (SailUpdateException e) {
            LOGGER.info("cannot add statement: " + e);
            throw new RuntimeException(e);
        }
    }

    public void add(Statement statement) {
        try {
            repository.add(statement);
        }
        catch (SailUpdateException e) {
            LOGGER.info("cannot add statement: " + e);
            throw new RuntimeException(e);
        }
    }

    private Value getInternal(URI subject, URI property) {
        Collection statements = repository.getStatements(subject, property, null);
        if (statements.isEmpty()) {
            return null;
        }
        else {
            Statement firstStatement = (Statement) statements.iterator().next();
            return firstStatement.getObject();
        }
    }
}
