/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class VisioExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new VisioExtractorFactory();
        Extractor extractor = factory.get();
        RDF2GoRDFContainer container = extract(DOCS_PATH + "microsoft-visio.vsd", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "developers", container);
        checkStatement(DATA.title, "Title", container);
        checkStatement(DATA.subject, "Topic", container);
        checkStatement(DATA.description, "abstract", container);
        checkStatement(DATA.creator, "Leo", container);
        checkStatement(DATA.keyword, "visio", container);
        checkStatement(DATA.keyword, "aperture", container);
        
        container.dispose();
    }        
}
