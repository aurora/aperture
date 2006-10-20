/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

import java.util.Set;

import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * CrawlerFactories created Crawlers capable of crawling specific types of DataSources. Rather than
 * referring to specific DataSource implementations, a CrawlerFactory refers to the DataSource type URIs
 * it supports.
 * 
 * @see org.semanticdesktop.aperture.datasource.DataSource#getType()
 */
public interface CrawlerFactory {

    /**
     * Returns a set of URIs indicating the DataSource types that are supported by the Crawler
     * implementation provided by this CrawlerFactory.
     * 
     * @return A Set of DataSource type URIs.
     */
    public Set getSupportedTypes();

    /**
     * Return a Crawler that can crawl the specified DataSource. The getType method of this DataSource
     * should return a URI that is contained in the supported types set of this CrawlerFactory.
     * 
     * @param dataSource The DataSource for which a Crawler needs to be generated.
     * @return A Crawler whose getDataSource method returns the specified DataSource.
     */
    public Crawler getCrawler(DataSource dataSource);
}
