/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
