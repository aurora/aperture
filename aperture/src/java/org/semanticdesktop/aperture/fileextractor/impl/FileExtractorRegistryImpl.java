/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.fileextractor.FileExtractorFactory;
import org.semanticdesktop.aperture.fileextractor.FileExtractorRegistry;

/**
 * A trivial default implementation of the FileExtractorRegistry interface.
 */
public class FileExtractorRegistryImpl implements FileExtractorRegistry {

    /**
     * A mapping from MIME types (Strings) to Sets of FileExtractorFactories.
     */
    private HashMap factories = new HashMap();

    public void add(FileExtractorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }

        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();

            Set factorySet = (Set) factories.get(mimeType);
            if (factorySet == null) {
                factorySet = new HashSet();
                factories.put(mimeType, factorySet);
            }

            factorySet.add(factory);
        }
    }

    public void remove(FileExtractorFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set factorySet = (Set) factories.get(mimeType);
            if (factorySet != null) {
                factorySet.remove(factory);

                if (factorySet.isEmpty()) {
                    factories.remove(mimeType);
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
