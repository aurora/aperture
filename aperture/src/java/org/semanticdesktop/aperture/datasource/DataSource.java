/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A DataSource defines the characteristics of a source from which DataObjects can be extracted. A
 * Datasource contains all information necessary to realize these objects, such as paths, usernames,
 * passwords, etc.
 */
public interface DataSource {

    /**
     * Gets the id of this data source.
     * 
     * @return A URI identifier for the data source.
     */
    public URI getID();

    /**
     * Set the ID of this data source.
     * 
     * @param id The new ID of this DataSource.
     */
    public void setID(URI id);

    /**
     * Gets the name of this data source.
     * 
     * @return A descriptive name for the data source.
     */
    public String getName();

    /**
     * Sets the name of this data source.
     * 
     * @param name A descriptive name for the data source.
     */
    public void setName(String name);

    /**
     * Gets the RDF model containing the configuration of this DataSource. The returned RDFContainer can
     * be modified in order to update the configuration.
     * 
     * @return A mutable RDFContainer.
     */
    public RDFContainer getConfiguration();
}
