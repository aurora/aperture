/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.ExitCode;

/**
 * SubCrawlerHandlers are notified by a SubCrawler about additions and modifications of resources in an
 * InputStream.
 * 
 * <p>
 * Rather than being pure listeners on a Crawler, CrawlerHandlers are also responsible to produce an
 * RDFContainer on demand which the Crawler can use to store the source-specific metadata of a DataObject. It
 * is up to the CrawlerHandler implementor to decide whether a new instance is returned for every DataObject
 * or whether a shared instance is used. It is also responsible for any transaction and context management.
 */
public interface SubCrawlerHandler {

    /**
     * Returns an RDFContainerFactory that will be used to provide RDFContainers that will hold a DataObject's
     * metadata.
     * 
     * @param url The url of the resource that is currently being accessed.
     * @return an RDFContainer instance.
     */
    public RDFContainerFactory getRDFContainerFactory(String url);

    /**
     * Notification that the Crawler has found a new resource in the domain it is crawling.
     * 
     * @param object The constructed DataObject modeling the new resource.
     */
    public void objectNew(DataObject object);

    /**
     * Notification that the Crawler has found a changed resource in the domain it is crawling.
     * 
     * @param object The constructed DataObject modeling the changed resource.
     */
    public void objectChanged(DataObject object);

    /**
     * Notification that the Crawler has found a resource that has not been modified since the previous crawl.
     * 
     * @param url The url of the unmodified resource.
     */
    public void objectNotModified(String url);
}
