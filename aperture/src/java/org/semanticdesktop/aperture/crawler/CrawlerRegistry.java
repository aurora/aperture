/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.crawler;

import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;

/**
 * An CrawlerRegistry serves as a central registry for registering and obtaining CrawlerFactories.
 */
public interface CrawlerRegistry {

    /**
     * Adds a CrawlerFactory to this registry.
     */
    public void add(CrawlerFactory factory);

    /**
     * Removes a CrawlerFactory from this registry.
     */
    public void remove(CrawlerFactory factory);

    /**
     * Returns all CrawlerFactories that support the specified DataSource type.
     * 
     * @return A Set of CrawlerFactories whose supported types contain the specified type.
     */
    public Set get(URI type);

    /**
     * Returns all CrawlerFactories registered in this CrawlerRegistry.
     * 
     * @return A Set of CrawlerFactory instances.
     */
    public Set getAll();
}
