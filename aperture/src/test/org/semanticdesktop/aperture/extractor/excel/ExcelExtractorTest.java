/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.excel;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class ExcelExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException {
        // apply the extractor on an example file
        ExtractorFactory factory = new ExcelExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(DOCS_PATH + "microsoft-excel-2000.xls", extractor);

        // check the extraction results
        checkStatement(AccessVocabulary.FULL_TEXT, "spreadsheet", container);
        checkStatement(AccessVocabulary.TITLE, "Excel", container);
        checkStatement(AccessVocabulary.SUBJECT, "document", container);
        checkStatement(AccessVocabulary.DESCRIPTION, "comments", container);
        checkStatement(AccessVocabulary.GENERATOR, "Excel", container);
        checkStatement(AccessVocabulary.CREATOR, "Fluit", container);
        checkStatement(AccessVocabulary.KEYWORD, "test", container);
        checkStatement(AccessVocabulary.KEYWORD, "rdf", container);
    }        
}
