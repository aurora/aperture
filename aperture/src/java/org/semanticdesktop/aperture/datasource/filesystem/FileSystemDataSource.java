/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A FileSystemDataSource defines a collection of Files residing on a local or shared drive.
 */
public class FileSystemDataSource extends DataSourceBase {

    public URI getType() {
        return DATASOURCE_GEN.FileSystemDataSource;
    }
}
