/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;

/**
 * Returns HttpAccessor instances. 
 */
@SuppressWarnings("unchecked")
public class HttpAccessorFactory implements DataAccessorFactory {

    private static final Set SUPPORTED_SCHEMES;
    
    static {
        HashSet set = new HashSet();
        set.add("http");
        set.add("https");
        SUPPORTED_SCHEMES = Collections.unmodifiableSet(set);
    }
    
    private HttpAccessor accessor;

    /**
     * @see DataAccessorFactory#getSupportedSchemes()
     */
    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
    
    /**
     * @see DataAccessorFactory#get()
     */
    public DataAccessor get() {
        if (accessor == null) {
            accessor = new HttpAccessor();
        }
        return accessor;
    }
}
