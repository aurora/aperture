/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.IOException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.model.Vocabulary;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class HtmlExtractorTest extends ExtractorTestBase {
    
    private static final String[] RESOURCES = {
        DOCS_PATH + "html-handwritten.html"
    };
    
    public void testExtraction() throws ExtractorException, IOException {
        for (int i = 0; i < RESOURCES.length; i++) {
            testExtraction(RESOURCES[i]);
        }
    }

    private void testExtraction(String resourceName) throws ExtractorException, IOException {
        ExtractorFactory factory = new HtmlExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(resourceName, extractor);
        checkStatement(Vocabulary.FULL_TEXT_URI, "text", container);
        checkStatement(Vocabulary.TITLE_URI, "document", container);
        checkStatement(Vocabulary.CREATOR_URI, "Chris", container);
        checkStatement(Vocabulary.KEYWORD_URI, "test", container);
        checkStatement(Vocabulary.KEYWORD_URI, "rdf", container);
        checkStatement(Vocabulary.DESCRIPTION_URI, "testing", container);
    }
}
