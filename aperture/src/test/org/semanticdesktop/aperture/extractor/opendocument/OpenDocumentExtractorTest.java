/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.io.IOException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.model.Vocabulary;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class OpenDocumentExtractorTest extends ExtractorTestBase {

    private static final String[] RESOURCES = {
        DOCS_PATH + "openoffice-2.0-calc.ods",
        DOCS_PATH + "openoffice-2.0-calc-template.ots",
        DOCS_PATH + "openoffice-2.0-draw.odg",
        DOCS_PATH + "openoffice-2.0-draw-template.otg",
        DOCS_PATH + "openoffice-2.0-formula.odf",
        DOCS_PATH + "openoffice-2.0-impress.odp",
        DOCS_PATH + "openoffice-2.0-impress-template.otp",
        DOCS_PATH + "openoffice-2.0-writer.odt",
        DOCS_PATH + "openoffice-2.0-writer-template.ott",
        DOCS_PATH + "openoffice-1.1.5-calc.sxc",
        DOCS_PATH + "openoffice-1.1.5-calc-template.stc",
        DOCS_PATH + "openoffice-1.1.5-draw.sxd",
        DOCS_PATH + "openoffice-1.1.5-draw-template.std",
        DOCS_PATH + "openoffice-1.1.5-impress.sxi",
        DOCS_PATH + "openoffice-1.1.5-impress-template.sti",
        DOCS_PATH + "openoffice-1.1.5-writer.sxw",
        DOCS_PATH + "openoffice-1.1.5-writer-template.stw"
    };
    
    private static final String OPEN_OFFICE_WRITER_DOC = DOCS_PATH + "openoffice-1.1.5-writer.sxw";
    
    private static final String OPEN_DOCUMENT_WRITER_DOC = DOCS_PATH + "openoffice-2.0-writer.odt";
    
    public void testContentExtraction() throws ExtractorException, IOException {
        // repeat for every example OpenDocument/OpenOffice document
        for (int i = 0; i < RESOURCES.length; i++) {
            // check of any document text is extracted
            SesameRDFContainer container = getStatements(RESOURCES[i]);
            checkStatement(Vocabulary.FULL_TEXT_URI, "This", container);
        }
    }
    
    private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        ExtractorFactory factory = new OpenDocumentExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void testMetadataExtraction() throws ExtractorException, IOException {
        testMetadataExtraction(OPEN_DOCUMENT_WRITER_DOC);
        testMetadataExtraction(OPEN_OFFICE_WRITER_DOC);
    }

    private void testMetadataExtraction(String resourceName) throws ExtractorException, IOException {
        // apply the extractor
        SesameRDFContainer container = getStatements(resourceName);

        // check for all properties that we're sure of exist in this example document
        checkStatement(Vocabulary.TITLE_URI, "Example", container);
        checkStatement(Vocabulary.SUBJECT_URI, "Testing", container);
        checkStatement(Vocabulary.KEYWORD_URI, "rdf", container);
        checkStatement(Vocabulary.KEYWORD_URI, "test", container);
        checkStatement(Vocabulary.DESCRIPTION_URI, "comments", container);
        checkStatement(Vocabulary.CREATOR_URI, "Christiaan Fluit", container);
        checkStatement(Vocabulary.DATE_URI, "2005", container);
        checkStatement(Vocabulary.CREATION_DATE_URI, "2005", container);
        checkStatement(Vocabulary.PRINT_DATE_URI, "2005", container);
        checkStatement(Vocabulary.LANGUAGE_URI, "en-US", container);
        checkStatement(Vocabulary.PAGE_COUNT_URI, "1", container);
        checkStatement(Vocabulary.GENERATOR_URI, "OpenOffice", container);
    }
}
