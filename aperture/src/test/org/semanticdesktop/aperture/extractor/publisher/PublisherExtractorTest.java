/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.publisher;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class PublisherExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new PublisherExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "microsoft-publisher-2003.pub", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "Example", container);
        checkStatement(NIE.title, "Publisher", container);
        checkStatement(NIE.subject, "document", container);
        checkStatement(NIE.description, "comments", container);
        checkSimpleContact(NCO.creator, "Jeroen Wester", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.keyword, "rdf", container);
        validate(container);
        container.dispose();
    }        
}
