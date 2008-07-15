/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.mbox;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * ImapDataSourceFactory returns instances of the ImapDataSource class. 
 */
public class MboxDataSourceFactory implements DataSourceFactory {
    
    /**
     * @see DataSourceFactory#getSupportedType()
     */
	public URI getSupportedType() {
        return MBOXDS.MboxDataSource;
    }

	/**
	 * @see DataSourceFactory#newInstance()
	 */
    public DataSource newInstance() {
        return new MboxDataSource();
    }

    /**
     * @see DataSourceFactory#getDescription(Model)
     */
	public boolean getDescription(Model model) {
		MBOXDS.getMBOXDSOntology(model);
		return true;
	}
}
