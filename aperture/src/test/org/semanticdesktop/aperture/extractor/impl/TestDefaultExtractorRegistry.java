/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultExtractorRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultExtractorRegistry registry = new DefaultExtractorRegistry();
        assertEquals(1, registry.get("text/plain").size());
        assertEquals(1, registry.get("text/html").size());
        assertEquals(1, registry.get("application/pdf").size());
        assertEquals(1, registry.get("application/vnd.oasis.opendocument.text").size());
        assertEquals(1, registry.get("application/vnd.sun.xml.writer").size());
    }
}
