package org.semanticdesktop.aperture.webdav.accessor;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 *
 * Licensed under the Open Software License version 3.0..
 */

/**
 * @author Patrick Ernst
 * 
 **/

public class WebdavAccessorFactory implements DataAccessorFactory {

    private static final Set SUPPORTED_SCHEMES = Collections.singleton("webdavFile");
    
    private WebdavAccessor accessor;

    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
    
    public DataAccessor get() {
        if (accessor == null) {
            accessor = new WebdavAccessor();
        }
        return accessor;
    }
}