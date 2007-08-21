/*
 * Copyright (c) 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.openxml;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class OpenXmlExtractorTest extends ExtractorTestBase {

    private static final String WORD_PREFIX = "microsoft-word-2007beta2.";

    private static final String EXCEL_PREFIX = "microsoft-excel-2007beta2.";

    private static final String POWERPOINT_PREFIX = "microsoft-powerpoint-2007beta2.";

    // the .xlsb file is excluded from the unit tests as it contains the shared strings table in a binary
    // format that I didn't bother to master yet (see http://www.codeproject.com/useritems/office2007bin.asp
    // for more info on this subject)
    private static final String[] RESOURCES = { WORD_PREFIX + "docm", WORD_PREFIX + "docx",
            WORD_PREFIX + "dotm", WORD_PREFIX + "dotx",

            EXCEL_PREFIX + "xlam", EXCEL_PREFIX + "xlsm", EXCEL_PREFIX + "xlsx", EXCEL_PREFIX + "xltm",
            EXCEL_PREFIX + "xltx",

            POWERPOINT_PREFIX + "potm", POWERPOINT_PREFIX + "potx", POWERPOINT_PREFIX + "ppsm",
            POWERPOINT_PREFIX + "ppsx", POWERPOINT_PREFIX + "pptm", POWERPOINT_PREFIX + "pptx" };

    private static final String WORDS_DOC = DOCS_PATH + WORD_PREFIX + "docx";

    private static final String EXCEL_DOC = DOCS_PATH + EXCEL_PREFIX + "xlsx";

    private static final String POWERPOINT_DOC = DOCS_PATH + POWERPOINT_PREFIX + "pptx";

    public void testContentExtraction() throws ExtractorException, IOException, ModelException {
        // repeat for every example OpenDocument/OpenOffice document
        for (int i = 0; i < RESOURCES.length; i++) {
            // check of any document text is extracted
            RDFContainer container = getStatements(DOCS_PATH + RESOURCES[i]);
            checkStatement(NIE.plainTextContent, "This", container);
            validate(container,false);
            container.dispose();
        }
    }

    private RDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        ExtractorFactory factory = new OpenXmlExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }

    public void testMetadataExtraction() throws ExtractorException, IOException, ModelException {
        testMetadataExtraction(WORDS_DOC);
        testExcelExtraction(EXCEL_DOC);
        testMetadataExtraction(POWERPOINT_DOC);
    }

    private void testMetadataExtraction(String resourceName) throws ExtractorException, IOException,
            ModelException {
        // apply the extractor
        RDFContainer container = getStatements(resourceName);

        // check for all properties that we're sure of exist in this example document
        checkStatement(NIE.title, "Example", container);
        checkStatement(NIE.subject, "testing", container);
        checkStatement(NIE.keyword, "rdf", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.description, "comments", container);
        checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
        //checkStatement(DATA.date, "2006", container);
        checkStatement(NIE.contentCreated, "2006", container);
        validate(container,false);
        container.getModel().close();
    }
    
    private void testExcelExtraction(String resourceName) throws ExtractorException, IOException, ModelException {
        RDFContainer container = getStatements(resourceName);
        
        // check for all properties that we're sure of exist in this example document
        checkStatement(NIE.title, "Example", container);
        checkStatement(NIE.subject, "testing", container);
        checkStatement(NIE.keyword, "rdf", container);
        checkStatement(NIE.keyword, "test", container);
        checkStatement(NIE.description, "comments", container);
        checkSimpleContact(NCO.contributor, "Christiaan Fluit", container);
        //checkStatement(DATA.date, "2006", container);
        checkStatement(NIE.contentCreated, "2006", container);
        validate(container,false);
        container.getModel().close();
    }
}
