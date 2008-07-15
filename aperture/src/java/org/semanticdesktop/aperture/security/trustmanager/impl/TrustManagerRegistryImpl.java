/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.security.trustmanager.TrustManagerFactory;
import org.semanticdesktop.aperture.security.trustmanager.TrustManagerRegistry;

/**
 * A trivial default implementation of the TrustManagerRegistry interface.
 */
public class TrustManagerRegistryImpl implements TrustManagerRegistry {

    private HashSet factories;

    public TrustManagerRegistryImpl() {
        factories = new HashSet(1);
    }

    public void add(TrustManagerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory should not be null");
        }
        factories.add(factory);
    }

    public void remove(TrustManagerFactory factory) {
        factories.remove(factory);
    }

    public Set getAll() {
        return Collections.unmodifiableSet(factories);
    }
}
