/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class VisioExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new VisioExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "microsoft-visio.vsd", extractor);

        // check the extraction results
        checkStatement(AccessVocabulary.FULL_TEXT, "developers", container);
        checkStatement(AccessVocabulary.TITLE, "Title", container);
        checkStatement(AccessVocabulary.SUBJECT, "Topic", container);
        checkStatement(AccessVocabulary.DESCRIPTION, "abstract", container);
        checkStatement(AccessVocabulary.CREATOR, "Leo", container);
        checkStatement(AccessVocabulary.KEYWORD, "visio", container);
        checkStatement(AccessVocabulary.KEYWORD, "aperture", container);
    }        
}
