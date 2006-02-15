/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.extractor.excel.ExcelExtractorTest;
import org.semanticdesktop.aperture.extractor.html.HtmlExtractorTest;
import org.semanticdesktop.aperture.extractor.impl.TestDefaultExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.TestExtractorRegistryImpl;
import org.semanticdesktop.aperture.extractor.office.OfficeExtractorTest;
import org.semanticdesktop.aperture.extractor.opendocument.OpenDocumentExtractorTest;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorTest;
import org.semanticdesktop.aperture.extractor.plaintext.PlainTextExtractorTest;
import org.semanticdesktop.aperture.extractor.powerpoint.PowerPointExtractorTest;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorTest;
import org.semanticdesktop.aperture.extractor.publisher.PublisherExtractorTest;
import org.semanticdesktop.aperture.extractor.quattro.QuattroExtractorTest;
import org.semanticdesktop.aperture.extractor.rtf.RtfExtractorTest;
import org.semanticdesktop.aperture.extractor.visio.VisioExtractorTest;
import org.semanticdesktop.aperture.extractor.word.WordExtractorTest;
import org.semanticdesktop.aperture.extractor.wordperfect.WordPerfectExtractorTest;
import org.semanticdesktop.aperture.extractor.xml.XmlExtractorTest;

/**
 * Tests all Extractor implementations and related classes.
 */
public class TestExtractors extends TestSuite {

    public static Test suite() {
        return new TestExtractors();
    }
    
    private TestExtractors() {
        super("extractors");
        
        // test the various extractors
        addTest(new TestSuite(ExcelExtractorTest.class));
        addTest(new TestSuite(HtmlExtractorTest.class));
        addTest(new TestSuite(OfficeExtractorTest.class));
        addTest(new TestSuite(OpenDocumentExtractorTest.class));
        addTest(new TestSuite(PdfExtractorTest.class));
        addTest(new TestSuite(PlainTextExtractorTest.class));
        addTest(new TestSuite(PowerPointExtractorTest.class));
        addTest(new TestSuite(PresentationsExtractorTest.class));
        addTest(new TestSuite(PublisherExtractorTest.class));
        addTest(new TestSuite(QuattroExtractorTest.class));
        addTest(new TestSuite(RtfExtractorTest.class));
        addTest(new TestSuite(VisioExtractorTest.class));
        addTest(new TestSuite(WordExtractorTest.class));
        addTest(new TestSuite(WordPerfectExtractorTest.class));
        addTest(new TestSuite(XmlExtractorTest.class));
        
        // test the registries holding the ExtractorFactories
        addTest(new TestSuite(TestExtractorRegistryImpl.class));
        addTest(new TestSuite(TestDefaultExtractorRegistry.class));
    }
}
