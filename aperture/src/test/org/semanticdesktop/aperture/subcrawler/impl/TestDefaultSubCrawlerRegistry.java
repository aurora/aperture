/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.impl;

import org.semanticdesktop.aperture.ApertureTestBase;

public class TestDefaultSubCrawlerRegistry extends ApertureTestBase {

    public void testRegistry() {
        DefaultSubCrawlerRegistry registry = new DefaultSubCrawlerRegistry();
        assertEquals(6, registry.getAll().size());
        assertEquals(1, registry.get("text/x-vcard").size());
        assertEquals(1, registry.get("application/zip").size());
        assertEquals(1, registry.get("application/gzip").size());
        assertEquals(1, registry.get("application/bzip2").size());
        assertEquals(1, registry.get("application/x-tar").size());
        assertEquals(1, registry.get("message/rfc822").size());
        assertEquals(1, registry.get("message/news").size());
    }
}
