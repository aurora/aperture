/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.impl;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.datasource.Vocabulary;

public class TestDefaultCrawlerRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultCrawlerRegistry registry = new DefaultCrawlerRegistry();
        assertEquals(1, registry.get(Vocabulary.FILE_SYSTEM_DATA_SOURCE).size());
    }
}

