/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource.ConnectionSecurity;
import org.semanticdesktop.aperture.examples.handler.IMAPUrisValidatingCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.websites.flickr.FlickrCrawler;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource.CrawlType;

public class ExampleFlickrCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleFlickrCrawler.class.getName());

	private static final String ACCOUNT_TO_CRAWL_OPTION = "--accountToCrawl";
	
	private static final String APIKEY_OPTION = "--apikey";
	
	private static final String SHARED_SECRET_OPTION = "--sharedSecret";
	
	private static final String DOWNLOAD_FOLDER_OPTION = "--download";
	
	private String accountToCrawl = null;
	
	private String apikey = null;
	
	private String sharedSecret = null;

	/**
	 * download-dir. Null means: don't download
	 */
    private String downloadDirectory = null;
	
	public ExampleFlickrCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setAccountToCrawl(String user) {
		this.accountToCrawl = user;
	}
	
	public void setApikey(String key) {
	    this.apikey = key;
	}
	
	public void setSharedSecret(String pass) {
	    this.sharedSecret = pass;
	}
	
	public String getUsername() {
	    return accountToCrawl;
	}
	
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleFlickrCrawler crawler = new ExampleFlickrCrawler();
		List<String> remainingOptions = crawler.processCommonOptions(args);

		Iterator<String> iterator = remainingOptions.iterator();
		
        while (iterator.hasNext()) {
            // fetch the option name
            String option = iterator.next();
            
            // fetch the option value
            if (!iterator.hasNext()) {
                System.err.println("missing value for option " + option);
                crawler.exitWithUsageMessage();
            }
            
            String value = iterator.next();

            if (ACCOUNT_TO_CRAWL_OPTION.equals(option)) {
                crawler.setAccountToCrawl(value);
            } else if (APIKEY_OPTION.equals(option)) {
                crawler.setApikey(value);
            } else if (DOWNLOAD_FOLDER_OPTION.equals(option)) {
                crawler.setDownloadDirectory(value);
            } else if (SHARED_SECRET_OPTION.equals(option)) {
                crawler.setSharedSecret(value);
            } else
                crawler.exitWithUsageMessage();
        }
		
		if (crawler.getUsername() == null)
		    crawler.exitWithUsageMessage();
		
        // start crawling and exit afterwards
        crawler.crawl();
    }

	public void setDownloadDirectory(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    private void crawl() throws Exception {
		 // create a data source configuration
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer configuration = new RDFContainerImpl(model,new URIImpl("source:testSource"));

        // create the DataSource
        FlickrDataSource source = new FlickrDataSource();
        source.setConfiguration(configuration);
        // download?
        if (downloadDirectory != null)
            source.setCrawlType(CrawlType.MetadataAndPicturesCrawlType);
        else
            source.setCrawlType(CrawlType.MetadataOnlyCrawlType);
        //set the Flickr Username
        source.setAccountToCrawl(accountToCrawl);
        source.setApikey(apikey);
        source.setSharedSecret(sharedSecret);
        
        // setup a crawler that can handle this type of DataSource
        FlickrCrawler crawler = new FlickrCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        
        // start crawling
        crawler.crawl();		
	}

    @Override
    protected String getSpecificExplanationPart() {
        return getAlignedOption(ACCOUNT_TO_CRAWL_OPTION) +"the Flickr account to crawl\n"
             + getAlignedOption(APIKEY_OPTION)+"your Flickr API key\n"     
             + getAlignedOption(SHARED_SECRET_OPTION)+"your Flickr password key\n"
             + getAlignedOption(DOWNLOAD_FOLDER_OPTION) + "directory to download path to the folder where\n"
             + getAlignedOption("") + "pictures are to be downloaded";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return ACCOUNT_TO_CRAWL_OPTION + " <username> " + 
               APIKEY_OPTION + " <apikey> " + 
               SHARED_SECRET_OPTION + " <sharedSecret>" + 
               DOWNLOAD_FOLDER_OPTION + " <folder>";
    }

    
    public String getDownloadDirectory() {
        return downloadDirectory;
    }
}
