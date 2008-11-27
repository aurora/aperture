/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.flickr;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A factory of Flickr Crawlers
 */
public class FlickrCrawlerFactory implements CrawlerFactory {
		
	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	@SuppressWarnings("unchecked")
    public Set getSupportedTypes() {
		return Collections.singleton(FLICKRDS.FlickrDataSource);
	}

	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
        return new FlickrCrawler(dataSource);
	}

}
