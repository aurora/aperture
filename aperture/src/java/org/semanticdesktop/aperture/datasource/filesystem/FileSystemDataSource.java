/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * A FileSystemDataSource defines a collection of Files residing on a local or shared drive.
 */
public class FileSystemDataSource extends DataSourceBase {

    public URI getType() {
        return Vocabulary.FILE_SYSTEM_DATA_SOURCE;
    }
}
