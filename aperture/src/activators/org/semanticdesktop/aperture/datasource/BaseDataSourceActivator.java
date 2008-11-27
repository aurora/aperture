/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;

/**
 * Generic DataSourceActivator that 
 * registers DataSource, CrawlerFactory, DataSourceDetector, Opener, and Accessor.
 * @author sauermann
 */
public class BaseDataSourceActivator implements BundleActivator {

	public BundleContext bc;
	
	// the classes to create the instances or null. these are set on constructing.
	final private Class<? extends CrawlerFactory> crawlerFactoryClass;
	final private Class<? extends DataSourceFactory> dataSourceFactoryClass;
	final private Class<? extends DataSourceDetector> dataSourceDetectorClass;
	final private Class<? extends DataAccessorFactory> accessorFactoryClass;
	final private Class<? extends DataOpenerFactory> openerFactoryClass;

	// instances or null, created on start
	private CrawlerFactory crawlerFactory;
	private DataSourceFactory dataSourceFactory;
	private DataSourceDetector dataSourceDetector;
	private DataAccessorFactory accessorFactory;
	private DataOpenerFactory openerFactory;

	private ServiceRegistration crawlerServiceRegistration;
	private ServiceRegistration dataSourceServiceRegistration;
	private ServiceRegistration dataSourceDetectorServiceRegistration;
	private ServiceRegistration accessorServiceRegistration;
	private ServiceRegistration openerServiceRegistration;
	


    /**
     * Create a new DataSourceActivator that instances and registers the given objects.
     * any parameter can be <code>null</code>.
     * @param crawlerFactoryClass
     * @param dataSourceFactoryClass
     * @param dataSourceDetectorClass
     * @param accessorFactoryClass this will be null when you don't need special accessors
     * @param openerFactoryClass this will be null when you don't need special openers
     */
    public BaseDataSourceActivator(Class<? extends CrawlerFactory> crawlerFactoryClass,
            Class<? extends DataSourceFactory> dataSourceFactoryClass,
            Class<? extends DataSourceDetector> dataSourceDetectorClass,
            Class<? extends DataAccessorFactory> accessorFactoryClass, 
            Class<? extends DataOpenerFactory> openerFactoryClass) {
        super();
        this.crawlerFactoryClass = crawlerFactoryClass;
        this.dataSourceFactoryClass = dataSourceFactoryClass;
        this.dataSourceDetectorClass = dataSourceDetectorClass;
        this.accessorFactoryClass = accessorFactoryClass;
        this.openerFactoryClass = openerFactoryClass;
    }

    public void start(BundleContext context) throws Exception {
		bc = context;

		if (crawlerFactoryClass != null)
		{
		    crawlerFactory = crawlerFactoryClass.newInstance();
		    crawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(), crawlerFactory,
		        new Hashtable());
		}
		
		if (dataSourceFactoryClass != null) 
		{
		    dataSourceFactory = dataSourceFactoryClass.newInstance();
		    dataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), dataSourceFactory,
		        new Hashtable());
		}
		
		if (dataSourceDetectorClass != null)
		{
		    dataSourceDetector = dataSourceDetectorClass.newInstance();
		    dataSourceDetectorServiceRegistration = bc.registerService(DataSourceDetector.class.getName(), dataSourceDetector,
                new Hashtable());
		}
		
		if (accessorFactoryClass != null)
        {
		    accessorFactory = accessorFactoryClass.newInstance();
		    accessorServiceRegistration = bc.registerService(DataAccessorFactory.class.getName(), accessorFactory,
                new Hashtable());
        }

		if (openerFactoryClass != null)
		{
		    openerFactory = openerFactoryClass.newInstance();
		    openerServiceRegistration = bc.registerService(DataOpenerFactory.class.getName(), openerFactory,
		        new Hashtable());
		}
		
	}

	public void stop(BundleContext context) throws Exception {
	    if (crawlerServiceRegistration != null)
	    {
	        crawlerServiceRegistration.unregister();
	        crawlerServiceRegistration = null;
	        crawlerFactory = null;
	    }
	    if (dataSourceServiceRegistration != null) {
	        dataSourceServiceRegistration.unregister();
	        dataSourceServiceRegistration = null;
	        dataSourceFactory = null;
	    }
	    if (dataSourceDetectorServiceRegistration != null) {
	        dataSourceDetectorServiceRegistration.unregister();
	        dataSourceDetectorServiceRegistration = null;
	        dataSourceDetector = null;
	    }
	    if (accessorServiceRegistration != null) {
	        accessorServiceRegistration.unregister();
	        accessorServiceRegistration = null;
	        accessorFactory = null;
	    }
	    if (openerServiceRegistration != null) {
	        openerServiceRegistration.unregister();
	        openerServiceRegistration = null;
	        openerFactory = null;
	    }
	}
}