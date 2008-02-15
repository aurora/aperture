/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultSubCrawlerRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultSubCrawlerRegistry registry = new DefaultSubCrawlerRegistry();
        assertEquals(0, registry.getAll().size());
    }
}
