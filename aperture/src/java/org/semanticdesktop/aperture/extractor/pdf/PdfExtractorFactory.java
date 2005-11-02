/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;


public class PdfExtractorFactory implements ExtractorFactory {

    public PdfExtractor extractor;
    
    public Extractor get() {
        if (extractor == null) {
            extractor = new PdfExtractor();
        }
        return extractor;
    }

    public Set getSupportedMimeTypes() {
        return Collections.singleton("application/pdf");
    }
}
