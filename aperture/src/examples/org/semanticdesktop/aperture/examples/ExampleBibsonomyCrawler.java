/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples;

import java.util.List;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyCrawler;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyDataSource;

public class ExampleBibsonomyCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleBibsonomyCrawler.class.getName());

	private String username = null;
	
	public ExampleBibsonomyCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUserName(String user) {
		this.username = user;
	}
	
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleBibsonomyCrawler crawler = new ExampleBibsonomyCrawler();
		List<String> remainingOptions = crawler.processCommonOptions(args);
		
		if (remainingOptions.size() == 1) {
		    crawler.setUserName(remainingOptions.get(0));
		} else {
		    crawler.exitWithUsageMessage();
		}
		
        // start crawling and exit afterwards
        crawler.crawl();
    }

	private void crawl() {
		 // create a data source configuration
	    Model model = RDF2Go.getModelFactory().createModel();
	    model.open();
	    RDFContainer configuration = new RDFContainerImpl(model, new URIImpl("source:testSource"));
        
        // create the DataSource
        BibsonomyDataSource source = new BibsonomyDataSource();
        //set the Bibsonomy Username
        source.setConfiguration(configuration);
        source.setUsername(username);
        source.setCrawlType(BibsonomyDataSource.CrawlType.TagsAndItemsCrawlType);
        
        
        // setup a crawler that can handle this type of DataSource
        BibsonomyCrawler crawler = new BibsonomyCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setAccessData(new AccessDataImpl());
        crawler.setCrawlerHandler(getHandler());
        
        // start crawling
        crawler.crawl();
	}

    @Override
    protected String getSpecificExplanationPart() {
        return "    username - your bibsonomy user name";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "username";
        
    }
}
