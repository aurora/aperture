/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * WebDataSourceFactory returns instances of the WebDataSource class. 
 */
public class WebDataSourceFactory implements DataSourceFactory {

    public URI getSupportedType() {
        return DATASOURCE_GEN.WebDataSource;
    }

    public DataSource newInstance() {
        return new WebDataSource();
    }

	public boolean getDescription(Model model) {
		return false;
	}
}
