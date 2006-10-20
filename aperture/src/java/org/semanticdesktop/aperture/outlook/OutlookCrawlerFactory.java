/*
 * Copyright (c) 2005 - 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
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
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;


/**
 * Create a new Outlook crawler
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookCrawlerFactory implements CrawlerFactory {
	
	private HashSet supported;

	/**
	 * 
	 */
	public OutlookCrawlerFactory() {
		super();
		supported = new HashSet();
		supported.add(DATASOURCE_GEN.MicrosoftOutlookDataSource);
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	public Set getSupportedTypes() {
		return supported;
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
		OutlookCrawler crawler = new OutlookCrawler();
		crawler.setDataSource(dataSource);
		return crawler;
	}

}

