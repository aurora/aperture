/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource;

import java.util.Set;

import org.ontoware.rdf2go.model.Model;
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
    
    /**
     * Generates a graph containing the datasource ontology (the vocabulary used to configure data sources),
     * the sourceformat ontology (vocabulary used to specify widgets for the UI) and descriptions of data
     * source types registered within this registry. These descriptions use the Fresnel Display Vocabulary.
     * Each one comprises a fresnel:Lens instance, with a list of all configuration properties needed by a
     * crawler for this data source. Each configuration property has a fresnel:Format that specifies the
     * widget that is to be used in the user interface, the widgets are chosen from those specified in the
     * Aperture sourceformat ontology. This graph is stored in the provided model.
     * 
     * @throws Exception if something goes wrong in the process.
     */
    public void getDataSourceOntologyAndDescriptions(Model model);
}
