/*
 * Copyright (c) 2005 - 2008 Aduna.
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
        set.add("application/xhtml+xml");

        // FIXME: I added text/xml because our own MIME type identifier currently labels XHTML docs as such.
        // Ideally the application/xhtml+xml MIME type would handle these docs.
        set.add("text/xml");

        SUPPORTED_MIME_TYPES = Collections.unmodifiableSet(set);
    }

    public LinkExtractor get() {
        return new HtmlLinkExtractor();
    }

    public Set getSupportedMimeTypes() {
        return SUPPORTED_MIME_TYPES;
    }
}
