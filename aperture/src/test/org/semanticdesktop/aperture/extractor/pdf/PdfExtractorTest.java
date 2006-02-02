/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class PdfExtractorTest extends ExtractorTestBase {

    private static final String OPEN_OFFICE_2_DOC = DOCS_PATH + "pdf-openoffice-2.0-writer.pdf";
    
    private static final String OPEN_OFFICE_1_DOC = DOCS_PATH + "pdf-openoffice-1.1.5-writer.pdf";
    
    private static final String PDF_CREATOR_DOC = DOCS_PATH + "pdf-word-2000-pdfcreator-0.8.0.pdf";
    
    private static final String PDF_MAKER_DOC = DOCS_PATH + "pdf-word-2000-pdfmaker-7.0.pdf";
    
    private static final String PDF_WRITER_DOC = DOCS_PATH + "pdf-word-2000-pdfwriter-7.0.pdf";
    
    private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        // apply the extractor
        ExtractorFactory factory = new PdfExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void testOpenOffice2Writer() throws ExtractorException, IOException {
        // note: document has no date
        SesameRDFContainer container = getStatements(OPEN_OFFICE_2_DOC);
        
        checkStatement(AccessVocabulary.GENERATOR, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testOpenOffice1Writer() throws ExtractorException, IOException {
        // note: document has no date
        SesameRDFContainer container = getStatements(OPEN_OFFICE_1_DOC);
        
        checkStatement(AccessVocabulary.GENERATOR, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFCreator() throws ExtractorException, IOException {
        SesameRDFContainer container = getStatements(PDF_CREATOR_DOC);
        
        checkStatement(AccessVocabulary.GENERATOR, "PDFCreator", container);
        checkStatement(AccessVocabulary.GENERATOR, "Ghostscript", container);
        checkStatement(AccessVocabulary.DATE, "2005", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFMaker() throws ExtractorException, IOException {
        SesameRDFContainer container = getStatements(PDF_MAKER_DOC);
        
        checkStatement(AccessVocabulary.GENERATOR, "PDFMaker", container);
        checkStatement(AccessVocabulary.GENERATOR, "Distiller", container);
        checkStatement(AccessVocabulary.DATE, "2005", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFWriter() throws ExtractorException, IOException {
        SesameRDFContainer container = getStatements(PDF_WRITER_DOC);
        
        checkStatement(AccessVocabulary.TITLE, "Microsoft Word", container);
        checkStatement(AccessVocabulary.CREATOR, "Christiaan Fluit", container);
        checkStatement(AccessVocabulary.GENERATOR, "PScript5.dll", container);
        checkStatement(AccessVocabulary.GENERATOR, "Distiller", container);
        checkStatement(AccessVocabulary.DATE, "2005", container);
        checkStatement(AccessVocabulary.CREATION_DATE, "2005", container);
        checkStatement(AccessVocabulary.PAGE_COUNT, "1", container);
    }
    
    private void checkOmnipresentStatements(SesameRDFContainer container) {
        checkStatement(AccessVocabulary.CREATOR, "Christiaan Fluit", container);
        checkStatement(AccessVocabulary.SUBJECT, "Testing", container);
        checkStatement(AccessVocabulary.TITLE, "Example", container);
        checkStatement(AccessVocabulary.CREATION_DATE, "2005", container);
        checkStatement(AccessVocabulary.PAGE_COUNT, "1", container);
        checkStatement(AccessVocabulary.KEYWORD, "rdf", container);
        checkStatement(AccessVocabulary.KEYWORD, "test", container);
    }
}
