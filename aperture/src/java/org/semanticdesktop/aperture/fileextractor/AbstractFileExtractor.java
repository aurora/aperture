/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor;

import java.io.File;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A common superclass for all file extractors. Its purpose is to check if a file exists and if it's readable
 * before the actual extraction can commence.
 */
public abstract class AbstractFileExtractor implements FileExtractor {

    /**
     * Extracts metadata from a file. This method checks if the file exists and if it's readable before
     * calling the method that performs the actual extraction.
     * 
     * @see FileExtractor#extract(URI, File, Charset, String, RDFContainer)
     */
    public void extract(URI id, File file, Charset charset, String mimeType, RDFContainer result)
            throws FileExtractorException {
        if (!file.exists()) {
            throw new FileExtractorException("File not found: " + file.getPath());
        }
        else if (!file.canRead()) {
            throw new FileExtractorException("File not readable: " + file.getPath());
        }
        else {
            performExtraction(id, file, charset, mimeType, result);
        }

    }

    protected abstract void performExtraction(URI id, File file, Charset charset, String mimeType,
            RDFContainer result) throws FileExtractorException;

}
