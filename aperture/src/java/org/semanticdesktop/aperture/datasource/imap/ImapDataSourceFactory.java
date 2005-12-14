/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.imap;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.Vocabulary;

/**
 * ImapDataSourceFactory returns instances of the ImapDataSource class. 
 */
public class ImapDataSourceFactory implements DataSourceFactory {

    public URI getSupportedType() {
        return Vocabulary.IMAP_DATA_SOURCE;
    }

    public DataSource newInstance() {
        return new ImapDataSource();
    }
}
