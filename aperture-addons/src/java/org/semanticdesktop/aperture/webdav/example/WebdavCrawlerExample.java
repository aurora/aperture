/**
 * 
 */
package org.semanticdesktop.aperture.webdav.example;


import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.web.WebDataSource;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler;

/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 *
 * Licensed under the Open Software License version 3.0..
 */

/**
 * @author Patrick Ernst
 * 
 * The Class WebdavCrawlerExample shows a few lines of code that uses the webdavcrawler.
 * The main method needs two arguments: 
 * First the rootUrl of the webdav directory and second the path to the start directory. 
 * For example: https://projects.dfki.uni-kl.de/webdav/medico/ and corpus/images/images-2007-12/950004CXCS/1.3.12.2.1107.5.8.2.485257.835054.68674855.20071121151920967373/
 */
public class WebdavCrawlerExample {

	/**
	 * The main method.
	 * 
	 * @param args the args
	 * 
	 * @throws Exception the exception
	 */
    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
    	WebdavCrawlerExample crawler = new WebdavCrawlerExample();
    	
        
        // start crawling and exit afterwards
        crawler.doCrawling(args[0],args[1]);
    }
    
    /**
     * Do crawling.
     * 
     * @param startUrl the start url
     * @param path the path
     * 
     * @throws Exception the exception
     */
    public void doCrawling(String startUrl, String path) throws Exception {
        // First we need a model that will store the data source configuration
        Model model = RDF2Go.getModelFactory().createModel();
        // Don't forget to open it before it can be used
        model.open();
        // Then we wrap it in an RDFContainer
        RDFContainer configuration = new RDFContainerImpl(model, new URIImpl("source:dicomWebdavExtraction"), false);
 
        WebDataSource source = new WebDataSource();
        source.setConfiguration(configuration);

        source.setRootUrl(startUrl);
        source.setIncludeEmbeddedResources(false);
        // this call will prepare a persistent modelSet that will store information
        // in the folder defined above
        ModelSet modelSet = RDF2Go.getModelFactory().createModelSet();
        modelSet.open();
        // this crawler handler implementation (see below) will store information
        // in the provided model set
        SimpleCrawlerHandler handler = new SimpleCrawlerHandler(true, true, true, null, modelSet);
 
        // setup a crawler that can handle this type of DataSource
        WebdavCrawler crawler = new WebdavCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(handler);
        crawler.setPath(path);
        crawler.setUsername("");
        crawler.setPassword("");
        // start crawling
        crawler.crawl();
    }

}
