/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.xml;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class XmlExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "xml-handwritten.xml", extractor);

        // check the extraction results
        checkStatement(AccessVocabulary.FULL_TEXT, "handwritten", container);
        checkStatement(AccessVocabulary.FULL_TEXT, "Nested", container);
        checkStatement(AccessVocabulary.FULL_TEXT, "More", container);
        checkStatement(AccessVocabulary.FULL_TEXT, "value", container);
    }        
}
