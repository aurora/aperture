/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.SourceVocabulary;

/**
 * WebDataSourceFactory returns instances of the WebDataSource class. 
 */
public class WebDataSourceFactory implements DataSourceFactory {

    public URI getSupportedType() {
        return SourceVocabulary.WEB_DATA_SOURCE;
    }

    public DataSource newInstance() {
        return new WebDataSource();
    }
}
