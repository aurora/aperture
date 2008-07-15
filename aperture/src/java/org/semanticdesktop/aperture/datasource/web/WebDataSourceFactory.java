/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * A factory of WebDataSource instances. 
 */
public class WebDataSourceFactory implements DataSourceFactory {

    /**
     * @see DataSourceFactory#getSupportedType()
     */
	public URI getSupportedType() {
        return WEBDS.WebDataSource;
    }

	/**
	 * @see DataSourceFactory#newInstance()
	 */
    public DataSource newInstance() {
        return new WebDataSource();
    }

    /**
     * @see DataSourceFactory#getDescription(Model)
     */
	public boolean getDescription(Model model) {
		WEBDS.getWEBDSOntology(model);
		return true;
	}
}
