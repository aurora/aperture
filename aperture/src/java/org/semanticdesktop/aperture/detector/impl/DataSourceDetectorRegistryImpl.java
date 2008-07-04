/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.detector.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.detector.DataSourceDetectorRegistry;

/**
 * A trivial default implementation of the DataSourceDetectorRegistry interface.
 * @author sauermann, 2.7.2008
 * @since 1.1.1
 */
public class DataSourceDetectorRegistryImpl implements DataSourceDetectorRegistry {

    /**
     * A mapping from DataSource types (URIs!) to Sets of DataSourceDetector
     */
    private HashMap<URI, Set<DataSourceDetector>> registry = new HashMap<URI, Set<DataSourceDetector>>();
    
    /** Default constructor */
    public DataSourceDetectorRegistryImpl() {
        
    }
    
    public void add(DataSourceDetector detector) {
        if (detector == null) {
            throw new IllegalArgumentException("detector is not allowed to be null");
        }

        URI type = detector.getSupportedType();
        Set<DataSourceDetector> set = registry.get(type);
        if (set == null) {
            set = new HashSet<DataSourceDetector>();
            registry.put(type, set);
        }
        set.add(detector);
    }

    public void remove(DataSourceDetector detector) {
        URI type = detector.getSupportedType();
        Set<DataSourceDetector> set = registry.get(type);
        if (set != null) {
            set.remove(detector);

            if (set.isEmpty()) {
                registry.remove(type);
            }
        }
    }

    public Set<DataSourceDetector> get(URI type) {
        Set<DataSourceDetector> set = registry.get(type);
        if (set == null) {
            return Collections.emptySet();
        }
        else {
            return new HashSet<DataSourceDetector>(set);
        }
    }

    public Set<DataSourceDetector> getAll() {
        HashSet<DataSourceDetector> result = new HashSet<DataSourceDetector>();

        Iterator<Set<DataSourceDetector>> sets = registry.values().iterator();
        while (sets.hasNext()) {
            Set<DataSourceDetector> set = sets.next();
            result.addAll(set);
        }
        return result;
    }

}
