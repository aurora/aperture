/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/**
 * Create Microsoft Outlook Datasources
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookDataSourceFactory implements DataSourceFactory {

	/**
	 * 
	 */
	public OutlookDataSourceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		return DATASOURCE.MicrosoftOutlookDataSource;
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new OutlookDataSource();
	}

}

