/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;

/**
 * Returns FileAccessor instances. 
 */
public class FileAccessorFactory implements DataAccessorFactory {

    private static final Set SUPPORTED_SCHEMES = Collections.singleton("file");
    
    private FileAccessor accessor;

    private RDFContainerFactory containerFactory;
    
    public void setContainerFactory(RDFContainerFactory factory) {
        containerFactory = factory;
    }
    
    public RDFContainerFactory getContainerFactory() {
        return containerFactory;
    }
    
    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
    
    public DataAccessor get() {
        if (accessor == null) {
            // instance is statefull but does not change after initialization, hence can be shared
            accessor = new FileAccessor(containerFactory);
        }
        return accessor;
    }
}
