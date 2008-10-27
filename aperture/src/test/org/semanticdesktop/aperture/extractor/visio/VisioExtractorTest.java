/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class VisioExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new VisioExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "microsoft-visio.vsd", extractor);
        // check the extraction results
        checkStatement(NIE.plainTextContent, "developers", container);
        checkStatement(NIE.title, "Title", container);
        checkStatement(NIE.subject, "Topic", container);
        checkStatement(NIE.description, "abstract", container);
        checkSimpleContact(NCO.creator, "TheAuthor-LeoSauermann", container);
        checkStatement(NIE.keyword, "visio", container);
        checkStatement(NIE.keyword, "aperture", container);
        validate(container);
        container.dispose();
    }        
}
