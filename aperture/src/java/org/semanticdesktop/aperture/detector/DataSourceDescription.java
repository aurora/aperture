/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.detector;

import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A bean-like object that holds information about a detected datasource.
 * A DataSourceDetector returns instances of these objects.
 * This class may change in the future, when DataSourceDescription
 * has been used in practice. 
 * 
 * These will be the same for all detected datasources and we expect
 * no subclasses and only one implementation, no interface is needed.
 * @since 1.1.1
 * @author sauermann, 2.7.2008
 */
public class DataSourceDescription {
    
    DataSource datasource;
    
    /**
     * Create a new DataSourceDescription.
     * @param datasource
     */
    public DataSourceDescription(DataSource datasource) {
        super();
        this.datasource = datasource;
    }

    /**
     * Get the detected datasource
     * @return the detected datasource
     */
    public DataSource getDataSource() {
        return datasource;
    }

}

