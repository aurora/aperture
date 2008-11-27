/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.flickr;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * Bibsonomy activator
 */
public class FlickrActivator implements BundleActivator {

	private static BundleContext bc;

	private FlickrCrawlerFactory crawlerFactory;

	private FlickrDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	/**
	 * Starts the bundle
	 * @param context the bundle context
	 * @throws Exception if something goes wrong
	 */
    public void start(BundleContext context) throws Exception {
		FlickrActivator.bc = context;

		crawlerFactory = new FlickrCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			null);
		
		dataSourceFactory = new FlickrDataSourceFactory();
		dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
			null);
	}

    /**
     * Stops the bundle, unregisters the services
     * @param context the bundle context
     * @throws Exception if something goes wrong in the process
     */
	public void stop(BundleContext context) throws Exception {
		crawlerServiceRegistration.unregister();
        dataSourceServiceRegistration.unregister();
	}
}

