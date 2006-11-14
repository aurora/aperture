/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;

/**
 * A trivial default implementation of the CrawlerRegistry interface.
 */
public class CrawlerRegistryImpl implements CrawlerRegistry {

    /**
     * A mapping from DataSource types (URIs!) to Sets of CrawlerFactories.
     */
    private HashMap factories = new HashMap();

    public void add(CrawlerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator types = factory.getSupportedTypes().iterator();
        while (types.hasNext()) {
            URI type = (URI) types.next();

            Set factorySet = (Set) factories.get(type);
            if (factorySet == null) {
                factorySet = new HashSet();
                factories.put(type, factorySet);
            }

            factorySet.add(factory);
        }
    }

    public void remove(CrawlerFactory factory) {
        Iterator types = factory.getSupportedTypes().iterator();
        while (types.hasNext()) {
            URI type = (URI) types.next();
            Set factorySet = (Set) factories.get(type);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    factories.remove(type);
                }
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
}
