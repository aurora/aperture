/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.util.XmlSafetyUtils;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class WordExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new WordExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "microsoft-word-2000.doc", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "Microsoft", container);
        checkStatement(NIE.title, "Word", container);
        checkStatement(NIE.subject, "document", container);
        checkStatement(NIE.description, "comments", container);
        checkStatement(NIE.generator, "Word", container);
        checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.keyword, "rdf", container);
        validate(container);
        container.dispose();
    }
    
    /**
     * Tests the files gathered in the course of investigating the issue 1976336
     * @throws Exception
     */
    public void testXmlSafeExtraction() throws Exception {
        ExtractorFactory factory = new WordExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainerFactory fac = new RDFContainerFactoryImpl();
        RDFContainerFactory xmlsafefac = XmlSafetyUtils.wrapXmlSafeRDFContainerFactory(fac);
        
        RDFContainer container1 = extract(DOCS_PATH +"microsoft-word-illegal-unicode-characters.doc",extractor,xmlsafefac);
        testXmlSafety(container1.getModel());
        
        RDFContainer container2 = extract(DOCS_PATH +"microsoft-word-testdoc-comments.doc",extractor,xmlsafefac);
        testXmlSafety(container2.getModel());
        
        RDFContainer container3 = extract(DOCS_PATH +"microsoft-word-testdoc-nocomments.doc",extractor,xmlsafefac);
        testXmlSafety(container3.getModel());
        
        validate(container1);
        validate(container2);
        validate(container3);
        container1.dispose();
        container2.dispose();
        container3.dispose();
    }
}
