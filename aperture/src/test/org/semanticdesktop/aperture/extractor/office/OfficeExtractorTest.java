/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.office;

import java.io.IOException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class OfficeExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new OfficeExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "microsoft-word-2000.doc", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "Microsoft", container);
        checkStatement(DATA.title, "Word", container);
        checkStatement(DATA.subject, "document", container);
        checkStatement(DATA.description, "comments", container);
        checkStatement(DATA.generator, "Word", container);
        checkStatement(DATA.creator, "Fluit", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.keyword, "rdf", container);
    }        
}
