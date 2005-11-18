/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
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
        checkStatement(Vocabulary.FULL_TEXT, "text", container);
        checkStatement(Vocabulary.TITLE, "document", container);
        checkStatement(Vocabulary.CREATOR, "Chris", container);
        checkStatement(Vocabulary.KEYWORD, "test", container);
        checkStatement(Vocabulary.KEYWORD, "rdf", container);
        checkStatement(Vocabulary.DESCRIPTION, "testing", container);
    }
}
