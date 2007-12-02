/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.mp3;

import java.io.File;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.fileextractor.FileExtractor;
import org.semanticdesktop.aperture.fileextractor.FileExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NID3;

/**
 * A file extractor implementation for MP3 files.
 */
public class MP3FileExtractor implements FileExtractor {

    /**
     * @see FileExtractor#extract(URI, File, Charset, String, RDFContainer)
     */
    public void extract(URI id, File file, Charset charset, String mimeType, RDFContainer result)
            throws FileExtractorException {
        result.add(RDF.type,NID3.ID3Audio);
    }

}

