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
    
    private ModelSet modelSet;
    
    private static Logger log = Logger.getLogger(DataSourceRegistryImpl.class.getName());

    public DataSourceRegistryImpl() {
        try {
        modelSet = RDF2Go.getModelFactory().createModelSet();
        Model sourceModel = modelSet.getModel(DATASOURCE.NS_DATASOURCE_GEN);
        OntologyUtil.getSourceOntology(sourceModel);
        sourceModel.close();
        
        Model sourceFormatModel = modelSet.getModel(SOURCEFORMAT.NS_SOURCEFORMAT_GEN);
        OntologyUtil.getSourceFormatOntology(sourceFormatModel);
        sourceFormatModel.close();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create a Data source registry impl",e);
        }
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
        Model descriptionModel = modelSet.getModel(factory.getSupportedType());
        factory.getDescription(descriptionModel);
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
        
        Model modelToRemove = modelSet.getModel(type);
        modelSet.removeModel(modelToRemove);
        modelToRemove.close();
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
            Iterator<? extends Model> iterator = modelSet.getModels();
            while (iterator.hasNext()) {
                Model modelToAdd = iterator.next();
                ClosableIterator<? extends Statement> closableIterator = null;
                try {
                    closableIterator = modelToAdd.iterator();
                    model.addAll(closableIterator);
                    closableIterator.close();
                } catch (ModelException me) {
                    log.log(Level.SEVERE, "Couldn't get data source description");
                } finally {
                    if (closableIterator != null) {
                        closableIterator.close();
                    }
                }
            }
        } catch (Exception me) {
            log.log(Level.SEVERE,"Couldnt get data source ontology and " +
                    "descriptions",me);
        }
    }
}
