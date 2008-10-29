/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerRegistry;

/**
 * A trivial default implementation of the SubCrawlerRegistry interface.
 */
public class SubCrawlerRegistryImpl implements SubCrawlerRegistry {

    /**
     * A mapping from MIME types (Strings) to Sets of SubCrawlerFactories.
     */
    private HashMap subCrawlerFactories = new HashMap();
    
    /**
     * A mapping from URI prefixes (Strings) to Sets of SubCrawlerFactories
     */
    private HashMap uriPrefixFactories = new HashMap();
    
    public void add(SubCrawlerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();

            Set factorySet = (Set) subCrawlerFactories.get(mimeType);
            if (factorySet == null) {
                factorySet = new HashSet();
                subCrawlerFactories.put(mimeType, factorySet);
            }

            factorySet.add(factory);
        }
        
        String prefix = factory.getUriPrefix();
        Set factorySet = (Set) uriPrefixFactories.get(prefix);
        if (factorySet == null) {
            factorySet = new HashSet();
            uriPrefixFactories.put(prefix, factorySet);
        }
        factorySet.add(factory);
    }

    public void remove(SubCrawlerFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set factorySet = (Set) subCrawlerFactories.get(mimeType);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    subCrawlerFactories.remove(mimeType);
                }
            }
        }
        String prefix = factory.getUriPrefix();
        Set factorySet = (Set) uriPrefixFactories.get(prefix);
        if (factorySet != null) {
            factorySet.remove(factory);

            if (factorySet.isEmpty()) {
                uriPrefixFactories.remove(prefix);
            }
        }
    }
        
    public Set get(String mimeType) {
        Set factorySet = (Set) subCrawlerFactories.get(mimeType);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return new HashSet(factorySet);
        }
    }
    
    public Set getByPrefix(String prefix) {
        Set factorySet = (Set) uriPrefixFactories.get(prefix);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return new HashSet(factorySet);
        }
    }

    public Set getAll() {
        HashSet result = new HashSet();

        Iterator sets = subCrawlerFactories.values().iterator();
        while (sets.hasNext()) {
            Set factorySet = (Set) sets.next();
            result.addAll(factorySet);
        }

        return result;
    }
}
