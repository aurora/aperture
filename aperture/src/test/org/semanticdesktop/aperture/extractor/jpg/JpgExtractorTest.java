/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.jpg;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Syntax;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NEXIF;

import com.drew.imaging.jpeg.JpegProcessingException;

public class JpgExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-exif-img_9367.JPG", extractor);

        // check the extraction results
        checkStatement(NEXIF.width, "100", container);
        checkStatement(NEXIF.height, "67", container);
        checkStatement(NEXIF.flash, "16", container);
        validate(container);
        container.dispose();
    }

    public void testZeroLength() throws ExtractorException, IOException {
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-exif-zerolength.jpg", extractor);
        validate(container);
        container.dispose();
    }
}
