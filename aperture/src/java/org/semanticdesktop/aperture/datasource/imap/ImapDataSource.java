/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.imap;

import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * An ImapDataSource defines a collection of mails residing on an IMAP server.
 */
public class ImapDataSource extends DataSourceBase {

    public org.openrdf.model.URI getType() {
        return Vocabulary.IMAP_DATA_SOURCE;
    }
}
