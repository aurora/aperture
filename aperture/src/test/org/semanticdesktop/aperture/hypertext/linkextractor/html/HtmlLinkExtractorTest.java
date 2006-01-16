/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;

public class HtmlLinkExtractorTest extends ApertureTestBase {

    private InputStream stream;

    public void setUp() {
        stream = ClassLoader
                .getSystemResourceAsStream("org/semanticdesktop/aperture/hypertext/linkextractor/html/test.html");
    }

    public void tearDown() throws IOException {
        stream.close();
    }

    public void testNavigationLinks() throws IOException {
        List links = getLinks(Boolean.TRUE);
        assertEquals(2, links.size());
        assertTrue(links.contains("http://example.com/absolute/link"));
        assertTrue(links.contains("http://example.com/relative/link"));
    }
    
    public void testAllLinks() throws IOException {
        List links = getLinks(Boolean.FALSE);
        assertEquals(3, links.size());
        assertTrue(links.contains("http://example.com/absolute/link"));
        assertTrue(links.contains("http://example.com/relative/link"));
        assertTrue(links.contains("http://example.com/image"));
    }
    
    private List getLinks(Boolean navLinksOnly) throws IOException {
        HtmlLinkExtractor extractor = new HtmlLinkExtractor();
        HashMap params = new HashMap();
        params.put(LinkExtractor.NAVIGATIONAL_LINKS_ONLY_KEY, navLinksOnly);
        return extractor.extractLinks(stream, params);
    }
}
