/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class PdfExtractorTest extends ExtractorTestBase {

    private static final String OPEN_OFFICE_2_DOC = DOCS_PATH + "pdf-openoffice-2.0-writer.pdf";
    
    private static final String OPEN_OFFICE_1_DOC = DOCS_PATH + "pdf-openoffice-1.1.5-writer.pdf";
    
    private static final String PDF_CREATOR_DOC = DOCS_PATH + "pdf-word-2000-pdfcreator-0.8.0.pdf";
    
    private static final String PDF_MAKER_DOC = DOCS_PATH + "pdf-word-2000-pdfmaker-7.0.pdf";
    
    private static final String PDF_WRITER_DOC = DOCS_PATH + "pdf-word-2000-pdfwriter-7.0.pdf";
    
    private RDF2GoRDFContainer container;
    
    
    private RDF2GoRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        // apply the extractor
        ExtractorFactory factory = new PdfExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void tearDown() {
    	container.dispose();
    	container = null;
    }
    
    public void testOpenOffice2Writer() throws ExtractorException, IOException, ModelException {
        // note: document has no date
        container = getStatements(OPEN_OFFICE_2_DOC);
        
        checkStatement(DATA.generator, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testOpenOffice1Writer() throws ExtractorException, IOException, ModelException {
        // note: document has no date
        container = getStatements(OPEN_OFFICE_1_DOC);
        
        checkStatement(DATA.generator, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFCreator() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_CREATOR_DOC);
        
        checkStatement(DATA.generator, "PDFCreator", container);
        checkStatement(DATA.generator, "Ghostscript", container);
        checkStatement(DATA.date, "2005", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFMaker() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_MAKER_DOC);
        
        checkStatement(DATA.generator, "PDFMaker", container);
        checkStatement(DATA.generator, "Distiller", container);
        checkStatement(DATA.date, "2005", container);
        
        checkOmnipresentStatements(container);
    }
    
    public void testPDFWriter() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_WRITER_DOC);
        
        checkStatement(DATA.title, "Microsoft Word", container);
        checkStatement(DATA.creator, "Christiaan Fluit", container);
        checkStatement(DATA.generator, "PScript5.dll", container);
        checkStatement(DATA.generator, "Distiller", container);
        checkStatement(DATA.date, "2005", container);
        checkStatement(DATA.created, "2005", container);
        checkStatement(DATA.pageCount, "1", container);
    }
    
    private void checkOmnipresentStatements(RDF2GoRDFContainer container) throws ModelException {
        checkStatement(DATA.creator, "Christiaan Fluit", container);
        checkStatement(DATA.subject, "Testing", container);
        checkStatement(DATA.title, "Example", container);
        checkStatement(DATA.created, "2005", container);
        checkStatement(DATA.pageCount, "1", container);
        checkStatement(DATA.keyword, "rdf", container);
        checkStatement(DATA.keyword, "test", container);
    }
}
