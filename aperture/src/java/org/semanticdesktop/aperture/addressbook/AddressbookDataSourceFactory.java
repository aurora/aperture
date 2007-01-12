/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class AddressbookDataSourceFactory implements DataSourceFactory {

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		
		return AddressbookDataSource.type;
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new AddressbookDataSource();
	}

	public boolean getDescription(Model model) {
		return false;
	}

}

