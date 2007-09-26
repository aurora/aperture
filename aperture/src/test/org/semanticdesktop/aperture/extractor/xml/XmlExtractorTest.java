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
import org.semanticdesktop.aperture.vocabulary.NIE;

public class XmlExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-handwritten.xml", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "handwritten", container);
        checkStatement(NIE.plainTextContent, "Nested", container);
        checkStatement(NIE.plainTextContent, "More", container);
        checkStatement(NIE.plainTextContent, "value", container);
        validate(container);
        container.dispose();
    }
    
    public void testNonExistentDtd() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-nonexistent-dtd.xml", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "This is an XML document", container);
        validate(container);
        container.dispose();
    }
    
    public void testNonExistentRemoteDtd() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-nonexistent-remote-dtd.xml", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "This is an XML document", container);
        validate(container);
        container.dispose();
    }
    
    public void testNonExistentXsd() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-nonexistent-xsd.xml", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "This is an XML document", container);
        validate(container);
        container.dispose();
    }
    
    public void testNonExistentRemoteXsd() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new XmlExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "xml-nonexistent-remote-xsd.xml", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "This is an XML document", container);
        validate(container);
        container.dispose();
    }
}
