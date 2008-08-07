/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
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
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.websites.flickr.FlickrCrawler;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource.CrawlType;

public class ExampleFlickrCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleFlickrCrawler.class.getName());

	private String username = null;

	/**
	 * download-dir. Null means: don't download
	 */
    private String downloadDirectory = null;
	
	public ExampleFlickrCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUsername(String user) {
		this.username = user;
	}
	
	public String getUsername() {
	    return username;
	}
	
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleFlickrCrawler crawler = new ExampleFlickrCrawler();
		List<String> remainingOptions = crawler.processCommonOptions(args);

		Iterator<String> iterator = remainingOptions.iterator();
		if (iterator.hasNext()) {
		    String arg = iterator.next();
		    if ("-download".equals(arg))
		    {
		        String val = iterator.next();
		        if (val==null)
		            crawler.exitWithUsageMessage();
		        crawler.setDownloadDirectory(val);
		    } else // the rest
		        crawler.setUsername(arg);
		} else
		    crawler.exitWithUsageMessage();
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
        source.setUsername(username);
        
        // setup a crawler that can handle this type of DataSource
        FlickrCrawler crawler = new FlickrCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setAccessData(new AccessDataImpl());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        
        // start crawling
        crawler.crawl();		
	}

    @Override
    protected String getSpecificExplanationPart() {
        return getAlignedOption("-download") +"set <pathfordownload> to a directory where\n" +
        	           getAlignedOption(null)+"downloaded pictures should be stored\n"
            + getAlignedOption("username")+"your Flickr username";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "[--download <pathfordownload>] username";
    }

    
    public String getDownloadDirectory() {
        return downloadDirectory;
    }
}
