/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * FileSystemDataSourceFactory returns instances of the FileSystemDataSource class. 
 */
public class FileSystemDataSourceFactory implements DataSourceFactory {

    public URI getSupportedType() {
        return DATASOURCE_GEN.FileSystemDataSource;
    }

    public DataSource newInstance() {
        return new FileSystemDataSource();
    }
}
