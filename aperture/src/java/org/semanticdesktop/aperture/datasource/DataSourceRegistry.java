/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;

/**
 * An DataSourceRegistry serves as a central registry for registering and obtaining DataSourceFactories.
 */
public interface DataSourceRegistry {

    /**
     * Adds a DataSourceFactory to this registry.
     */
    public void add(DataSourceFactory factory);

    /**
     * Removes a DataSourceFactory from this registry.
     */
    public void remove(DataSourceFactory factory);

    /**
     * Returns all DataSourceFactories that support the specified DataSource type.
     * 
     * @return A Set of DataSourceFactories whose supported types equal the specified type.
     */
    public Set get(URI type);

    /**
     * Returns all DataSourceFactories registered in this DataSourceRegistry.
     * 
     * @return A Set of DataSourceFactory instances.
     */
    public Set getAll();
}
