/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultDataAccessorRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultDataAccessorRegistry registry = new DefaultDataAccessorRegistry();
        assertNotNull(registry.get("file"));
    }
}

