/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookCrawlerFactory;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookDataSourceFactory;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressbookCrawlerFactory;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressbookDataSourceFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

public class AddressBookActivator implements BundleActivator {

    public static BundleContext bc;

    private AppleAddressbookCrawlerFactory appleCrawlerFactory;

    private AppleAddressbookDataSourceFactory appleDataSourceFactory;

    private ThunderbirdAddressbookCrawlerFactory thunderbirdCrawlerFactory;

    private ThunderbirdAddressbookDataSourceFactory thunderbirdDataSourceFactory;

    private ServiceRegistration appleCrawlerServiceRegistration;

    private ServiceRegistration appleDataSourceServiceRegistration;
    
    private ServiceRegistration thunderbirdCrawlerServiceRegistration;
    
    private ServiceRegistration thunderbirdDataSourceServiceRegistration;

    public void start(BundleContext context) throws Exception {

        AddressBookActivator.bc = context;

        appleCrawlerFactory = new AppleAddressbookCrawlerFactory();
        appleCrawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(),
            appleCrawlerFactory, new Hashtable());

        appleDataSourceFactory = new AppleAddressbookDataSourceFactory();
        appleDataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), appleDataSourceFactory,
            new Hashtable());
        
        thunderbirdCrawlerFactory = new ThunderbirdAddressbookCrawlerFactory();
        thunderbirdCrawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(),
            thunderbirdCrawlerFactory, new Hashtable());

        thunderbirdDataSourceFactory = new ThunderbirdAddressbookDataSourceFactory();
        thunderbirdDataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), thunderbirdDataSourceFactory,
            new Hashtable());

        
    }

    public void stop(BundleContext context) throws Exception {
        appleCrawlerServiceRegistration.unregister();
        appleDataSourceServiceRegistration.unregister();
        thunderbirdCrawlerServiceRegistration.unregister();
        thunderbirdDataSourceServiceRegistration.unregister();
    }
}
