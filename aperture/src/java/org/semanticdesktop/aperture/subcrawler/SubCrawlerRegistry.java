/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.util.Set;

/**
 * A  SubCrawlerRegistry serves as a central registry for registering and obtaining SubCrawlerFactories.
 */
public interface SubCrawlerRegistry {

    /**
     * Adds a SubCrawlerFactory to this registry.
     */
    public void add(SubCrawlerFactory factory);

    /**
     * Removes a SubCrawlerFactory from this registry.
     */
    public void remove(SubCrawlerFactory factory);

    /**
     * Returns all SubCrawlerFactories that support the specified mime type.
     * 
     * @return A Set of SubCrawlerFactories that support the specified type.
     */
    public Set get(String mimeType);

    
    /**
     * Returns all subcrawler factories that support the specified uri prefix.
     * @param prefix the uri prefix
     * @return a set of SubCrawlerFactories that support the specified prefix
     */
    public Set getByPrefix(String prefix);
    
    /**
     * Returns all SubCrawlerFactories registered in this SubCrawlerRegistry.
     * 
     * @return A Set of SubCrawlerFactory instances.
     */
    public Set getAll();
}
