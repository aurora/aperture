/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;

import org.omg.DynamicAny.DynFixedOperations;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Syntax;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class PdfExtractorTest extends ExtractorTestBase {

    private static final String OPEN_OFFICE_2_DOC = DOCS_PATH + "pdf-openoffice-2.0-writer.pdf";
    
    private static final String OPEN_OFFICE_1_DOC = DOCS_PATH + "pdf-openoffice-1.1.5-writer.pdf";
    
    private static final String PDF_CREATOR_DOC = DOCS_PATH + "pdf-word-2000-pdfcreator-0.8.0.pdf";
    
    private static final String PDF_MAKER_DOC = DOCS_PATH + "pdf-word-2000-pdfmaker-7.0.pdf";
    
    private static final String PDF_WRITER_DOC = DOCS_PATH + "pdf-word-2000-pdfwriter-7.0.pdf";
    
    private static final String PDF_DISTILLER_WEIRDCHARS_DOC = DOCS_PATH + "pdf-distiller-6-weirdchars.pdf";
    
    private static final String PDF_NO_AUTHOR_DOC = DOCS_PATH + "pdf-no-author.pdf";
    
    private RDFContainer container;
    
    
    private RDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
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
        
        checkStatement(NIE.generator, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
        validate(container);
    }
    
    public void testOpenOffice1Writer() throws ExtractorException, IOException, ModelException {
        // note: document has no date
        container = getStatements(OPEN_OFFICE_1_DOC);
        
        checkStatement(NIE.generator, "OpenOffice", container);
        
        checkOmnipresentStatements(container);
        validate(container);
    }
    
    public void testPDFCreator() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_CREATOR_DOC);
        
        checkStatement(NIE.generator, "PDFCreator", container);
        checkStatement(NIE.generator, "Ghostscript", container);
        checkStatement(NIE.contentLastModified, "2005", container);
        
        checkOmnipresentStatements(container);
        validate(container);
    }
    
    public void testPDFMaker() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_MAKER_DOC);
        
        checkStatement(NIE.generator, "PDFMaker", container);
        checkStatement(NIE.generator, "Distiller", container);
        checkStatement(NIE.contentLastModified, "2005", container);
        checkOmnipresentStatements(container);
        validate(container);
    }
    
    public void testPDFWriter() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_WRITER_DOC);
        
        checkStatement(NIE.title, "Microsoft Word", container);
        checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
        checkStatement(NIE.generator, "PScript5.dll", container);
        checkStatement(NIE.generator, "Distiller", container);
        checkStatement(NIE.contentLastModified, "2005", container);
        checkStatement(NIE.contentCreated, "2005", container);
        checkStatement(NFO.pageCount, "1", container);
        validate(container);
    }
    
    public void testDistiller6() throws ExtractorException, IOException, ModelException {
        container = getStatements(PDF_DISTILLER_WEIRDCHARS_DOC);
        
        checkStatement(NIE.title, "Microsoft Word - wp618-kessell.doc", container);
        checkSimpleContact(NCO.creator, "Angela Kessell", container);
        checkStatement(NIE.generator, "PScript5.dll Version 5.2.2", container);
        checkStatement(NIE.generator, "Acrobat Distiller 6.0 (Windows)", container);
        //checkStatement(DATA.date, "2006-02-18T20:44:22", container);
        checkStatement(NIE.contentCreated, "2006-02-18T12:44:22", container);
        checkStatement(NFO.pageCount, "6", container);
        // note that the apostrophe in people's is NOT a normal apostrophy
        // it's some kind of a weird unicode character that caused problems
        checkStatement(NIE.plainTextContent, "of people’s recorded tasks", container);
        validate(container);
        
    }
    
    public void testNoAuthor() throws Exception {
        container = getStatements(PDF_NO_AUTHOR_DOC);
        
        //checkStatement(NIE.plainTextContent,"This is an example PDF without an author.",container);
        //assertNull(container.getNode(NCO.creator));
        
        //validate(container);
    }
    
    private void checkOmnipresentStatements(RDFContainer container) throws ModelException {
        checkSimpleContact(NCO.creator, "Christiaan Fluit", container);
        checkStatement(NIE.subject, "Testing", container);
        checkStatement(NIE.title, "Example", container);
        checkStatement(NIE.contentCreated, "2005", container);
        checkStatement(NFO.pageCount, "1", container);
        checkStatement(NIE.keyword, "rdf", container);
        checkStatement(NIE.keyword, "test", container);
    }
}
