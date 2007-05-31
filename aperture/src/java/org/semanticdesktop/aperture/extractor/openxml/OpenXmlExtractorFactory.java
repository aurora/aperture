/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.openxml;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class OpenXmlExtractorFactory implements ExtractorFactory {

    private static final Set MIME_TYPES;

    static {
        HashSet set = new HashSet();
        set.add("application/vnd.openxmlformats-officedocument.wordprocessingml");
        set.add("application/vnd.openxmlformats-officedocument.spreadsheetml");
        set.add("application/vnd.openxmlformats-officedocument.presentationml");

        MIME_TYPES = Collections.unmodifiableSet(set);
    }
    
    public Extractor get() {
    	return new OpenXmlExtractor();
    }

    public Set getSupportedMimeTypes() {
        return MIME_TYPES;
    }
}
