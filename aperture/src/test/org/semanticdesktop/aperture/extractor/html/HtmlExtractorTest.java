/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

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
        RDFContainer container = extract(resourceName, extractor);
        checkStatement(NIE.plainTextContent, "text", container);
        checkStatement(NIE.title, "document", container);
        checkSimpleContact(NCO.creator, "Chris", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.keyword, "rdf", container);
        checkStatement(NIE.description, "testing", container);
        validate(container);
        container.dispose();
    }
}
