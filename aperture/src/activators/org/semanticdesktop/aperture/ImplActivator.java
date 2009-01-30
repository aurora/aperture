/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
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
import org.semanticdesktop.aperture.crawler.mbox.MboxCrawlerActivator;
import org.semanticdesktop.aperture.crawler.web.WebCrawlerActivator;
import org.semanticdesktop.aperture.extractor.mime.MimeExtractorActivator;
import org.semanticdesktop.aperture.extractor.mp3.Mp3FileExtractorActivator;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorActivator;
import org.semanticdesktop.aperture.extractor.util.ExtractorUtilActivator;
import org.semanticdesktop.aperture.hypertext.linkextractor.html.HtmlLinkExtractorActivator;
import org.semanticdesktop.aperture.opener.email.EmailOpenerActivator;
import org.semanticdesktop.aperture.opener.file.FileOpenerActivator;
import org.semanticdesktop.aperture.opener.http.HttpOpenerActivator;
import org.semanticdesktop.aperture.outlook.OutlookActivator;
import org.semanticdesktop.aperture.security.trustdecider.dialog.DialogTrustDeciderActivator;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManagerActivator;
import org.semanticdesktop.aperture.subcrawler.bzip2.BZip2SubCrawlerActivator;
import org.semanticdesktop.aperture.subcrawler.gzip.GZipSubCrawlerActivator;
import org.semanticdesktop.aperture.subcrawler.tar.TarSubCrawlerActivator;
import org.semanticdesktop.aperture.subcrawler.vcard.VcardSubCrawlerActivator;
import org.semanticdesktop.aperture.subcrawler.zip.ZipSubCrawlerActivator;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyActivator;
import org.semanticdesktop.aperture.websites.delicious.DeliciousActivator;
import org.semanticdesktop.aperture.websites.flickr.FlickrActivator;
import org.semanticdesktop.aperture.websites.iphoto.IPhotoActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator for the core implementations bundle.
 */
public class ImplActivator implements BundleActivator {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static BundleContext bc;
    
    private FileAccessorActivator fileAccessorActivator;
    private HttpAccessorActivator httpAccessorActivator;
    // crawlers
    private FilesystemCrawlerActivator fileSystemCrawlerActivator;
    private IcalCrawlerActivator icalCrawlerActivator;
    private MboxCrawlerActivator mboxCrawlerActivator;
    private ImapCrawlerActivator imapCrawlerActivator;
    private WebCrawlerActivator webCrawlerActivator;
    private AppleAddressBookActivator appleAddressBookActivator;
    private ThunderbirdAddressBookActivator thunderbirdAddressbookActivator;
    private OutlookActivator outlookActivator;
    private BibsonomyActivator bibsonomyActivator;
    private DeliciousActivator deliciousActivator;
    private FlickrActivator flickrActivator;
    private IPhotoActivator iphotoActivator;
    // subcrawlers
    private VcardSubCrawlerActivator vcardSubCrawlerActivator;
    private ZipSubCrawlerActivator zipSubCrawlerActivator;
    private GZipSubCrawlerActivator gzipSubCrawlerActivator;
    private BZip2SubCrawlerActivator bzip2SubCrawlerActivator;
    private TarSubCrawlerActivator tarSubCrawlerActivator;
    
    private MimeExtractorActivator mimeExtractorActivator;
    private PdfExtractorActivator pdfExtractorActivator;
    private Mp3FileExtractorActivator mp3FileExtractorActivator;

    private FileOpenerActivator fileOpenerActivator;
    private HttpOpenerActivator httpOpenerActivator;
    private EmailOpenerActivator emailOpenerActivator;
    private HtmlLinkExtractorActivator htmlLinkExtractorActivator;
    private DialogTrustDeciderActivator dialogTrustDeciderActivator;
    private StandardTrustManagerActivator standardTrustManagerActivator;
    
    public void start(BundleContext context) throws Exception {
        fileAccessorActivator = new FileAccessorActivator();
        fileAccessorActivator.start(context);
        httpAccessorActivator = new HttpAccessorActivator();
        httpAccessorActivator.start(context);
        
        fileSystemCrawlerActivator = new FilesystemCrawlerActivator();
        fileSystemCrawlerActivator.start(context);
        icalCrawlerActivator = new IcalCrawlerActivator();
        icalCrawlerActivator.start(context);
        mboxCrawlerActivator = new MboxCrawlerActivator();
        mboxCrawlerActivator.start(context);
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
        
        mimeExtractorActivator = new MimeExtractorActivator();
        mimeExtractorActivator.start(context);
        pdfExtractorActivator = new PdfExtractorActivator();
        pdfExtractorActivator.start(context);
        mp3FileExtractorActivator = new Mp3FileExtractorActivator();
        mp3FileExtractorActivator.start(context);
        
        vcardSubCrawlerActivator = new VcardSubCrawlerActivator();
        vcardSubCrawlerActivator.start(context);
        zipSubCrawlerActivator = new ZipSubCrawlerActivator();
        zipSubCrawlerActivator.start(context);
        gzipSubCrawlerActivator = new GZipSubCrawlerActivator();
        gzipSubCrawlerActivator.start(context);
        bzip2SubCrawlerActivator = new BZip2SubCrawlerActivator();
        bzip2SubCrawlerActivator.start(context);
        tarSubCrawlerActivator = new TarSubCrawlerActivator();
        tarSubCrawlerActivator.start(context);
        
        fileOpenerActivator = new FileOpenerActivator();
        fileOpenerActivator.start(context);
        httpOpenerActivator = new HttpOpenerActivator();
        httpOpenerActivator.start(context);
        emailOpenerActivator = new EmailOpenerActivator();
        emailOpenerActivator.start(context);
        
        htmlLinkExtractorActivator = new HtmlLinkExtractorActivator();
        htmlLinkExtractorActivator.start(context);
        
        dialogTrustDeciderActivator = new DialogTrustDeciderActivator();
        dialogTrustDeciderActivator.start(context);
        
        standardTrustManagerActivator = new StandardTrustManagerActivator();
        standardTrustManagerActivator.start(context);
        
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
        mboxCrawlerActivator.stop(context);
        mboxCrawlerActivator = null;
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
        
        mimeExtractorActivator.stop(context);
        mimeExtractorActivator = null;
        pdfExtractorActivator.stop(context);
        pdfExtractorActivator = null;
        mp3FileExtractorActivator.stop(context);
        mp3FileExtractorActivator = null;
      
        vcardSubCrawlerActivator.stop(context);
        vcardSubCrawlerActivator = null;
        
        if(zipSubCrawlerActivator != null){
            zipSubCrawlerActivator.stop(context);
            zipSubCrawlerActivator = null;
        }
        gzipSubCrawlerActivator.stop(context);
        gzipSubCrawlerActivator = null;
        bzip2SubCrawlerActivator.stop(context);
        bzip2SubCrawlerActivator = null;
        tarSubCrawlerActivator.stop(context);
        tarSubCrawlerActivator = null;
        
        fileOpenerActivator.stop(context);
        fileOpenerActivator = null;
        httpOpenerActivator.stop(context);
        httpOpenerActivator = null;
        emailOpenerActivator.stop(context);
        emailOpenerActivator = null;
        
        htmlLinkExtractorActivator.stop(context);
        htmlLinkExtractorActivator = null;
        
        dialogTrustDeciderActivator.stop(context);
        dialogTrustDeciderActivator = null;
        
        standardTrustManagerActivator.stop(context);
        standardTrustManagerActivator = null;
        
        bc = null;  
    }
}
