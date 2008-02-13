/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;

/**
 * A default simple implementation of the SubCrawlerHandler interface. It delegates all callbacks to the
 * CrawlerHandler.
 */
public class DefaultSubCrawlerHandler implements SubCrawlerHandler {

    private CrawlerHandler handler;
    private Crawler crawler;
    
    /**
     * A default constructor.
     * 
     * @param handler the crawler handler that is to receive notifications
     * @param crawler the crawler that will be used as the source of events passed to the crawler handler
     * @throws NullPointerException if the handler is null
     */
    public DefaultSubCrawlerHandler(CrawlerHandler handler, Crawler crawler) {
        if (handler == null) {
            throw new NullPointerException("Null handler is not allowed");
        }
        this.handler = handler;
        this.crawler = crawler;
    }
    
    /**
     * @see SubCrawlerHandler#getRDFContainerFactory(String)
     */
    public RDFContainerFactory getRDFContainerFactory(String url) {
        return handler.getRDFContainerFactory(crawler, url);
    }
    
    /**
     * @see SubCrawlerHandler#objectChanged(DataObject)
     */
    public void objectChanged(DataObject object) {
        handler.objectChanged(crawler, object);
    }
    
    /**
     * @see SubCrawlerHandler#objectNew(DataObject)
     */
    public void objectNew(DataObject object) {
        handler.objectNew(crawler, object);
        
    }
    
    /**
     * @see SubCrawlerHandler#objectNotModified(String)
     */
    public void objectNotModified(String url) {
        handler.objectNotModified(crawler, url);
    }
}
