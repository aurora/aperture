/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.excel;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class ExcelExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new ExcelExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "microsoft-excel-2000.xls", extractor);

        // check the extraction results
        checkStatement(NIE.plainTextContent, "spreadsheet", container);
        checkStatement(NIE.title, "Excel", container);
        checkStatement(NIE.subject, "document", container);
        checkStatement(NIE.description, "comments", container);
        checkStatement(NIE.generator, "Excel", container);
        checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.keyword, "rdf", container);
        validate(container);
        container.dispose();
    }
}
