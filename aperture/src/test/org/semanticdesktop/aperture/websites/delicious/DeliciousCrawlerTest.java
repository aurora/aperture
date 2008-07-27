/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.delicious;

import org.semanticdesktop.aperture.ApertureTestBase;


public class DeliciousCrawlerTest extends ApertureTestBase {
    
// commented the code below because it used the ExampleDeliciousCrawler
// ExampleDeliciousCrawler is in the example folder, which is NOT on the classpath
// when compiling the test folder, thus the code below made the build fail
    
//    ExampleDeliciousCrawler crawler;
//    DeliciousDataSource deliciousDataSource;
//    protected void setUp() throws Exception {
//        
//         crawler = new ExampleDeliciousCrawler();
//         crawler.setUserName("aperture_test");
//         crawler.setPassword("junit21");
//         crawler.setHandler(new SimpleCrawlerHandler(true, true, true,new File("crawl.rdf"),null));
//       
//    }
//
//    protected void tearDown() throws Exception {
//        
//    }
//
//    public void testCrawl() throws Exception {
//        crawler.crawl();
//        SimpleCrawlerHandler handler = crawler.getHandler();
//        
//        ModelSet modelSet = handler.getModelSet();
//        modelSet.open();
//        modelSet.dump();
//        assertTrue(modelSet.containsStatements(Variable.ANY, new URIImpl("http://aperture.sourceforge.net/"), new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#plainTextContent"), Variable.ANY));
//    }

}

