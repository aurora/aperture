/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class OpenDocumentExtractorFactory implements ExtractorFactory {

    // see http://framework.openoffice.org/documentation/mimetypes/mimetypes.html for more info on
    // OpenOffice/OpenDocument mimetypes

    private static final Set MIMETYPES;

    static {
        HashSet set = new HashSet();

        // all OpenDocument mimetypes
        set.add("application/vnd.oasis.opendocument.text");
        set.add("application/vnd.oasis.opendocument.text-template");
        set.add("application/vnd.oasis.opendocument.text-master");
        set.add("application/vnd.oasis.opendocument.text-web");
        set.add("application/vnd.oasis.opendocument.spreadsheet");
        set.add("application/vnd.oasis.opendocument.spreadsheet-template");
        set.add("application/vnd.oasis.opendocument.graphics");
        set.add("application/vnd.oasis.opendocument.graphics-template");
        set.add("application/vnd.oasis.opendocument.presentation");
        set.add("application/vnd.oasis.opendocument.presentation-template");
        set.add("application/vnd.oasis.opendocument.image");
        set.add("application/vnd.oasis.opendocument.image-template");
        set.add("application/vnd.oasis.opendocument.formula");
        set.add("application/vnd.oasis.opendocument.formula-template");
        set.add("application/vnd.oasis.opendocument.chart");
        set.add("application/vnd.oasis.opendocument.chart-template");

        // all OpenOffice 1.x and StarOffice 6.x/7.x mimetypes
        set.add("application/vnd.sun.xml.writer");
        set.add("application/vnd.sun.xml.writer.template");
        set.add("application/vnd.sun.xml.writer.global");
        set.add("application/vnd.sun.xml.calc");
        set.add("application/vnd.sun.xml.calc.template");
        set.add("application/vnd.sun.xml.draw");
        set.add("application/vnd.sun.xml.draw.template");
        set.add("application/vnd.sun.xml.impress");
        set.add("application/vnd.sun.xml.impress.template");
        set.add("application/vnd.sun.xml.math");

        MIMETYPES = Collections.unmodifiableSet(set);
    }

    public OpenDocumentExtractor extractor;

    public Extractor get() {
        if (extractor == null) {
            extractor = new OpenDocumentExtractor();
        }
        return extractor;
    }

    public Set getSupportedMimeTypes() {
        return MIMETYPES;
    }
}
