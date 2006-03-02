/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.impl;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

public class TestDefaultCrawlerRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultCrawlerRegistry registry = new DefaultCrawlerRegistry();
        assertEquals(1, registry.get(DATASOURCE_GEN.FileSystemDataSource).size());
    }
}

