/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * CrawlerHandlers are notified by a Crawler about additions, changes and deletions or resources in a
 * DataSource. Furthermore, they are notified when the Crawler is cleaning up all its crawl results.
 * 
 * <p>
 * Rather than being pure listeners on a Crawler, CrawlerHandlers are also responsible to produce an
 * RDFContainer on demand which the Crawler can use to store the source-specific metadata of a
 * DataObject. It is up to the CrawlerHandler implementor to decide whether a new instance is returned
 * for every DataObject or whether a shared instance is used. It is also responsible for any transaction
 * and context management.
 */
public interface CrawlerHandler {

    /**
     * Notification that the specified Crawler has started crawling its DataSource for DataObjects.
     * 
     * @param crawler The reporting Crawler.
     */
    public void crawlStarted(Crawler crawler);

    /**
     * Notification that the specified Crawler has stopped crawling its DataSource for DataObjects.
     * Reasons for stopping may be that the Crawler might have completed crawling, it may have been
     * requested to stop or it may have stopped because of a fatal exception.
     * 
     * @param crawler The reporting Crawler.
     * @param exitCode The status with which the crawling stopped.
     */
    public void crawlStopped(Crawler crawler, ExitCode exitCode);

    /**
     * Notification that the Crawler is going to start accessing the specified data object.
     * 
     * @param crawler The reporting Crawler.
     * @param url The url of the data object that is going to be accessed.
     */
    public void accessingObject(Crawler crawler, String url);

    /**
     * Returns a RDFContainer that will be used to store the metadata of the next DataObject that will be
     * generated.
     * @return an RDFContainer instance. 
     */
    public RDFContainer getRDFContainer(Crawler crawler, String url);

    /**
     * Notification that the Crawler has found a new resource in the domain it is crawling.
     * 
     * @param crawler The reporting Crawler.
     * @param object The constructed DataObject modeling the new resource.
     */
    public void objectNew(Crawler dataCrawler, DataObject object);

    /**
     * Notification that the Crawler has found a changed resource in the domain it is crawling.
     * 
     * @param crawler The reporting Crawler.
     * @param object The constructed DataObject modeling the changed resource.
     */
    public void objectChanged(Crawler dataCrawler, DataObject object);

    /**
     * Notification that the Crawler has found a resource that has not been modified since the previous
     * crawl.
     * 
     * @param crawler The reporting Crawler.
     * @param url The url of the unmodified resource.
     */
    public void objectNotModified(Crawler crawler, String url);

    /**
     * Notification that the specified resource that has been found in the past could no longer be found.
     * This may indicate that the resource no longer exists or that it now falls outside the scope of the
     * DataSource.
     * 
     * @param crawler The reporting Crawler.
     * @param url The url that could no longer be found.
     */
    public void objectRemoved(Crawler dataCrawler, String url);

    /**
     * Notification that the specified Crawler has started clearing its results. This is followed by a
     * clearingObject invocation on every known url.
     * 
     * @param crawler The reporting Crawler.
     */
    public void clearStarted(Crawler crawler);

    /**
     * Notification that the Crawler is removing all information it knows about the specified url.
     * 
     * @param crawler The reporting Crawler.
     * @param url The url of the resource whose crawl results are being cleared.
     */
    public void clearingObject(Crawler crawler, String url);

    /**
     * Notification that the Crawler has finished clearing its results.
     * 
     * @param crawler The concerning Crawler.
     * @param exitCode The status with which the clearing stopped.
     */
    public void clearFinished(Crawler crawler, ExitCode exitCode);
}
