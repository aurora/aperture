/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * FileSystemDataSourceFactory returns instances of the FileSystemDataSource class. 
 */
public class FileSystemDataSourceFactory implements DataSourceFactory {

    public URI getSupportedType() {
        return FileSystemDataSource.FILE_SYSTEM_DATA_SOURCE_TYPE;
    }

    public DataSource newInstance() {
        return new FileSystemDataSource();
    }
}
