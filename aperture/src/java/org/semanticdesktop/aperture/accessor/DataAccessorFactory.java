/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor;

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