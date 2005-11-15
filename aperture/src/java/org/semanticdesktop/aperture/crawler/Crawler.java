/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import org.semanticdesktop.aperture.access.datasource.DataSource;

/**
 * A Crawler accesses the physical source represented by a DataSource and delivers a stream of
 * DataObjects representing the resources in that source.
 */
public interface Crawler {

    /**
     * Returns the DataSource crawled by this Crawler.
     */
    public DataSource getDataSource();

    /**
     * Starts crawling the domain defined in the DataSource of this Crawler. If this is a subsequent run
     * of this method, it will only report the differences with the previous run, unless the previous
     * scan results have been cleared. Any CrawlerListeners registered on this Crawler will get notified
     * about the crawling progress.
     */
    public void crawl();

    /**
     * Clears all stored crawl results. Any listeners registered with this data source will be notified
     * of the removal of the individual crawl results. The next call to crawl() will again report all
     * DataObjects in the configured domain.
     */
    public void clear();

    /**
     * Stops a running crawl or clear operation as fast as possible. This method may return before the
     * crawling has actually stopped.
     */
    public void stop();

    /**
     * Gets the CrawlReport of the last performed crawl, or the current crawl when it is in progress.
     * Returns null when no crawl has been performed in this application's session yet and there is no
     * report available from the previous session.
     * 
     * @return The CrawlReport of the last run, or null when this is not available.
     */
    public CrawlReport getCrawlReport();

    /**
     * Adds a CrawlerListener to which this Crawler should report any scanned or cleared resources.
     * 
     * @param listener The CrawlerListener to add.
     */
    public void addListener(CrawlerListener listener);

    /**
     * Removes a CrawlerListener from this Crawler. Nothing happens when the specified CrawlerListener
     * was not registered on this Crawler.
     * 
     * @param listener The CrawlerListener to remove.
     */
    public void removeListener(CrawlerListener listener);
}
