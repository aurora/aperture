/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.util.ontology.OntologyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A trivial default implementation of the DataSourceRegistry interface.
 */
public class DataSourceRegistryImpl implements DataSourceRegistry {

    /**
     * A mapping from DataSource types (URIs!) to Sets of DataSourceFactories
     */
    private HashMap factories = new HashMap();
    
    /** Default constructor */
    public DataSourceRegistryImpl() {
        
    }
    
    public void add(DataSourceFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        URI type = factory.getSupportedType();
        Set factorySet = (Set) factories.get(type);
        if (factorySet == null) {
            factorySet = new HashSet();
            factories.put(type, factorySet);
        }

        factorySet.add(factory);
    }

    public void remove(DataSourceFactory factory) {
        URI type = factory.getSupportedType();
        Set factorySet = (Set) factories.get(type);
        if (factorySet != null) {
            factorySet.remove(factory);

            if (factorySet.isEmpty()) {
                factories.remove(type);
            }
        }
    }

    public Set get(URI type) {
        Set factorySet = (Set) factories.get(type);
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

    public void getDataSourceOntologyAndDescriptions(Model model) {
        try {
            OntologyUtil.getSourceFormatOntology(model);
            OntologyUtil.getSourceOntology(model);
            Iterator iterator = factories.values().iterator();
            while (iterator.hasNext()) {
                Set set = (Set)iterator.next();
                for (Object factoryObject : set) {
                    DataSourceFactory factory = (DataSourceFactory)factoryObject;
                    factory.getDescription(model);
                }
            }
        } catch (Exception me) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Could not get data source ontology and descriptions",me);
        }
    }
}
