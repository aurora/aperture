/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.mime.identifier.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultMimeTypeIdentifierRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultMimeTypeIdentifierRegistry registry = new DefaultMimeTypeIdentifierRegistry();
        assertFalse(registry.getAll().isEmpty());
    }
}

