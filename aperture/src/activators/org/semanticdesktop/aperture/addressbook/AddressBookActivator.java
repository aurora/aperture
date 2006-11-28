/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

public class AddressBookActivator implements BundleActivator {

	public static BundleContext bc;

	private AddressbookCrawlerFactory crawlerFactory;

	private AddressbookDataSourceFactory dataSourceFactory;

	private ServiceReference crawlerServiceReference;
	private ServiceReference dataSourceServiceReference;

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());

		AddressBookActivator.bc = context;

		crawlerFactory = new AddressbookCrawlerFactory();
		ServiceRegistration registration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
			new Hashtable());
		crawlerServiceReference = registration.getReference();
		
		dataSourceFactory = new AddressbookDataSourceFactory();
		registration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
			new Hashtable());
		dataSourceServiceReference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(crawlerServiceReference);
		bc.ungetService(dataSourceServiceReference);
	}

}
