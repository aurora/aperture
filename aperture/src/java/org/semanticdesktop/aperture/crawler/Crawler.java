/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A Crawler accesses the physical source represented by a DataSource and delivers a stream of DataObjects
 * representing the resources in that source.
 * 
 * <p>
 * An AccessData instance can optionally be specified to a Crawler, allowing it to perform incremental
 * crawling, i.e. to scan and report the differences in the data source since the last crawl.
 */
public interface Crawler {

	/**
	 * Returns the DataSource crawled by this Crawler.
	 */
	public DataSource getDataSource();

	/**
	 * Sets the DataAccessorRegistry to obtain DataAccessorFactories from.
	 * 
	 * @param registry The DataAccessorRegistry to use, or 'null' when then DataAccessorRegistry should be
	 *            unset.
	 */
	public void setDataAccessorRegistry(DataAccessorRegistry registry);

	/**
	 * Returns the DataAccessorRegistry currently used by this Crawler.
	 * 
	 * @return The currently used DataAccessorRegistry, or 'null' when no DataAccessorRegistry has been set.
	 */
	public DataAccessorRegistry getDataAccessorRegistry();

	/**
	 * Sets the AccessData instance to be used.
	 * 
	 * @param accessData The AccessData instance to use, or 'null' when no AccessData is to be used.
	 */
	public void setAccessData(AccessData accessData);

	/**
	 * Returns the AccessData used by this Crawler.
	 * 
	 * @return The AccessData used by this Crawler, or 'null' when no AccessData is used.
	 */
	public AccessData getAccessData();

	/**
	 * Starts crawling the domain defined in the DataSource of this Crawler. If this is a subsequent run of
	 * this method, it will only report the differences with the previous run, unless the previous scan
	 * results have been cleared. Any CrawlerListeners registered on this Crawler will get notified about the
	 * crawling progress.
	 */
	public void crawl();

	/**
	 * Clears all stored crawl results. Any listeners registered with this data source will be notified of the
	 * removal of the individual crawl results. The next call to crawl() will again report all DataObjects in
	 * the configured domain.
	 */
	public void clear();

	/**
	 * Stops a running crawl or clear operation as fast as possible. This method may return before the
	 * crawling has actually stopped.
	 */
	public void stop();

	/**
	 * Gets the CrawlReport of the last performed crawl, or the current crawl when it is in progress. Returns
	 * null when no crawl has been performed in this application's session yet and there is no report
	 * available from the previous session.
	 * 
	 * @return The CrawlReport of the last run, or null when this is not available.
	 */
	public CrawlReport getCrawlReport();

	/**
	 * Sets the CrawlerHandler to which this Crawler should report any scanned or cleared resources and from
	 * which it obtains RDFContainer.
	 * 
	 * @param handler The CrawlerHandler to register.
	 */
	public void setCrawlerHandler(CrawlerHandler handler);

	/**
	 * Returns the currently registered CrawlerHandler.
	 * 
	 * @return The current CrawlerHandler.
	 */
	public CrawlerHandler getCrawlerHandler();
}
