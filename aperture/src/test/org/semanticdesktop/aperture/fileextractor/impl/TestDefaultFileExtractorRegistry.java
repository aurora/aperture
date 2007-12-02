/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultFileExtractorRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultFileExtractorRegistry registry = new DefaultFileExtractorRegistry();
        assertEquals(registry.getAll().size(),0);
    }
}
