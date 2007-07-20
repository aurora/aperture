/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.web;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.web.WebDataSourceFactory;


public class WebCrawlerActivator implements BundleActivator {

	public static BundleContext bc;

	private WebCrawlerFactory crawlerFactory;

	private WebDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	public void start(BundleContext context) throws Exception {
		WebCrawlerActivator.bc = context;

		crawlerFactory = new WebCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			new Hashtable());
		
		dataSourceFactory = new WebDataSourceFactory();
		dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
			new Hashtable());
	}

	public void stop(BundleContext context) throws Exception {
		crawlerServiceRegistration.unregister();
        dataSourceServiceRegistration.unregister();
	}
}

