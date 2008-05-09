/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSourceFactory;

/**
 * An activator of the mbox crawler bundle
 */
public class MboxCrawlerActivator implements BundleActivator {

	private static BundleContext bc;

	private MboxCrawlerFactory crawlerFactory;

	private MboxDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration accessorServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	/**
	 * Starts the mbox bundle, registers the crawler, accessor and datasource factories.
	 * @param context the bundle context
	 */
	public void start(BundleContext context) {
        MboxCrawlerActivator.bc = context;

		crawlerFactory = new MboxCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			null);
		accessorServiceRegistration = bc.registerService(DataAccessorFactory.class.getName(), crawlerFactory,
            null);
		
		dataSourceFactory = new MboxDataSourceFactory();
		dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
			null);
	}

	/**
     * Starts the imap bundle, unregisters the crawler, accessor and datasource factories.
     * @param context the bundle context
     */
	public void stop(BundleContext context) throws Exception {
		crawlerServiceRegistration.unregister();
		accessorServiceRegistration.unregister();
        dataSourceServiceRegistration.unregister();
	}
}

