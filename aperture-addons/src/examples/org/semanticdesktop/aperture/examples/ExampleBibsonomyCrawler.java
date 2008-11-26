/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyCrawler;
import org.semanticdesktop.aperture.websites.bibsonomy.BibsonomyDataSource;

public class ExampleBibsonomyCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleBibsonomyCrawler.class.getName());
	
	private static final String USERNAME_OPTION = "--username";
	
	private static final String API_KEY_OPTION = "--apikey";
	
	private static final String USERNAME_TO_CRAWL_OPTION = "--usernameToCrawl";

	private String username = null;
	
	private String apikey = null;
	
	private String usernameToCrawl;
	
	public ExampleBibsonomyCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUserName(String user) {
		this.username = user;
	}
	
	public void setApiKey(String key) {
		this.apikey = key;
	}
	
	public void setUsernameToCrawl(String name) {
		this.usernameToCrawl = name;
	}
	
	
	
	
	
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    
    /**
     * @return the apikey
     */
    public String getApikey() {
        return apikey;
    }

    
    /**
     * @return the usernameToCrawl
     */
    public String getUsernameToCrawl() {
        return usernameToCrawl;
    }

    public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleBibsonomyCrawler crawler = new ExampleBibsonomyCrawler();
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
            
            if (USERNAME_OPTION.equals(option)) {
            	crawler.setUserName(value);
            } else if (API_KEY_OPTION.equals(option)) {
            	crawler.setApiKey(value);
            } else if (USERNAME_TO_CRAWL_OPTION.equals(option)) {
            	crawler.setUsernameToCrawl(value);
            } else {
            	System.err.println("Unknown option: " + option);
            	crawler.exitWithUsageMessage();
            }            
        }
		
        if (crawler.getUsername() == null || crawler.getUsernameToCrawl() == null ||
                crawler.getApikey() == null) {
            System.err.println("Must specifiy the username, the api key and the account to crawl");
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
        source.setApiusername(username);
        source.setApikey(apikey);
        source.setCrawledusername(usernameToCrawl);
        
        
        // setup a crawler that can handle this type of DataSource
        BibsonomyCrawler crawler = new BibsonomyCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        // start crawling
        crawler.crawl();
	}

    @Override
    protected String getSpecificExplanationPart() {
    	StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.println(getAlignedOption(USERNAME_TO_CRAWL_OPTION) + "the bibsonomy account you'd like to crawl");
        writer.println(getAlignedOption(USERNAME_OPTION) + "your Bibsonomy user name");
        writer.println(getAlignedOption(API_KEY_OPTION) + "your Bibsonomy api key");
        return stringWriter.toString();
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "--username username --apikey key";
        
    }
}
