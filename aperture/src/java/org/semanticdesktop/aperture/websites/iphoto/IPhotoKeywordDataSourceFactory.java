/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.websites.iphoto;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * A factory of IPhotoKeywordDataSource instances.
 */
public class IPhotoKeywordDataSourceFactory implements DataSourceFactory {

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		return IPHOTODS.IPhotoKeywordDataSource;
	}

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new IPhotoKeywordDataSource();
	}

	/**
	 * @see DataSourceFactory#getDescription(Model)
	 */
    public boolean getDescription(Model model) {
        IPHOTODS.getIPHOTODSOntology(model);
        return true;
    }
}
