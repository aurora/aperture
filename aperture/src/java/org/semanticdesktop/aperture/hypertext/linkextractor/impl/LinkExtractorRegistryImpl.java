/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorRegistry;

public class LinkExtractorRegistryImpl implements LinkExtractorRegistry {

    /**
     * A mapping from MIME types (Strings) to Sets of LinkExtractorFactories.
     */
    private HashMap factories;
    
    public LinkExtractorRegistryImpl() {
        factories = new HashMap();
    }
    
    public void add(LinkExtractorFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory is not allowed to be null");
        }
        
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            
            Set set = (Set) factories.get(mimeType);
            if (set == null) {
                set = new HashSet();
                factories.put(mimeType, set);
            }
            
            set.add(factory);
        }
    }

    public void remove(LinkExtractorFactory factory) {
        Iterator mimeTypes = factory.getSupportedMimeTypes().iterator();
        while (mimeTypes.hasNext()) {
            String mimeType = (String) mimeTypes.next();
            Set set = (Set) factories.get(mimeType);
            if (set != null) {
                set.remove(factory);
                
                if (set.isEmpty()) {
                    factories.remove(mimeType);
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

    public Set get(String mimeType) {
        Set set = (Set) factories.get(mimeType);
        if (set == null) {
            return Collections.EMPTY_SET;
        }
        else {
            return Collections.unmodifiableSet(set);
        }
    }
}
