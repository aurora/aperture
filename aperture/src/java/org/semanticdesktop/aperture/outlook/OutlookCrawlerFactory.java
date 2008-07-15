/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;


/**
 * Create a new Outlook crawler
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookCrawlerFactory implements CrawlerFactory {
	
	@SuppressWarnings("unchecked")
    private HashSet supported;

	/**
	 * constructor
	 */
	@SuppressWarnings("unchecked")
    public OutlookCrawlerFactory() {
		super();
		supported = new HashSet();
		supported.add(OUTLOOKDS.OutlookDataSource);
	}

	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	@SuppressWarnings("unchecked")
    public Set getSupportedTypes() {
		return supported;
	}

	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
		OutlookCrawler crawler = new OutlookCrawler();
		crawler.setDataSource(dataSource);
		return crawler;
	}

}

