/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.office;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class OfficeExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new OfficeExtractorFactory();
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
}
