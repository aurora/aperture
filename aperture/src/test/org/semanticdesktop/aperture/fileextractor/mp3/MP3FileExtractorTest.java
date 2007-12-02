/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.mp3;

import java.io.IOException;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.fileextractor.FileExtractorException;
import org.semanticdesktop.aperture.fileextractor.FileExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NID3;

/**
 * Tests for the MP3FileExtractor
 */
public class MP3FileExtractorTest extends FileExtractorTestBase {
    public void testJingle1() throws FileExtractorException, IOException {
        MP3FileExtractor extractor = new MP3FileExtractor();
        RDFContainer metadata = extract(DOCS_PATH + "jingle1.mp3", extractor);
        Model model = metadata.getModel();
        assertTrue(model.contains(metadata.getDescribedUri(), RDF.type, NID3.ID3Audio));
    }
}

