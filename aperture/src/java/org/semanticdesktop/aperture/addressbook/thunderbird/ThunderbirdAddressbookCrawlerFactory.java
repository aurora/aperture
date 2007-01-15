/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookCrawler;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdCrawler;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class ThunderbirdAddressbookCrawlerFactory implements CrawlerFactory {

	private static final Logger LOGGER = Logger.getLogger(ThunderbirdAddressbookCrawlerFactory.class.getName());
	
	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	public Set getSupportedTypes() {
		return Collections.singleton(DATASOURCE.ThunderbirdAddressbookDataSource);
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
			return new ThunderbirdCrawler(dataSource);
	}

}

