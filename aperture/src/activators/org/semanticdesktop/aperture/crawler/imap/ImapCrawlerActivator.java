/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSourceFactory;


public class ImapCrawlerActivator implements BundleActivator {

	public static BundleContext bc;

	private ImapCrawlerFactory crawlerFactory;

	private ImapDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration accessorServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	/**
	 * Starts the imap bundle, registers the crawler, accessor and datasource factories.
	 * @param context the bundle context
	 */
	public void start(BundleContext context) {
        ImapCrawlerActivator.bc = context;

		crawlerFactory = new ImapCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			null);
		accessorServiceRegistration = bc.registerService(DataAccessorFactory.class.getName(), crawlerFactory,
            null);
		
		dataSourceFactory = new ImapDataSourceFactory();
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

