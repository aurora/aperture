/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.extractor.excel.ExcelExtractorActivator;
import org.semanticdesktop.aperture.extractor.html.HtmlExtractorActivator;
import org.semanticdesktop.aperture.extractor.jpg.JpgExtractorActivator;
import org.semanticdesktop.aperture.extractor.office.OfficeExtractorActivator;
import org.semanticdesktop.aperture.extractor.opendocument.OpenDocumentExtractorActivator;
import org.semanticdesktop.aperture.extractor.openxml.OpenxmlExtractorActivator;
import org.semanticdesktop.aperture.extractor.plaintext.PlaintextExtractorActivator;
import org.semanticdesktop.aperture.extractor.powerpoint.PowerpointExtractorActivator;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorActivator;
import org.semanticdesktop.aperture.extractor.publisher.PublisherExtractorActivator;
import org.semanticdesktop.aperture.extractor.quattro.QuattroExtractorActivator;
import org.semanticdesktop.aperture.extractor.rtf.RtfExtractorActivator;
import org.semanticdesktop.aperture.extractor.util.ExtractorUtilActivator;
import org.semanticdesktop.aperture.extractor.visio.VisioExtractorActivator;
import org.semanticdesktop.aperture.extractor.word.WordExtractorActivator;
import org.semanticdesktop.aperture.extractor.wordperfect.WordPerfectExtractorActivator;
import org.semanticdesktop.aperture.extractor.works.WorksExtractorActivator;
import org.semanticdesktop.aperture.extractor.xml.XmlExtractorActivator;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeIdentifierActivator;

/**
 * Activator for the core implementations bundle.
 */
public class SafeActivator implements BundleActivator {

    private ExcelExtractorActivator excelExtractorActivator;
    private HtmlExtractorActivator htmlExtractorActivator;
    private JpgExtractorActivator jpgExtractorActivator;
    private OfficeExtractorActivator officeExtractorActivator;
    private OpenDocumentExtractorActivator openDocumentExtractorActivator;
    private OpenxmlExtractorActivator openXmlExtractorActivator;
    private PlaintextExtractorActivator plaintextExtractorActivator;
    private PowerpointExtractorActivator powerpointExtractorActivator;
    private PresentationsExtractorActivator presentationsExtractorActivator;
    private PublisherExtractorActivator publisherExtractorActivator;
    private QuattroExtractorActivator quattroExtractorActivator;
    private RtfExtractorActivator rtfExtractorActivator;
    private VisioExtractorActivator visioExtractorActivator;
    private WordExtractorActivator wordExtractorActivator;
    private WordPerfectExtractorActivator wordPerfectExtractorActivator;
    private WorksExtractorActivator worksExtractorActivator;
    private XmlExtractorActivator xmlExtractorActivator;
    private ExtractorUtilActivator extractorUtilActivator;
    private MagicMimeIdentifierActivator magicMimeIdentifierActivator;

    public void start(BundleContext context) throws Exception {

        excelExtractorActivator = new ExcelExtractorActivator();
        excelExtractorActivator.start(context);
        htmlExtractorActivator = new HtmlExtractorActivator();
        htmlExtractorActivator.start(context);
        jpgExtractorActivator = new JpgExtractorActivator();
        jpgExtractorActivator.start(context);
        officeExtractorActivator = new OfficeExtractorActivator();
        officeExtractorActivator.start(context);
        openDocumentExtractorActivator = new OpenDocumentExtractorActivator();
        openDocumentExtractorActivator.start(context);
        openXmlExtractorActivator = new OpenxmlExtractorActivator();
        openXmlExtractorActivator.start(context);
        plaintextExtractorActivator = new PlaintextExtractorActivator();
        plaintextExtractorActivator.start(context);
        powerpointExtractorActivator = new PowerpointExtractorActivator();
        powerpointExtractorActivator.start(context);
        presentationsExtractorActivator = new PresentationsExtractorActivator();
        presentationsExtractorActivator.start(context);
        publisherExtractorActivator = new PublisherExtractorActivator();
        publisherExtractorActivator.start(context);
        quattroExtractorActivator = new QuattroExtractorActivator();
        quattroExtractorActivator.start(context);
        rtfExtractorActivator = new RtfExtractorActivator();
        rtfExtractorActivator.start(context);
        visioExtractorActivator = new VisioExtractorActivator();
        visioExtractorActivator.start(context);
        wordExtractorActivator = new WordExtractorActivator();
        wordExtractorActivator.start(context);
        wordPerfectExtractorActivator = new WordPerfectExtractorActivator();
        wordPerfectExtractorActivator.start(context);
        worksExtractorActivator = new WorksExtractorActivator();
        worksExtractorActivator.start(context);
        xmlExtractorActivator = new XmlExtractorActivator();
        xmlExtractorActivator.start(context);

        extractorUtilActivator = new ExtractorUtilActivator();
        excelExtractorActivator.start(context);

        magicMimeIdentifierActivator = new MagicMimeIdentifierActivator();
        magicMimeIdentifierActivator.start(context);
    }

    public void stop(BundleContext context) throws Exception {
        excelExtractorActivator.stop(context);
        excelExtractorActivator = null;
        htmlExtractorActivator.stop(context);
        htmlExtractorActivator = null;
        jpgExtractorActivator.stop(context);
        jpgExtractorActivator = null;
        officeExtractorActivator.stop(context);
        officeExtractorActivator = null;
        openDocumentExtractorActivator.stop(context);
        openDocumentExtractorActivator = null;
        openXmlExtractorActivator.stop(context);
        openXmlExtractorActivator = null;
        plaintextExtractorActivator.stop(context);
        plaintextExtractorActivator = null;
        powerpointExtractorActivator.stop(context);
        powerpointExtractorActivator = null;
        presentationsExtractorActivator.stop(context);
        presentationsExtractorActivator = null;
        publisherExtractorActivator.stop(context);
        publisherExtractorActivator = null;
        quattroExtractorActivator.stop(context);
        quattroExtractorActivator = null;
        rtfExtractorActivator.stop(context);
        rtfExtractorActivator = null;
        visioExtractorActivator.stop(context);
        visioExtractorActivator = null;
        wordExtractorActivator.stop(context);
        wordExtractorActivator = null;
        wordPerfectExtractorActivator.stop(context);
        wordPerfectExtractorActivator = null;
        worksExtractorActivator.stop(context);
        worksExtractorActivator = null;
        xmlExtractorActivator.stop(context);
        xmlExtractorActivator = null;
        extractorUtilActivator.stop(context);
        extractorUtilActivator = null;
        magicMimeIdentifierActivator.stop(context);
        magicMimeIdentifierActivator = null;
    }
}
