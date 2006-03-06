/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.excel;

import java.io.IOException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class ExcelExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new ExcelExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "microsoft-excel-2000.xls", extractor);

        // check the extraction results
        checkStatement(DATA.fullText, "spreadsheet", container);
        checkStatement(DATA.title, "Excel", container);
        checkStatement(DATA.subject, "document", container);
        checkStatement(DATA.description, "comments", container);
        checkStatement(DATA.generator, "Excel", container);
        checkStatement(DATA.creator, "Fluit", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.keyword, "rdf", container);
    }        
}
