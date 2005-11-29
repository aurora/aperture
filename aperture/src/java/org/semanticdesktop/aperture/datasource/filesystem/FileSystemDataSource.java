/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * A FileSystemDataSource defines a collection of Files residing on a local or shared drive.
 */
public class FileSystemDataSource extends DataSourceBase {

    public org.openrdf.model.URI getType() {
        return Vocabulary.FILE_SYSTEM_DATA_SOURCE;
    }
    
    public void setRootFile(File rootFile) {
        setRootUrl(rootFile.toURI().toString());
    }

    public File getRootFile() {
        String rootUrl = getRootUrl();
        if (rootUrl == null) {
            return null;
        }

        URI uri = null;
        try {
            uri = new URI(rootUrl);
        }
        catch (URISyntaxException e) {
            return null;
        }

        return new File(uri);
    }
}
