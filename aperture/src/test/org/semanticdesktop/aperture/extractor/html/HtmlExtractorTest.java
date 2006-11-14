/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class HtmlExtractorTest extends ExtractorTestBase {
    
    private static final String[] RESOURCES = {
        DOCS_PATH + "html-handwritten.html"
    };
    
    public void testExtraction() throws ExtractorException, IOException, ModelException {
        for (int i = 0; i < RESOURCES.length; i++) {
            testExtraction(RESOURCES[i]);
        }
    }

    private void testExtraction(String resourceName) throws ExtractorException, IOException, ModelException {
        ExtractorFactory factory = new HtmlExtractorFactory();
        Extractor extractor = factory.get();
        RDF2GoRDFContainer container = extract(resourceName, extractor);
        checkStatement(DATA.fullText, "text", container);
        checkStatement(DATA.title, "document", container);
        checkStatement(DATA.creator, "Chris", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.keyword, "rdf", container);
        checkStatement(DATA.description, "testing", container);
        container.dispose();
    }
}
