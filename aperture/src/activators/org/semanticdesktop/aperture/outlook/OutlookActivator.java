/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.util.OSUtils;

public class OutlookActivator implements BundleActivator {

	public static BundleContext bc;

	private OutlookCrawlerFactory crawlerFactory;
	private OutlookDataSourceFactory dataSourceFactory;
	private OutlookAccessorFactory accessorFactory;
    private OutlookOpenerFactory openerFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;
	private ServiceRegistration accessorServiceRegistration;
    private ServiceRegistration openerServiceRegistration;

	public void start(BundleContext context) throws Exception {
        OutlookActivator.bc = context;
        if (OSUtils.isWindows()) {
            crawlerFactory = new OutlookCrawlerFactory();
            crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
                new Hashtable());
    
            dataSourceFactory = new OutlookDataSourceFactory();
            dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
                new Hashtable());
    
            accessorFactory = new OutlookAccessorFactory();
            accessorServiceRegistration = bc.registerService(DataAccessorFactory.class.getName(), accessorFactory,
                new Hashtable());
    
            openerFactory = new OutlookOpenerFactory();
            openerServiceRegistration = bc.registerService(DataOpenerFactory.class.getName(), openerFactory, new Hashtable());
        }
    }

	public void stop(BundleContext context) throws Exception {
	    if (OSUtils.isWindows()) {
    		crawlerServiceRegistration.unregister();
            dataSourceServiceRegistration.unregister();
            accessorServiceRegistration.unregister();
            openerServiceRegistration.unregister();
	    }
	}
}
