/*
 * Copyright (c) 2006 Aduna.
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
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.DCES;

public class PublisherExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new PublisherExtractorFactory();
        Extractor extractor = factory.get();
        RDF2GoRDFContainer container = extract(DOCS_PATH + "microsoft-publisher-2003.pub", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "Example", container);
        checkStatement(DCES.title, "Publisher", container);
        checkStatement(DCES.subject, "document", container);
        checkStatement(DCES.description, "comments", container);
        checkStatement(DCES.creator, "Wester", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.keyword, "rdf", container);
        
        container.dispose();
    }        
}
