/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;
import java.net.URISyntaxException;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.Vocabulary;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

public class PdfExtractorTest extends ExtractorTestBase {

    private static final String OPEN_OFFICE_2_DOC = DOCS_PATH + "pdf-openoffice-2.0-writer.pdf";
    
    private static final String OPEN_OFFICE_1_DOC = DOCS_PATH + "pdf-openoffice-1.1.5-writer.pdf";
    
    private static final String PDF_CREATOR_DOC = DOCS_PATH + "pdf-word-2000-pdfcreator-0.8.0.pdf";
    
    private RDFContainerSesame getStatements(String resourceName) throws URISyntaxException, ExtractorException, IOException {
        // apply the extractor
        ExtractorFactory factory = new PdfExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void testOpenOffice2Writer() throws URISyntaxException, ExtractorException, IOException {
        // note: document has no date
        RDFContainerSesame container = getStatements(OPEN_OFFICE_2_DOC);
        checkStatement(Vocabulary.GENERATOR_URI, "OpenOffice", container);
        checkOmnipresentStatements(container);
    }
    
    public void testOpenOffice1Writer() throws URISyntaxException, ExtractorException, IOException {
        // note: document has no date
        RDFContainerSesame container = getStatements(OPEN_OFFICE_1_DOC);
        checkStatement(Vocabulary.GENERATOR_URI, "OpenOffice", container);
        checkOmnipresentStatements(container);
    }
    
    public void testPDFCreator() throws URISyntaxException, ExtractorException, IOException {
        RDFContainerSesame container = getStatements(PDF_CREATOR_DOC);
        checkStatement(Vocabulary.GENERATOR_URI, "PDFCreator", container);
        checkStatement(Vocabulary.GENERATOR_URI, "Ghostscript", container);
        checkStatement(Vocabulary.DATE_URI, "2005", container);
        checkOmnipresentStatements(container);
    }
    
    private void checkOmnipresentStatements(RDFContainerSesame container) {
        checkStatement(Vocabulary.CREATOR_URI, "Christiaan Fluit", container);
        checkStatement(Vocabulary.SUBJECT_URI, "Testing", container);
        checkStatement(Vocabulary.TITLE_URI, "Example", container);
        checkStatement(Vocabulary.CREATION_DATE_URI, "2005", container);
        checkStatement(Vocabulary.PAGE_COUNT_URI, "1", container);
        checkStatement(Vocabulary.KEYWORD_URI, "rdf", container);
        checkStatement(Vocabulary.KEYWORD_URI, "test", container);
    }
}
