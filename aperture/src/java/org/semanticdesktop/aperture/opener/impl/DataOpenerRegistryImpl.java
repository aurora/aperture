/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.opener.DataOpenerRegistry;


public class DataOpenerRegistryImpl implements DataOpenerRegistry {
	/**
     * A mapping from MIME types (Strings) to Sets of ExtractorFactories.
     */
    private HashMap factories = new HashMap();

    public void add(DataOpenerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator schemes = factory.getSupportedSchemes().iterator();
        while (schemes.hasNext()) {
            String scheme = (String) schemes.next();

            Set factorySet = (Set) factories.get(scheme);
            if (factorySet == null) {
                factorySet = new HashSet();
                factories.put(scheme, factorySet);
            }

            factorySet.add(factory);
        }
    }

    public void remove(DataOpenerFactory factory) {
        Iterator schemes = factory.getSupportedSchemes().iterator();
        while (schemes.hasNext()) {
            String scheme = (String) schemes.next();
            Set factorySet = (Set) factories.get(scheme);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    factories.remove(scheme);
                }
            }
        }
    }

    public Set get(String mimeType) {
        Set factorySet = (Set) factories.get(mimeType);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return new HashSet(factorySet);
        }
    }

    public Set getAll() {
        HashSet result = new HashSet();

        Iterator sets = factories.values().iterator();
        while (sets.hasNext()) {
            Set factorySet = (Set) sets.next();
            result.addAll(factorySet);
        }

        return result;
    }
}

