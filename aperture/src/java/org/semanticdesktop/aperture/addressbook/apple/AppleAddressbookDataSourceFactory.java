/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.apple;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * AppleAddressbookDataSourceFactory returns instances of the AppleAddressbookDataSource class. 
 */
public class AppleAddressbookDataSourceFactory implements DataSourceFactory {
    
	public URI getSupportedType() {
        return APPLEADDRESSBOOKDS.AppleAddressbookDataSource;
    }

    public DataSource newInstance() {
        return new AppleAddressbookDataSource();
    }

	public boolean getDescription(Model model) {
		APPLEADDRESSBOOKDS.getAPPLEADDRESSBOOKDSOntology(model);
		return true;
	}
}
