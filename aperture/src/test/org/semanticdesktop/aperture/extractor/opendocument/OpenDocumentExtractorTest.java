/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

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
    
    public void testContentExtraction() throws ExtractorException, IOException, ModelException {
        // repeat for every example OpenDocument/OpenOffice document
        for (int i = 0; i < RESOURCES.length; i++) {
            // check of any document text is extracted
            RDFContainer container = getStatements(RESOURCES[i]);
            checkStatement(DATA.fullText, "This", container);
            container.dispose();
        }
    }
    
    private RDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        ExtractorFactory factory = new OpenDocumentExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void testMetadataExtraction() throws ExtractorException, IOException, ModelException {
        testMetadataExtraction(OPEN_DOCUMENT_WRITER_DOC);
        testMetadataExtraction(OPEN_OFFICE_WRITER_DOC);
    }

    private void testMetadataExtraction(String resourceName) throws ExtractorException, IOException,
    		ModelException {
        // apply the extractor
        RDFContainer container = getStatements(resourceName);

        // check for all properties that we're sure of exist in this example document
        checkStatement(DATA.title, "Example", container);
        checkStatement(DATA.subject, "Testing", container);
        checkStatement(DATA.keyword, "rdf", container);
        checkStatement(DATA.keyword, "test", container);
        checkStatement(DATA.description, "comments", container);
        checkStatement(DATA.creator, "Christiaan Fluit", container);
        checkStatement(DATA.date, "2005", container);
        checkStatement(DATA.created, "2005", container);
        checkStatement(DATA.printDate, "2005", container);
        checkStatement(DATA.language, "en-US", container);
        checkStatement(DATA.pageCount, "1", container);
        checkStatement(DATA.generator, "OpenOffice", container);
        
        container.getModel().close();
    }
}
