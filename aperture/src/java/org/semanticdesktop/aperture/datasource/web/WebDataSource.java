/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A WebDataSource defines a collection of resources in a web site.
 */
public class WebDataSource extends DataSourceBase {

    public URI getType() {
        return DATASOURCE_GEN.WebDataSource;
    }
}
