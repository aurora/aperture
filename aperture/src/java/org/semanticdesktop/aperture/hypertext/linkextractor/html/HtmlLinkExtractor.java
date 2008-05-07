/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;

/**
 * A LinkExtractor implementation that can extract links from HTML documents.
 */
public class HtmlLinkExtractor implements LinkExtractor, TokenHandler {

    private URL baseURL;

    // indicates whether embedded images, backgrounds, etc., also need to be returned
    private boolean includeEmbeddedResources;

    private ArrayList links;

    private String startTag;

    // maps Strings to Strings
    private HashMap attributes = new HashMap();

    public synchronized List extractLinks(InputStream inputStream, Map params) throws IOException {
        // initialize variables
        Tokenizer tokenizer = new Tokenizer(this);
        links = new ArrayList();
        baseURL = null;
        includeEmbeddedResources = false;

        // fetch parameters
        Object value = params.get(BASE_URL_KEY);
        if (value instanceof URL) {
            baseURL = (URL) value;
        }
        else if (value instanceof String) {
            baseURL = new URL((String) value);
        }

        value = params.get(INCLUDE_EMBEDDED_RESOURCES_KEY);
        if (value instanceof Boolean) {
            includeEmbeddedResources = ((Boolean) value).booleanValue();
        }

        // parse the stream
        tokenizer.read(inputStream);

        // return the extracted links
        ArrayList result = links;
        links = null;
        return result;
    }

    public void startDocument() {
        // ignore
    }

    public void endDocument() {
        // ignore
    }

    public void startOfStartTag(String name) {
        startTag = name.toUpperCase();
    }

    public void endOfStartTag() {
        ArrayList localLinks = new ArrayList();

        // handle the tag and its attributes
        if ("BASE".equals(startTag)) {
            String href = (String) attributes.get("HREF");
            if (href != null) {
                try {
                    baseURL = new URL(href);
                }
                catch (MalformedURLException e) {
                    // ignore
                }
            }
        }
        else if ("META".equals(startTag)) {
            String content = (String) attributes.get("CONTENT");
            String httpEquiv = (String) attributes.get("HTTP-EQUIV");

            if (content != null && httpEquiv != null && httpEquiv.trim().equalsIgnoreCase("REFRESH")) {
                // This META tag contains a refresh URL
                String contentInLC = content.toLowerCase();

                int urlIndex = contentInLC.indexOf("url");
                if (urlIndex != -1) {
                    // URL starts after "url="
                    urlIndex += 3;
                }
                int commaIndex = contentInLC.indexOf(',');
                int semiColonIndex = contentInLC.indexOf(';');

                int maxIndex = Math.max(urlIndex, commaIndex);
                maxIndex = Math.max(maxIndex, semiColonIndex);

                if (maxIndex != -1) {
                    content = content.substring(maxIndex + 1);
                }
                localLinks.add(content);
            }
        }
        else if ("A".equals(startTag)) {
            localLinks.add(attributes.get("HREF"));
        }
        else if ("FRAME".equals(startTag) || "IFRAME".equals(startTag)) {
            localLinks.add(attributes.get("SRC"));
            localLinks.add(attributes.get("LONGDESC"));
        }
        else if ("HEAD".equals(startTag)) {
            localLinks.add(attributes.get("PROFILE"));
        }
        else if ("AREA".equals(startTag)) {
            localLinks.add(attributes.get("HREF"));
        }
        else if ("Q".equals(startTag) || "BLOCKQUOTE".equals(startTag) || "INS".equals(startTag)
                || "DEL".equals(startTag)) {
            localLinks.add(attributes.get("CITE"));
        }
        else if ("LINK".equals(startTag)) {
            if (includeEmbeddedResources) {
                localLinks.add(attributes.get("HREF"));
                localLinks.add(attributes.get("SRC"));
            }
        }
        else if ("LAYER".equals(startTag) || "ILAYER".equals(startTag)) {
            localLinks.add(attributes.get("SRC"));

            if (includeEmbeddedResources) {
                localLinks.add(attributes.get("BACKGROUND"));
            }
        }
        else if ("BODY".equals(startTag) || "TABLE".equals(startTag) || "TR".equals(startTag)
                || "TH".equals(startTag) || "TD".equals(startTag)) {
            if (includeEmbeddedResources) {
                localLinks.add(attributes.get("BACKGROUND"));
            }
        }
        else if ("IMG".equals(startTag)) {
            if (includeEmbeddedResources) {
                localLinks.add(attributes.get("SRC"));
                localLinks.add(attributes.get("LOWSRC"));
            }

            localLinks.add(attributes.get("LONGDESC"));
            localLinks.add(attributes.get("USEMAP"));
        }
        else if ("INPUT".equals(startTag)) {
            if (includeEmbeddedResources) {
                localLinks.add(attributes.get("SRC"));
            }
            localLinks.add(attributes.get("USEMAP"));
        }

        // post-process all encountered links
        int nrLinks = localLinks.size();
        for (int i = 0; i < nrLinks; i++) {
            // check for non-null value
            String link = (String) localLinks.get(i);
            if (link == null) {
                continue;
            }

            // resolve entities
            link = EntityResolver.resolveEntities(link);

            // resolve against base URL (only works for schemes for which a URLStreamHandler is registered)
            if (baseURL != null) {
                try {
                    URL url = new URL(baseURL, link);
                    link = url.toExternalForm();
                }
                catch (MalformedURLException e) {
                    // ignore
                }
            }

            links.add(link);
        }

        // prepare for next invocation
        attributes.clear();
    }

    public void endTag(String name) {
        // ignore
    }

    public void attribute(String name) {
        // ignore
    }

    public void attribute(String name, String value) {
        attributes.put(name.toUpperCase(), value);
    }

    public void text(String text) {
        // ignore
    }

    public void comment(String comment) {
        // ignore
    }

    public void docType(String name, String sysId, String fpi, String uri) {
        // ignore
    }

    public void error(String message) {
        // ignore
    }
}
