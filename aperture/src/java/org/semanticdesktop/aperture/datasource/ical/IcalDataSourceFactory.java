/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.ical;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * ImapDataSourceFactory returns instances of the ImapDataSource class. 
 */
public class IcalDataSourceFactory implements DataSourceFactory {
    
    /**
     * @see DataSourceFactory#getSupportedType()
     */
	public URI getSupportedType() {
        return ICALDS.IcalDataSource;
    }

	/**
	 * @see DataSourceFactory#newInstance()
	 */
    public DataSource newInstance() {
        return new IcalDataSource();
    }

    /**
     * @see DataSourceFactory#getDescription(Model)
     */
	public boolean getDescription(Model model) {
		ICALDS.getICALDSOntology(model);
		return true;
	}
}
