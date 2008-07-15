/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * Creates Microsoft Outlook Datasources.
 */
public class OutlookDataSourceFactory implements DataSourceFactory {

    /**
     * @see DataSourceFactory#getSupportedType()
     */
    public URI getSupportedType() {
        return OUTLOOKDS.OutlookDataSource;
    }

    /**
     * @see DataSourceFactory#newInstance()
     */
    public DataSource newInstance() {
        return new OutlookDataSource();
    }

    /**
     * @see DataSourceFactory#getDescription(Model)
     */
    public boolean getDescription(Model model) {
        OUTLOOKDS.getOUTLOOKDSOntology(model);
        return true;
    }
}
