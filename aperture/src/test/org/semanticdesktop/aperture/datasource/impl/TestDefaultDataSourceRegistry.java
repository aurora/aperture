/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.impl;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

public class TestDefaultDataSourceRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultDataSourceRegistry registry = new DefaultDataSourceRegistry();
        assertEquals(1, registry.get(DATASOURCE_GEN.FileSystemDataSource).size());
        assertEquals(1, registry.get(DATASOURCE_GEN.WebDataSource).size());
        assertEquals(1, registry.get(DATASOURCE_GEN.IMAPDataSource).size());
    }
}

