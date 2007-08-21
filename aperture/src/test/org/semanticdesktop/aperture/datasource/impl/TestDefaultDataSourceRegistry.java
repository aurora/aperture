/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.impl;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.datasource.filesystem.FILESYSTEMDS;
import org.semanticdesktop.aperture.datasource.imap.IMAPDS;
import org.semanticdesktop.aperture.datasource.web.WEBDS;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

public class TestDefaultDataSourceRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultDataSourceRegistry registry = new DefaultDataSourceRegistry();
        assertEquals(1, registry.get(FILESYSTEMDS.FileSystemDataSource).size());
        assertEquals(1, registry.get(WEBDS.WebDataSource).size());
        assertEquals(1, registry.get(IMAPDS.ImapDataSource).size());
    }
}

