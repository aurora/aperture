/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * RDFContainer defines a simple interface for small RDF stores. Its purpose is to make populating an
 * model as simple as possible for developers new to RDF and triple stores.
 * 
 * <p>
 * RDFContainers typically have a central URI called the "described URI". The semantics of this is that
 * the content of the RDFContainer describes properties of this URI. All put methods in this interface
 * implicitly use this URI as subject. Furthermore, they adhere to Map semantics as much as possible,
 * e.g. invoking "put" a second time with the same property key overwrites any previously set value. When
 * the underlying model contains multiple properties that have the same subject and property, a
 * MultipleValuesException is thrown. Care therefore has to be taken when mixing use of the Map-like put
 * and get properties with the triple-oriented add and get methods.
 * 
 * <p>
 * extra bonus: can transparently use a different triple store by switching implementations. FIXME FIXME
 * FIXME
 * 
 * Putting a layer between the code generating the statements and the code that stores the statements
 * provides an additional benefit: decisions on how Java types such as ints and Dates are transformed
 * into RDF triples are now made in a single piece of code (the RDFContainer implementation), without
 * requiring the populators of the RDF model to address this issue.
 */
public interface RDFContainer {

    /**
     * Get the identifier of the resource that is described by the contents of this RDFContainer.
     * 
     * @return The URI of the described resource.
     */
    public URI getDescribedUri();

    /**
     * Get the underlying RDF model holding the RDF statements. Examples of RDF models are a Sesame
     * Repository or a Jena Graph.
     */
    public Object getModel();

    // Map-oriented methods that automatically take the described URI as subject

    public void put(URI property, String value);

    public void put(URI property, Date value);

    public void put(URI property, Calendar value);

    public void put(URI property, boolean value);

    public void put(URI property, int value);

    public void put(URI property, long value);

    public void put(URI property, Value value);

    public void add(URI property, String value);

    public void add(URI property, Date value);

    public void add(URI property, Calendar value);

    public void add(URI property, boolean value);

    public void add(URI property, int value);

    public void add(URI property, long value);

    public void add(URI property, Value value);

    public String getString(URI property);

    public Date getDate(URI property);

    public Calendar getCalendar(URI property);

    public Boolean getBoolean(URI property);

    public Integer getInteger(URI property);

    public Long getLong(URI property);

    public URI getURI(URI property);

    public Value getValue(URI property);

    public void remove(URI property);

    // returns a Collection of Values
    public Collection getAll(URI property);

    // // Triple-oriented methods
    //
    // public void add(URI subject, URI property, String value);
    //
    // public void add(URI subject, URI property, Date value);
    //
    // public void add(URI subject, URI property, Calendar value);
    //
    // public void add(URI subject, URI property, boolean value);
    //
    // public void add(URI subject, URI property, int value);
    //
    // public void add(URI subject, URI property, long value);
    //
    // public void add(URI subject, URI property, URI value);
    //
    // public String getString(URI subject, URI property);
    //
    // public Date getDate(URI subject, URI property);
    //
    // public Calendar getCalendar(URI subject, URI property);
    //
    // public Boolean getBoolean(URI subject, URI property);
    //
    // public Integer getInteger(URI subject, URI property);
    //
    // public Long getLong(URI subject, URI property);
    //
    // public URI getURI(URI subject, URI property);
    //
    // public void remove(URI subject, URI property);
    //
    // public Collection getAll(URI subject, URI property);

    // Statement-oriented methods

    public void add(Statement statement);

    public void remove(Statement statement);
}
