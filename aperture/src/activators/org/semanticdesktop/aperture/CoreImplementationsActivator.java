/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.accessor.file.FileAccessorActivator;
import org.semanticdesktop.aperture.accessor.http.HttpAccessorActivator;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressBookActivator;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressBookActivator;
import org.semanticdesktop.aperture.crawler.filesystem.FilesystemCrawlerActivator;
import org.semanticdesktop.aperture.crawler.ical.IcalCrawlerActivator;
import org.semanticdesktop.aperture.crawler.imap.ImapCrawlerActivator;
import org.semanticdesktop.aperture.crawler.web.WebCrawlerActivator;
import org.semanticdesktop.aperture.extractor.excel.ExcelExtractorActivator;
import org.semanticdesktop.aperture.extractor.html.HtmlExtractorActivator;
import org.semanticdesktop.aperture.extractor.mime.MimeExtractorActivator;
import org.semanticdesktop.aperture.extractor.office.OfficeExtractorActivator;
import org.semanticdesktop.aperture.extractor.opendocument.OpenDocumentExtractorActivator;
import org.semanticdesktop.aperture.extractor.openxml.OpenxmlExtractorActivator;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorActivator;
import org.semanticdesktop.aperture.extractor.plaintext.PlaintextExtractorActivator;
import org.semanticdesktop.aperture.extractor.powerpoint.PowerpointExtractorActivator;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorActivator;
import org.semanticdesktop.aperture.extractor.publisher.PublisherExtractorActivator;
import org.semanticdesktop.aperture.extractor.quattro.QuattroExtractorActivator;
import org.semanticdesktop.aperture.extractor.rtf.RtfExtractorActivator;
import org.semanticdesktop.aperture.extractor.util.ExtractorUtilActivator;
import org.semanticdesktop.aperture.extractor.vcard.VcardExtractorActivator;
import org.semanticdesktop.aperture.extractor.visio.VisioExtractorActivator;
import org.semanticdesktop.aperture.extractor.word.WordExtractorActivator;
import org.semanticdesktop.aperture.extractor.wordperfect.WordPerfectExtractorActivator;
import org.semanticdesktop.aperture.extractor.works.WorksExtractorActivator;
import org.semanticdesktop.aperture.extractor.xml.XmlExtractorActivator;
import org.semanticdesktop.aperture.opener.file.FileOpenerActivator;
import org.semanticdesktop.aperture.opener.http.HttpOpenerActivator;
import org.semanticdesktop.aperture.outlook.OutlookActivator;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyActivator;
import org.semanticdesktop.aperture.websites.delicious.DeliciousActivator;
import org.semanticdesktop.aperture.websites.flickr.FlickrActivator;
import org.semanticdesktop.aperture.websites.iphoto.IPhotoActivator;

public class CoreImplementationsActivator implements BundleActivator {

	public static BundleContext bc;
	
	private FileAccessorActivator fileAccessorActivator;
	private HttpAccessorActivator httpAccessorActivator;
	
	private FilesystemCrawlerActivator fileSystemCrawlerActivator;
	private IcalCrawlerActivator icalCrawlerActivator;
	private ImapCrawlerActivator imapCrawlerActivator;
	private WebCrawlerActivator	webCrawlerActivator;
	private AppleAddressBookActivator appleAddressBookActivator;
    private ThunderbirdAddressBookActivator thunderbirdAddressbookActivator;
    private OutlookActivator outlookActivator;
	private BibsonomyActivator bibsonomyActivator;
	private DeliciousActivator deliciousActivator;
	private FlickrActivator flickrActivator;
	private IPhotoActivator iphotoActivator;
    
	private ExcelExtractorActivator excelExtractorActivator;
	private HtmlExtractorActivator htmlExtractorActivator;
	private MimeExtractorActivator mimeExtractorActivator;
	private OfficeExtractorActivator officeExtractorActivator;
	private OpenDocumentExtractorActivator openDocumentExtractorActivator;
	private OpenxmlExtractorActivator openXmlExtractorActivator;
	private PdfExtractorActivator pdfExtractorActivator;
	private PlaintextExtractorActivator plaintextExtractorActivator;
	private PowerpointExtractorActivator powerpointExtractorActivator;
	private PresentationsExtractorActivator presentationsExtractorActivator;
	private PublisherExtractorActivator publisherExtractorActivator;
	private QuattroExtractorActivator quattroExtractorActivator;
	private RtfExtractorActivator rtfExtractorActivator;
	private VcardExtractorActivator vcardExtractorActivator;
	private VisioExtractorActivator visioExtractorActivator;
	private WordExtractorActivator wordExtractorActivator;
	private WordPerfectExtractorActivator wordPerfectExtractorActivator;
	private WorksExtractorActivator worksExtractorActivator;
	private XmlExtractorActivator xmlExtractorActivator;
	
