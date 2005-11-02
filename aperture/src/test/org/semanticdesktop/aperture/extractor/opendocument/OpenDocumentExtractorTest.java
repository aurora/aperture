/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.Vocabulary;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

public class OpenDocumentExtractorTest extends ExtractorTestBase {

    private static final String PATH = "org/semanticdesktop/aperture/docs/";
    
    private static final String[] RESOURCES = {
        PATH + "openoffice-2.0-calc.ods",
        PATH + "openoffice-2.0-calc-template.ots",
        PATH + "openoffice-2.0-draw.odg",
        PATH + "openoffice-2.0-draw-template.otg",
        PATH + "openoffice-2.0-formula.odf",
        PATH + "openoffice-2.0-impress.odp",
        PATH + "openoffice-2.0-impress-template.otp",
        PATH + "openoffice-2.0-writer.odt",
        PATH + "openoffice-2.0-writer-template.ott",
        PATH + "openoffice-1.1.5-calc.sxc",
        PATH + "openoffice-1.1.5-calc-template.stc",
        PATH + "openoffice-1.1.5-draw.sxd",
        PATH + "openoffice-1.1.5-draw-template.std",
        PATH + "openoffice-1.1.5-impress.sxi",
        PATH + "openoffice-1.1.5-impress-template.sti",
        PATH + "openoffice-1.1.5-writer.sxw",
        PATH + "openoffice-1.1.5-writer-template.stw"
    };
    
    private static final String OPEN_OFFICE_WRITER_DOC = PATH + "openoffice-1.1.5-writer.sxw";
    
    private static final String OPEN_DOCUMENT_WRITER_DOC = PATH + "openoffice-2.0-writer.odt";
    
    public void testContentExtraction() throws URISyntaxException, ExtractorException, IOException {
        // repeat for every example OpenDocument/OpenOffice document
        for (int i = 0; i < RESOURCES.length; i++) {
            // check of any document text is extracted
            RDFContainerSesame container = getStatements(RESOURCES[i]);
            checkStatement(Vocabulary.FULL_TEXT_URI, "This", container);
        }
    }
    
    private RDFContainerSesame getStatements(String resourceName) throws URISyntaxException, ExtractorException, IOException {
        ExtractorFactory factory = new OpenDocumentExtractorFactory();
        Extractor extractor = factory.get();
        return extract(resourceName, extractor);
    }
    
    public void testMetadataExtraction() throws URISyntaxException, ExtractorException, IOException {
        testMetadataExtraction(OPEN_DOCUMENT_WRITER_DOC);
        testMetadataExtraction(OPEN_OFFICE_WRITER_DOC);
    }

    private void testMetadataExtraction(String resourceName) throws URISyntaxException, ExtractorException, IOException {
        // apply the extractor
        RDFContainerSesame container = getStatements(resourceName);

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
