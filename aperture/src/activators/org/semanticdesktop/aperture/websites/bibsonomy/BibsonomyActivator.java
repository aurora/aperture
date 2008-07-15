/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.bibsonomy;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * Bibsonomy activator
 */
public class BibsonomyActivator implements BundleActivator {

	private static BundleContext bc;

	private BibsonomyCrawlerFactory crawlerFactory;

	private BibsonomyDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	/**
	 * Starts the bundle
	 * @param context the bundle context
	 * @throws Exception if something goes wrong
	 */
    public void start(BundleContext context) throws Exception {
		BibsonomyActivator.bc = context;

		crawlerFactory = new BibsonomyCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			null);
		
		dataSourceFactory = new BibsonomyDataSourceFactory();
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