	private ExtractorUtilActivator extractorUtilActivator;
	
	private FileOpenerActivator fileOpenerActivator;
	private HttpOpenerActivator httpOpenerActivator;
    
    
    
	public void start(BundleContext context) throws Exception {
		fileAccessorActivator = new FileAccessorActivator();
		fileAccessorActivator.start(context);
		httpAccessorActivator = new HttpAccessorActivator();
		httpAccessorActivator.start(context);
		
		fileSystemCrawlerActivator = new FilesystemCrawlerActivator();
		fileSystemCrawlerActivator.start(context);
		icalCrawlerActivator = new IcalCrawlerActivator();
		icalCrawlerActivator.start(context);
		imapCrawlerActivator = new ImapCrawlerActivator();
		imapCrawlerActivator.start(context);
		webCrawlerActivator = new WebCrawlerActivator();
		webCrawlerActivator.start(context);
		appleAddressBookActivator = new AppleAddressBookActivator();
		appleAddressBookActivator.start(context);
		thunderbirdAddressbookActivator = new ThunderbirdAddressBookActivator();
		thunderbirdAddressbookActivator.start(context);
		outlookActivator = new OutlookActivator();
        outlookActivator.start(context);
		bibsonomyActivator = new BibsonomyActivator();
		bibsonomyActivator.start(context);
		deliciousActivator = new DeliciousActivator();
        deliciousActivator.start(context);
        flickrActivator = new FlickrActivator();
        flickrActivator.start(context);
        iphotoActivator = new IPhotoActivator();
        iphotoActivator.start(context);
        
		excelExtractorActivator = new ExcelExtractorActivator();
		excelExtractorActivator.start(context);
		htmlExtractorActivator = new HtmlExtractorActivator();
		htmlExtractorActivator.start(context);
		mimeExtractorActivator = new MimeExtractorActivator();
		mimeExtractorActivator.start(context);
		officeExtractorActivator = new OfficeExtractorActivator();
		officeExtractorActivator.start(context);
		openDocumentExtractorActivator = new OpenDocumentExtractorActivator();
		openDocumentExtractorActivator.start(context);
		openXmlExtractorActivator = new OpenxmlExtractorActivator();
		openXmlExtractorActivator.start(context);
		pdfExtractorActivator = new PdfExtractorActivator();
		pdfExtractorActivator.start(context);
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
		vcardExtractorActivator = new VcardExtractorActivator();
        vcardExtractorActivator.start(context);
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
		extractorUtilActivator.start(context);
		
		fileOpenerActivator = new FileOpenerActivator();
		fileOpenerActivator.start(context);
		httpOpenerActivator = new HttpOpenerActivator();
		httpOpenerActivator.start(context);
        
		bc = context;	
	}

	public void stop(BundleContext context) throws Exception {		
		fileAccessorActivator.stop(context);
		fileAccessorActivator = null;
		httpAccessorActivator.stop(context);
		httpAccessorActivator = null;
		
		fileSystemCrawlerActivator.stop(context);
		fileSystemCrawlerActivator = null;
		icalCrawlerActivator.stop(context);
		icalCrawlerActivator = null;
		imapCrawlerActivator.stop(context);
		imapCrawlerActivator = null;
		webCrawlerActivator.stop(context);
		webCrawlerActivator = null;
		appleAddressBookActivator.stop(context);
		appleAddressBookActivator = null;
		thunderbirdAddressbookActivator.stop(context);
		thunderbirdAddressbookActivator = null;
		outlookActivator.stop(context);
        outlookActivator = null;
        bibsonomyActivator.stop(context);
        bibsonomyActivator = null;
        deliciousActivator.stop(context);
        deliciousActivator = null;
        flickrActivator.stop(context);
        flickrActivator = null;
        iphotoActivator.stop(context);
        iphotoActivator = null;
        
		excelExtractorActivator.stop(context);
		excelExtractorActivator = null;
		htmlExtractorActivator.stop(context);
		htmlExtractorActivator = null;
		mimeExtractorActivator.stop(context);
		mimeExtractorActivator = null;
		officeExtractorActivator.stop(context);
		officeExtractorActivator = null;
		openDocumentExtractorActivator.stop(context);
		openDocumentExtractorActivator = null;
		openXmlExtractorActivator.stop(context);
		openXmlExtractorActivator = null;
		pdfExtractorActivator.stop(context);
		pdfExtractorActivator = null;
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
		vcardExtractorActivator.stop(context);
		vcardExtractorActivator = null;
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
		
		fileOpenerActivator.stop(context);
		fileOpenerActivator = null;
		httpOpenerActivator.stop(context);
		httpOpenerActivator = null;
		
		bc = null;	
	}
}
