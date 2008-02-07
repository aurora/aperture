/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import java.io.InputStream;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A SubCrawler accesses an InputStream and produces a stream of other DataObjects representing the
 * resources found "inside".
 * 
 * <p>
 * An AccessData instance can optionally be specified to a SubCrawler, allowing it to perform incremental
 * crawling, i.e. to scan and report the differences in the stream since the last crawl.
 */
public interface SubCrawler {

    /**
     * Starts crawling the given stream and to report the encountered DataObjects to the given CrawlerHandler.
     * If an AccessData instance is passed, it is used to check if the data objects are to be reported as new,
     * modified, unmodified or deleted.
     * 
     * @param stream the stream to be crawled.
     * @param accessData the AccessData used to determine if the encountered objects are to be returned as
     *            new, modified, unmodified or deleted. Information about new or modified objects is stored
     *            within for use in future crawls. This parameter may be null if this functionality is not
     *            desired, in which case all DataObjects will be reported as new.
     * @param handler The crawler handler that is to receive the notifications from the SubCrawler
     * @param result The 'parent' RDFContainer, that will contain the metadata about the top-level entity in
     *            the stream. A SubCrawler may (in some cases) limit itself to augmenting the metadata in this
     *            RDFContainer without delivering any additional DataObjects.
     */
    public void subCrawl(InputStream stream, AccessData accessData, CrawlerHandler handler,
            RDFContainer result);
    
    /**
     * Clears the information the SubCrawler had about the state of the stream from the given AccessData
     * instance. The given handler instance receives notifications about the clearing process. See the
     * {@link Crawler#clear()} documentation for more details.
     * 
     * @param data the accessData that is to be cleared
     * @param handler the CrawlerHandler that is to receive notifications
     */
    public void clearSubCrawledResults(AccessData data, CrawlerHandler handler);
    
    /**
     * Stops a running crawl or a clear operation as fast as possible. This method may return before the
     * crawling has actually stopped.
     */
    public void stopSubCrawler();
}
