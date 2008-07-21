/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.delicious;

import java.io.File;

import org.ontoware.rdf2go.model.ModelSet;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.examples.ExampleDeliciousCrawler;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;


public class DeliciousCrawlerTest extends ApertureTestBase {

    ExampleDeliciousCrawler crawler;
    DeliciousDataSource deliciousDataSource;
    protected void setUp() throws Exception {
        
         crawler = new ExampleDeliciousCrawler();
         crawler.setUserName("aperture_test");
         crawler.setPassword("junit21");
         crawler.setHandler(new SimpleCrawlerHandler(true, true, true,new File("crawl.rdf"),null));
       
    }

    protected void tearDown() throws Exception {
        
    }

    public void testCrawl() throws Exception {
        crawler.crawl();
        SimpleCrawlerHandler handler = crawler.getHandler();
        ModelSet modelSet = handler.getModelSet();
        modelSet.open();
        modelSet.dump();
        //TODO verify if content is correct and valid NIE
    }

}

