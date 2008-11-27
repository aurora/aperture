/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.config;


public interface DomainBoundable {
    /**
     * Returns the domain boundaries for this domain boundable object
     * 
     * @return the domain boundaries for this domain boundable object
     */    
    public DomainBoundaries getDomainBoundaries();
}

