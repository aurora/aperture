/*
 * Copyright (c) 2006 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class PowerPointExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new PowerPointExtractorFactory();
        Extractor extractor = factory.get();
        RDF2GoRDFContainer container = extract(DOCS_PATH + "microsoft-powerpoint-2000.ppt", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "presentation", container);
        checkStatement(DATA.fullText, "2000", container);
        checkStatement(DATA.fullText, "notes", container);
        checkStatement(DATA.title, "Example", container);
        checkStatement(DATA.subject, "document", container);
        checkStatement(DATA.description, "comments", container);
        checkStatement(DATA.generator, "PowerPoint", container);
        checkStatement(DATA.creator, "Fluit", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.keyword, "rdf", container);
        container.dispose();
    }        
}
