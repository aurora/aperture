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

    public void put(URI property, URI value);

    public void add(URI subject, URI property, String value);

    public void add(URI subject, URI property, Date value);

    public void add(URI subject, URI property, Calendar value);

    public void add(URI subject, URI property, boolean value);

    public void add(URI subject, URI property, int value);

    public void add(URI subject, URI property, URI value);

    public void add(Statement statement);
}
