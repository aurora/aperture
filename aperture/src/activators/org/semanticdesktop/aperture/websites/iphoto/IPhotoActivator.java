/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.iphoto;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * Bibsonomy activator
 */
public class IPhotoActivator implements BundleActivator {

	private static BundleContext bc;

	private IPhotoKeywordCrawlerFactory crawlerFactory;

	private IPhotoKeywordDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	/**
	 * Starts the bundle
	 * @param context the bundle context
	 * @throws Exception if something goes wrong
	 */
    public void start(BundleContext context) throws Exception {
		IPhotoActivator.bc = context;

		crawlerFactory = new IPhotoKeywordCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			null);
		
		dataSourceFactory = new IPhotoKeywordDataSourceFactory();
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

