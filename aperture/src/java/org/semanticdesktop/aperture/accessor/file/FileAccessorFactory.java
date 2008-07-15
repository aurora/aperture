/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;

/**
 * Returns FileAccessor instances. 
 */
public class FileAccessorFactory implements DataAccessorFactory {

    private static final Set SUPPORTED_SCHEMES = Collections.singleton("file");
    
    private FileAccessor accessor;

    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
    
    public DataAccessor get() {
        if (accessor == null) {
            accessor = new FileAccessor();
        }
        return accessor;
    }
}
