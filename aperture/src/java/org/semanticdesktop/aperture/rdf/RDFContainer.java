/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import java.util.Calendar;
import java.util.Date;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * RDFContainer defines a simple interface for small RDF stores. Its purpose is to make populating an
 * model as simple as possible for developers new to RDF and triple stores.
 * 
 * <p>
 * RDFContainers typically have a central URI called the "described URI". The semantics of this is that
 * the content of the RDFContainer describes properties of this URI. All put methods in this interface
 * implicitly use this URI as subject. However, RDFContainers may still contain any arbitrary statement,
 * it is not restricted in any way.
 * 
 * <p>
 * The get methods in this interface look for the first available triple that matches their requirements.
 * They return null when there are no such triples or when the handling of the first triple (e.g. the
 * parsing of a Date from a String) resulted in errors.
 * 
 * <p>
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

    public void put(URI property, String value);

    public void put(URI property, Date value);

    public void put(URI property, Calendar value);

    public void put(URI property, boolean value);

    public void put(URI property, int value);

    public void put(URI property, long value);

    public void put(URI property, URI value);

    public String getString(URI property);

    public Date getDate(URI property);

    public Calendar getCalendar(URI property);

    public Boolean getBoolean(URI property);

    public Integer getInteger(URI property);

    public Long getLong(URI property);

    public URI getURI(URI property);

    public void add(URI subject, URI property, String value);

    public void add(URI subject, URI property, Date value);

    public void add(URI subject, URI property, Calendar value);

    public void add(URI subject, URI property, boolean value);

    public void add(URI subject, URI property, int value);

    public void add(URI subject, URI property, long value);

    public void add(URI subject, URI property, URI value);

    public void add(Statement statement);

    public String getString(URI subject, URI property);

    public Date getDate(URI subject, URI property);

    public Calendar getCalendar(URI subject, URI property);

    public Boolean getBoolean(URI subject, URI property);

    public Integer getInteger(URI subject, URI property);

    public Long getLong(URI subject, URI property);

    public URI getURI(URI subject, URI property);
}
