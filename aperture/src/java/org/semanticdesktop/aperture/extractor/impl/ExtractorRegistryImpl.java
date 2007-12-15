/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;

/**
 * A trivial default implementation of the ExtractorRegistry interface.
 */
public class ExtractorRegistryImpl implements ExtractorRegistry {

    /**
     * A mapping from MIME types (Strings) to Sets of ExtractorFactories.
     */
    private HashMap extractorFactories = new HashMap();

    /**
     * A mapping from MIME types (Strings) to Sets of FileExtractorFactories.
     */
    private HashMap fileExtractorFactories = new HashMap();
    
    public void add(ExtractorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();

            Set factorySet = (Set) extractorFactories.get(mimeType);
            if (factorySet == null) {
                factorySet = new HashSet();
                extractorFactories.put(mimeType, factorySet);
            }

            factorySet.add(factory);
        }
    }
    
    public void add(FileExtractorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();

            Set factorySet = (Set) fileExtractorFactories.get(mimeType);
            if (factorySet == null) {
                factorySet = new HashSet();
                fileExtractorFactories.put(mimeType, factorySet);
            }

            factorySet.add(factory);
        }
    }

    public void remove(ExtractorFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set factorySet = (Set) extractorFactories.get(mimeType);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    extractorFactories.remove(mimeType);
                }
            }
        }
    }
    
    public void remove(FileExtractorFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set factorySet = (Set) fileExtractorFactories.get(mimeType);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    fileExtractorFactories.remove(mimeType);
                }
            }
        }
    }
    
    /**
     * @see ExtractorRegistryImpl#get(String)
     * @deprecated
     */
    public Set get(String mimeType) {
        return getExtractorFactories(mimeType);
    }

    public Set getExtractorFactories(String mimeType) {
        Set factorySet = (Set) extractorFactories.get(mimeType);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return new HashSet(factorySet);
        }
    }
    
    /**
     * @see ExtractorRegistryImpl#getAll()
     * @deprecated
     */
    public Set getAll() {
        return getAllExtractorFactories();
    }

    public Set getAllExtractorFactories() {
        HashSet result = new HashSet();

        Iterator sets = extractorFactories.values().iterator();
        while (sets.hasNext()) {
            Set factorySet = (Set) sets.next();
            result.addAll(factorySet);
        }

        return result;
    }

    public Set getAllFileExtractorFactories() {
        HashSet result = new HashSet();

        Iterator sets = fileExtractorFactories.values().iterator();
        while (sets.hasNext()) {
            Set factorySet = (Set) sets.next();
            result.addAll(factorySet);
        }

        return result;
    }

    public Set getFileExtractorFactories(String mimeType) {
        Set factorySet = (Set) fileExtractorFactories.get(mimeType);
        if (factorySet == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return new HashSet(factorySet);
        }
    }
}
