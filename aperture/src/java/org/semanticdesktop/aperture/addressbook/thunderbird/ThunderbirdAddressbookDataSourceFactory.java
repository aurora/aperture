/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * ThunderbirdAddressbookDataSourceFactory returns instances of the ThunderbirdAddressbookDataSource class. 
 */
public class ThunderbirdAddressbookDataSourceFactory implements DataSourceFactory {
    
	public URI getSupportedType() {
        return THUNDERBIRDADDRESSBOOKDS.ThunderbirdAddressbookDataSource;
    }

    public DataSource newInstance() {
        return new ThunderbirdAddressbookDataSource();
    }

	public boolean getDescription(Model model) {
	    THUNDERBIRDADDRESSBOOKDS.getTHUNDERBIRDADDRESSBOOKDSOntology(model);
	    return true;
	}
}
