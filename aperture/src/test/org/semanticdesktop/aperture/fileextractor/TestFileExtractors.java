/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.extractor.excel.ExcelExtractorTest;
import org.semanticdesktop.aperture.extractor.html.HtmlExtractorTest;
import org.semanticdesktop.aperture.extractor.impl.TestDefaultExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.TestExtractorRegistryImpl;
import org.semanticdesktop.aperture.extractor.mime.MimeExtractorTest;
import org.semanticdesktop.aperture.extractor.office.OfficeExtractorTest;
import org.semanticdesktop.aperture.extractor.opendocument.OpenDocumentExtractorTest;
import org.semanticdesktop.aperture.extractor.openxml.OpenXmlExtractorTest;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorTest;
import org.semanticdesktop.aperture.extractor.plaintext.PlainTextExtractorTest;
import org.semanticdesktop.aperture.extractor.powerpoint.PowerPointExtractorTest;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorTest;
import org.semanticdesktop.aperture.extractor.publisher.PublisherExtractorTest;
import org.semanticdesktop.aperture.extractor.quattro.QuattroExtractorTest;
import org.semanticdesktop.aperture.extractor.rtf.RtfExtractorTest;
import org.semanticdesktop.aperture.extractor.util.ThreadedExtractorWrapperTest;
import org.semanticdesktop.aperture.extractor.visio.VisioExtractorTest;
import org.semanticdesktop.aperture.extractor.word.WordExtractorTest;
import org.semanticdesktop.aperture.extractor.wordperfect.WordPerfectExtractorTest;
import org.semanticdesktop.aperture.extractor.works.WorksExtractorTest;
import org.semanticdesktop.aperture.extractor.xml.XmlExtractorTest;
import org.semanticdesktop.aperture.fileextractor.impl.TestDefaultFileExtractorRegistry;
import org.semanticdesktop.aperture.fileextractor.impl.TestFileExtractorRegistryImpl;

/**
 * Tests all Extractor implementations and related classes.
 */
public class TestFileExtractors extends TestSuite {

    public static Test suite() {
        return new TestFileExtractors();
    }
    
    private TestFileExtractors() {
        super("fileextractors");
        
        // test the registries holding the ExtractorFactories
        addTest(new TestSuite(TestFileExtractorRegistryImpl.class));
        addTest(new TestSuite(TestDefaultFileExtractorRegistry.class));
    }
}
