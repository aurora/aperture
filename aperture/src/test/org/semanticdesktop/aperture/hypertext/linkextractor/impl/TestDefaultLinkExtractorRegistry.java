/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultLinkExtractorRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultLinkExtractorRegistry registry = new DefaultLinkExtractorRegistry();
        assertEquals(1, registry.get("text/html").size());
    }
}

