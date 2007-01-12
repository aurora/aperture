/*
 * Copyright (c) 2005 - 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;


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
		return DATASOURCE_GEN.MicrosoftOutlookDataSource;
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new OutlookDataSource();
	}

	public boolean getDescription(Model model) {
		return false;
	}

}

