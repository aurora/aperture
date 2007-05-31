/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener;

import java.util.Set;


public interface DataOpenerRegistry {
	 /**
     * Register a DataAccessorFactory.
     */
    public void add(DataOpenerFactory factory);

    /**
     * Removes a registered DataAccessorFactory.
     */
    public void remove(DataOpenerFactory factory);

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

