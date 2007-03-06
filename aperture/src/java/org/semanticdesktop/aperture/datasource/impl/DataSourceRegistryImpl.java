/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.util.ontology.OntologyUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.SOURCEFORMAT;

/**
 * A trivial default implementation of the DataSourceRegistry interface.
 */
public class DataSourceRegistryImpl implements DataSourceRegistry {

    /**
     * A mapping from DataSource types (URIs!) to Sets of DataSourceFactories
     */
    private HashMap factories = new HashMap();
    
    private static Logger log = Logger.getLogger(DataSourceRegistryImpl.class.getName());

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
            Iterator<DataSourceFactory> iterator = factories.values().iterator();
            while (iterator.hasNext()) {
                DataSourceFactory factory = iterator.next();
                factory.getDescription(model);
            }
        } catch (Exception me) {
            log.log(Level.SEVERE,"Couldnt get data source ontology and " +
                    "descriptions",me);
        }
    }
}
