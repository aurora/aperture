/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
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

