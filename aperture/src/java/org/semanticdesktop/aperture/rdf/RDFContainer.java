/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

/**
 * RDFContainer defines a simple interface for RDF stores. Its purpose is to make populating an model as
 * simple as possible for developers new to RDF and triple stores.
 * 
 * <p>
 * RDFContainers typically have a central URI called the "described URI". The semantics of this is that the
 * content of the RDFContainer describes properties of this URI. All put methods in this interface implicitly
 * use this URI as subject. Furthermore, they adhere to Map semantics as much as possible, e.g. invoking "put"
 * a second time with the same property key overwrites any previously set value. When the underlying model
 * contains multiple properties that have the same subject and property, a MultipleValuesException is thrown.
 * Care therefore has to be taken when mixing use of the Map-like put and get properties with the
 * triple-oriented add and get methods.
 * 
 * <p>
 * Putting a layer between the code generating the statements and the code that stores the statements provides
 * an additional benefit: decisions on how Java types such as ints and Dates are transformed into RDF triples
 * are now made in a single piece of code (the RDFContainer implementation), without requiring the populators
 * of the RDF model to address this issue.
 */
public interface RDFContainer {

    /**
     * Get the identifier of the resource that is described by the contents of this RDFContainer.
     * 
     * @return The URI of the described resource.
     */
    public URI getDescribedUri();

    /**
     * Get the underlying RDF2Go model holding the RDF statements.
     */
    public Model getModel();

    /**
     * Get a ValueFactory with which RDF2Go datatype instances can be made.
     */
    public ValueFactory getValueFactory();

    /**
     * State that this container won't be used anymore and it can perform any cleanup necessary. Examples of
     * actions taken by this method might include closing the connection to an underyling RDF store or freeing
     * any system resources this particular implementation might own
     */
    public void dispose();

    /* Map-oriented methods that automatically take the described URI as subject */

    public void put(URI property, String value) throws UpdateException;

    public void put(URI property, Date value) throws UpdateException;

    public void put(URI property, Calendar value) throws UpdateException;

    public void put(URI property, boolean value) throws UpdateException;

    public void put(URI property, int value) throws UpdateException;

    public void put(URI property, long value) throws UpdateException;

    public void put(URI property, Node value) throws UpdateException;

    public void add(URI property, String value) throws UpdateException;

    public void add(URI property, Date value) throws UpdateException;

    public void add(URI property, Calendar value) throws UpdateException;

    public void add(URI property, boolean value) throws UpdateException;

    public void add(URI property, int value) throws UpdateException;

    public void add(URI property, long value) throws UpdateException;

    public void add(URI property, Node value) throws UpdateException;

    public String getString(URI property);

    public Date getDate(URI property);

    public Calendar getCalendar(URI property);

    public Boolean getBoolean(URI property);

    public Integer getInteger(URI property);

    public Long getLong(URI property);

    public URI getURI(URI property);

    public Node getNode(URI property);

    public void remove(URI property) throws UpdateException;

    // returns a Collection of Nodes
    public Collection getAll(URI property);

    /* Statement-oriented methods */

    public void add(Statement statement) throws UpdateException;

    public void remove(Statement statement) throws UpdateException;
}
