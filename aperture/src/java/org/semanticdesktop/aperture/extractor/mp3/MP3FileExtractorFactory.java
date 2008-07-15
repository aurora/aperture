/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mp3;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;

/**
 * A FileExtractorFactory implementation for the MP3FileExtractors
 */
public class MP3FileExtractorFactory implements FileExtractorFactory {

    private static final Set MIME_TYPES = Collections.singleton("audio/mpeg");
    
    /**
     * @see FileExtractorFactory#get()
     */
    public FileExtractor get() {
        return new MP3FileExtractor();
    }

    /**
     * @see FileExtractorFactory#getSupportedMimeTypes()
     */
    public Set getSupportedMimeTypes() {
        return MIME_TYPES;
    }

}

