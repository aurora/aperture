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
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.fileextractor.FileExtractorException;
import org.semanticdesktop.aperture.fileextractor.FileExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NID3;

/**
 * Tests for the MP3FileExtractor
 */
public class ID3V2_3_0Test extends FileExtractorTestBase {

    /**
     * The jingle3.mp3 contains some ID3 2.3.0 frames inserted with the kid3 tool
     */
    public void testJingle3() throws Exception {
        MP3FileExtractor extractor = new MP3FileExtractor();
        RDFContainer metadata = extract(DOCS_PATH + "jingle3.mp3", extractor);
        checkStatement(RDF.type, NID3.ID3Audio, metadata);
        checkStatement(NID3.title, "Aperture Jingle 3", metadata);
        checkStatement(NID3.leadArtist, "Antoni the Lead Artist Mylka", metadata);
        checkStatement(NID3.albumTitle, "The Aperture test album", metadata);
        checkStatement(NID3.comments, "The comment", metadata);
    }   
}
