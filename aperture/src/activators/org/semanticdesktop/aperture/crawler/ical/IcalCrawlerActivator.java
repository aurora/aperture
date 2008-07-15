/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSourceFactory;


public class IcalCrawlerActivator implements BundleActivator {

	public static BundleContext bc;

	private IcalCrawlerFactory crawlerFactory;

	private IcalDataSourceFactory dataSourceFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;

	public void start(BundleContext context) throws Exception {
		

		IcalCrawlerActivator.bc = context;

		crawlerFactory = new IcalCrawlerFactory();
		crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			new Hashtable());
		
		dataSourceFactory = new IcalDataSourceFactory();
		dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
			new Hashtable());
	}

	public void stop(BundleContext context) throws Exception {
		crawlerServiceRegistration.unregister();
        dataSourceServiceRegistration.unregister();
	}
}

