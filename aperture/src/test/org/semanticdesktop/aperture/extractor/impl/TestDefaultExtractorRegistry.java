/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultExtractorRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultExtractorRegistry registry = new DefaultExtractorRegistry();
        assertEquals(1, registry.getExtractorFactories("text/plain").size());
        assertEquals(1, registry.getExtractorFactories("text/html").size());
        assertEquals(1, registry.getExtractorFactories("application/pdf").size());
        assertEquals(1, registry.getExtractorFactories("application/vnd.oasis.opendocument.text").size());
        assertEquals(1, registry.getExtractorFactories("application/vnd.sun.xml.writer").size());
        assertEquals(1, registry.getExtractorFactories("image/jpg").size());
        assertEquals(1, registry.getFileExtractorFactories("audio/mpeg").size());
    }
}
