/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider;

import java.util.Set;

/**
 * A TrustDeciderRegistry maintains the set of known TrustDeciderFactories.
 */
public interface TrustDeciderRegistry {

    /**
     * Adds a TrustDeciderFactory.
     */
    public void add(TrustDeciderFactory factory);

    /**
     * Removes a TrustDeciderFactory.
     */
    public void remove(TrustDeciderFactory factory);

    /**
     * Returns all registered TrustDeciderFactories.
     * 
     * @return A set of TrustDeciderFactory instances.
     */
    public Set getAll();
}
