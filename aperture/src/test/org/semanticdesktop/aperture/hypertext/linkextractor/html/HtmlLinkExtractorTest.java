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

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class HtmlLinkExtractorTest extends ApertureTestBase {

    private InputStream stream;

    private static final String TEST_RESOURCE = "org/semanticdesktop/aperture/hypertext/linkextractor/html/test.html"; 

    private static final String CONDENAST_RESOURCE = "org/semanticdesktop/aperture/docs/condenast.html";
    
    public void tearDown() throws IOException {
        stream.close();
    }

    public void testNavigationLinks() throws IOException {
        List links = getLinks(Boolean.FALSE, TEST_RESOURCE);
        assertEquals(2, links.size());
        assertTrue(links.contains("http://example.com/absolute/link"));
        assertTrue(links.contains("http://example.com/relative/link"));
    }

    public void testAllLinks() throws IOException {
        List links = getLinks(Boolean.TRUE, TEST_RESOURCE);
        assertEquals(3, links.size());
        assertTrue(links.contains("http://example.com/absolute/link"));
        assertTrue(links.contains("http://example.com/relative/link"));
        assertTrue(links.contains("http://example.com/image"));
    }
    
    public void testCondeNast() throws IOException {
        List links = getLinks(Boolean.FALSE, DOCS_PATH + "html-condenast.html");
        // this file will yield faulty urls, but there should be 43 of them
        // some may not be valid URIs 
        assertEquals(links.size(), 43);
    }
    
    public void testQuelleDe() throws Exception {
        List links = getLinks(Boolean.FALSE, DOCS_PATH + "html-quelle.de.html");
        // this file will yield faulty urls, but there should be 72 of them
        // some may not be valid URIs 
        assertEquals(links.size(), 72);
    }

    private List getLinks(Boolean includeEmbeddedResources, String resource) throws IOException {
        stream = ResourceUtil.getInputStream(resource, HtmlLinkExtractorTest.class);
        HtmlLinkExtractor extractor = new HtmlLinkExtractor();
        HashMap params = new HashMap();
        params.put(LinkExtractor.INCLUDE_EMBEDDED_RESOURCES_KEY, includeEmbeddedResources);
        return extractor.extractLinks(stream, params);
    }
}
