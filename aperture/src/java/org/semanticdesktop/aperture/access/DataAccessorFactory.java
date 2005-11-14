/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.access;

import java.util.Set;

/**
 * A DataAccessorFactory returns DataAccessors for a specific scheme.
 */
public interface DataAccessorFactory {

    /**
     * Returns all schemes supported by the DataAccessors returned by this DataAccessorFactory.
     * 
     * @return A Set of Strings.
     */
    public Set getSupportedSchemes();

    /**
     * Returns a DataAccessor instance for accessing the represented schemes.
     * 
     * @return A DataAccessor instance.
     */
    public DataAccessor get();
}