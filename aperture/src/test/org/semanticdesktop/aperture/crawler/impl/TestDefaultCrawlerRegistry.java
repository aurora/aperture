/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.crawler.impl;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.datasource.filesystem.FILESYSTEMDS;

public class TestDefaultCrawlerRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultCrawlerRegistry registry = new DefaultCrawlerRegistry();
        assertEquals(1, registry.get(FILESYSTEMDS.FileSystemDataSource).size());
    }
}

