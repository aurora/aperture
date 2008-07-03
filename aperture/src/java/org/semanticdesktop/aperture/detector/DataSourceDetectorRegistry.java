/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.detector;

import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;

/**
 * An DataSourceDetectorRegistry serves as a central registry for 
 * registering and obtaining DataSourceDetectors.
 * @since 1.1.1
 * @author sauermann, 2.7.2008
 */
public interface DataSourceDetectorRegistry {

    /**
     * Adds a DataSourceDetector to this registry.
     */
    public void add(DataSourceDetector detector);

    /**
     * Removes a DataSourceDetector from this registry.
     */
    public void remove(DataSourceDetector detector);

    /**
     * Returns all DataSourceDetectors that support the specified DataSource type.
     * 
     * @return A Set of DataSourceDetectors whose supported types equal the specified type.
     * May be empty but never <code>null</code>
     */
    public Set<DataSourceDetector> get(URI type);

    /**
     * Returns all DataSourceDetectors registered in this DataSourceDetectorRegistry.
     * 
     * @return A Set of DataSourceDetector instances.
     * May be empty but never <code>null</code>
     */
    public Set<DataSourceDetector> getAll();
    

}

