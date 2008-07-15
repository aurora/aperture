/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

public class ThunderbirdAddressbookCrawlerFactory implements CrawlerFactory {

	public Set getSupportedTypes() {
		return Collections.singleton(THUNDERBIRDADDRESSBOOKDS.ThunderbirdAddressbookDataSource);
	}

	public Crawler getCrawler(DataSource dataSource) {
			return new ThunderbirdCrawler(dataSource);
	}
}

