/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;


public class PdfExtractorFactory implements ExtractorFactory {

    private static final Set MIMETYPES;
    
    static {
        HashSet set = new HashSet();
        set.add("application/pdf");
        set.add("application/x-pdf");
        
        MIMETYPES = Collections.unmodifiableSet(set);
    }
    
    public PdfExtractor extractor;
    
    public Extractor get() {
        if (extractor == null) {
            extractor = new PdfExtractor();
        }
        return extractor;
    }

    public Set getSupportedMimeTypes() {
        return MIMETYPES;
    }
}
