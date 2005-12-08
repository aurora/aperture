/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager;

import java.util.Set;

/**
 * A TrustManagerRegistry maintains the set of known TrustManagerFactories.
 */
public interface TrustManagerRegistry {

    /**
     * Add a TrustManagerFactory to this TrustManagerRegistry.
     */
    public void add(TrustManagerFactory factory);

    /**
     * Remove a TrustManagerFactory from this TrustManagerRegistry.
     */
    public void remove(TrustManagerFactory factory);

    /**
     * Returns all registered TrustManagerFactories.
     * 
     * @return A Set of TrustManagerFactory instances.
     */
    public Set getAll();
}
