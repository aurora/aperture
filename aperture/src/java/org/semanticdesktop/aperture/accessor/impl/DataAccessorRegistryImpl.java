/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;

public class DataAccessorRegistryImpl implements DataAccessorRegistry {

    private HashMap factories;
    
    public DataAccessorRegistryImpl() {
        factories = new HashMap();
    }
    
    public void add(DataAccessorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }
        
        Iterator schemes = factory.getSupportedSchemes().iterator();
        while (schemes.hasNext()) {
            String scheme = (String) schemes.next();
            
            Set set = (Set) factories.get(scheme);
            if (set == null) {
                set = new HashSet();
                factories.put(scheme, set);
            }
            
            set.add(factory);
        }
    }
    
    public void remove(DataAccessorFactory factory) {
        Iterator schemes = factory.getSupportedSchemes().iterator();
        while (schemes.hasNext()) {
            String scheme = (String) schemes.next();
            Set set = (Set) factories.get(scheme);
            if (set != null) {
                set.remove(factory);
                
                if (set.isEmpty()) {
                    factories.remove(scheme);
                }
            }
        }
    }

    public Set getAll() {
        HashSet result = new HashSet();
        
        Iterator sets = factories.values().iterator();
        while (sets.hasNext()) {
            result.addAll((Set) sets.next());
        }
        
        return result;
    }

    public Set get(String scheme) {
        Set set = (Set) factories.get(scheme);
        if (set == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return Collections.unmodifiableSet(set);
        }
    }
}
