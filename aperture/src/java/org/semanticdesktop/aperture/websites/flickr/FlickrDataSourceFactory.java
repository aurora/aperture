/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.websites.flickr;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * A factory of Flickr data source instances
 */
public class FlickrDataSourceFactory implements DataSourceFactory {

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		return FLICKRDS.FlickrDataSource;
	}

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new FlickrDataSource();
	}

	/**
	 * @see DataSourceFactory#getDescription(Model)
	 */
    public boolean getDescription(Model model) {
        FLICKRDS.getFLICKRDSOntology(model);
        return true;
    }

}
