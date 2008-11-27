/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.bibsonomy;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * A factory of bibsonomy data sources.
 */
public class BibsonomyDataSourceFactory implements DataSourceFactory {

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		return BIBSONOMYDS.BibsonomyDataSource;
	}

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new BibsonomyDataSource();
	}

	/**
	 * @see DataSourceFactory#getDescription(Model)
	 */
    public boolean getDescription(Model model) {
        BIBSONOMYDS.getBIBSONOMYDSOntology(model);
        return true;
    }

}
