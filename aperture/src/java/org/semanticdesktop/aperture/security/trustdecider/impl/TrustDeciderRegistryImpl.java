/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.security.trustdecider.TrustDeciderFactory;
import org.semanticdesktop.aperture.security.trustdecider.TrustDeciderRegistry;

/**
 * A trivial default implementation of the TrustDeciderFactory interface. 
 */
public class TrustDeciderRegistryImpl implements TrustDeciderRegistry {

    private HashSet factories;
    
    public TrustDeciderRegistryImpl() {
        factories = new HashSet(1);
    }
    
    public void add(TrustDeciderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory should not be null");
        }
        factories.add(factory);
    }

    public void remove(TrustDeciderFactory factory) {
        factories.remove(factory);
    }

    public Set getAll() {
        return Collections.unmodifiableSet(factories);
    }
}
