/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;

/**
 * A HtmlLinkExtractorFactory returns instances of HtmlLinkExtractor.
 */
public class HtmlLinkExtractorFactory implements LinkExtractorFactory {

    private static final Set SUPPORTED_MIME_TYPES;
    
    static {
        HashSet set = new HashSet();
        set.add("text/html");
        set.add("application/xhtml+xml");  // found at http://www.hixie.ch/advocacy/xhtml

        SUPPORTED_MIME_TYPES = Collections.unmodifiableSet(set);
    }
    
    public LinkExtractor get() {
        return new HtmlLinkExtractor();
    }
    
    public Set getSupportedMimeTypes() {
        return SUPPORTED_MIME_TYPES;
    }
}
