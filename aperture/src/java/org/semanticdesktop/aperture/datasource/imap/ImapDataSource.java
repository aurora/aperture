/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.imap;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * An ImapDataSource defines a collection of mails residing on an IMAP server.
 */
public class ImapDataSource extends DataSourceBase {

    public URI getType() {
        return DATASOURCE_GEN.IMAPDataSource;
    }
}
