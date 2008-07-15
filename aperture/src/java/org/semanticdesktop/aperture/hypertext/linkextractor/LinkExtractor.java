/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A LinkExtractor extracts links from a document, e.g. the anchors inside a HTML document.
 * Implementations are typically MIME type-specific.
 * 
 * <p>
 * The resulting list of links is returned as a collection of Strings rather than URLs in order to allow
 * for any kind of scheme to be used without having to provide a URLStreamHandler for that scheme.
 */
public interface LinkExtractor {

    /**
     * Suggested key to use in the params map to indicate the base URL with which relative URLs can be
     * resolved. The corresponding value should be a String holding the base URL.
     */
    public static final Object BASE_URL_KEY = "baseUrl";

    /**
     * Suggested key to use in the params map to indicate that non-navigational links should als be
     * extracted, e.g. embedded images, background, stylesheets, etc. The corresponding value should be a
     * Boolean.
     */
    public static final Object INCLUDE_EMBEDDED_RESOURCES_KEY = "includeEmbeddedResources";

    /**
     * Extracts all links occurring in the specified stream.
     * 
     * @param stream The input stream containing the content from which the links should be extracted,
     *            e.g. an HTML document.
     * @param params An optional set of parameters to guide the link extraction process.
     * @return A List of Strings representing the encountered links in the order in which they were
     *         encountered in the document.
     * @throws Exception When an error occurred during processing of the document stream.
     */
    public List extractLinks(InputStream stream, Map params) throws Exception;
}
