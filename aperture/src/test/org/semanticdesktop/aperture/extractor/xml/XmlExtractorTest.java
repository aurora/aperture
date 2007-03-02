/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.xml;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class XmlExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-handwritten.xml", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "handwritten", container);
        checkStatement(DATA.fullText, "Nested", container);
        checkStatement(DATA.fullText, "More", container);
        checkStatement(DATA.fullText, "value", container);
        
        container.dispose();
    }        
}
