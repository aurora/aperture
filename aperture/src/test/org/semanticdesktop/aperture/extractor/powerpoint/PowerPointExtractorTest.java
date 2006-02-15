/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class PowerPointExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new PowerPointExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "microsoft-powerpoint-2000.ppt", extractor);

        // check the extraction results
        checkStatement(AccessVocabulary.FULL_TEXT, "presentation", container);
        checkStatement(AccessVocabulary.FULL_TEXT, "2000", container);
        checkStatement(AccessVocabulary.FULL_TEXT, "notes", container);
        checkStatement(AccessVocabulary.TITLE, "Example", container);
        checkStatement(AccessVocabulary.SUBJECT, "document", container);
        checkStatement(AccessVocabulary.DESCRIPTION, "comments", container);
        checkStatement(AccessVocabulary.GENERATOR, "PowerPoint", container);
        checkStatement(AccessVocabulary.CREATOR, "Fluit", container);
        checkStatement(AccessVocabulary.KEYWORD, "test", container);
        checkStatement(AccessVocabulary.KEYWORD, "rdf", container);
    }        
}
