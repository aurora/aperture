/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class AddressbookCrawlerFactory implements CrawlerFactory {

	private static final Logger LOGGER = Logger.getLogger(AddressbookCrawlerFactory.class.getName());
	
	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	public Set getSupportedTypes() {
		return Collections.singleton(AddressbookDataSource.type);
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
		RDFContainer config=dataSource.getConfiguration();
		String type=config.getString(DATASOURCE.flavour);
		
		if (type.equalsIgnoreCase("thunderbird")) {
			return new ThunderbirdCrawler(dataSource);
		} else { 
			LOGGER.severe("Unknown AddressbookDataSource flavour: "+type);
			return null;
		}
	}

}

