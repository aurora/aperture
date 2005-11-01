/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import java.net.URI;
import java.util.Date;

import org.openrdf.model.Statement;

public interface RDFContainer {

    /**
     * Get the identifier of the resource that is described by the contents of this RDFContainer.
     * 
     * @return The URI of the described resource.
     */
    public URI getDataObjectUri();

    public void put(URI property, String value);

    public void put(URI property, Date value);

    public void put(URI property, boolean value);

    public void put(URI property, int value);

    public void put(URI property, URI value);

    public void add(URI subject, URI property, String value);

    public void add(URI subject, URI property, Date value);

    public void add(URI subject, URI property, boolean value);

    public void add(URI subject, URI property, int value);

    public void add(URI subject, URI property, URI value);

    public void put(org.openrdf.model.URI property, String value);

    public void put(org.openrdf.model.URI property, Date value);

    public void put(org.openrdf.model.URI property, boolean value);

    public void put(org.openrdf.model.URI property, int value);

    public void put(org.openrdf.model.URI property, org.openrdf.model.URI value);

    public void add(Statement statement);

    /**
     * Return the raw RDF object, this is a RDF graph object from another api like sesame or jena.
     * 
     * @return The graph api.
     */
    public Object getRawRDF();

    /**
     * Return the raw RDF resource that represents the DataObject, this is a RDF Resource object from
     * another api like sesame or jena.
     * 
     * @return The graph api.
     */
    public Object getRawResource();

}
