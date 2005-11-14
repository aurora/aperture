/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.access;

import java.util.Set;

/**
 * A DataAccessorRegistry keeps track of the available DataAccessorFactories.
 */
public interface DataAccessorRegistry {

    /**
     * Register a DataAccessorFactory.
     */
    public void add(DataAccessorFactory factory);

    /**
     * Removes a registered DataAccessorFactory.
     */
    public void remove(DataAccessorFactory factory);

    /**
     * Get all registered DataAccessorFactories.
     */
    public Set getAll();

    /**
     * Get all DataAccessorFactories capable of handling the specified scheme.
     * 
     * @param scheme A scheme, e.g. "http" or "file".
     * @return A set of DataAccessorFactories for the specified scheme.
     */
    public Set get(String scheme);
}
