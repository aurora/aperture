/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.SourceVocabulary;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * A WebDataSource defines a collection of resources in a web site.
 */
public class WebDataSource extends DataSourceBase {

    public URI getType() {
        return SourceVocabulary.WEB_DATA_SOURCE;
    }
}
