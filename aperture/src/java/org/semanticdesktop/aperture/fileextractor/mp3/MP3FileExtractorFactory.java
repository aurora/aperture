/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.mp3;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.fileextractor.FileExtractor;
import org.semanticdesktop.aperture.fileextractor.FileExtractorFactory;

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

