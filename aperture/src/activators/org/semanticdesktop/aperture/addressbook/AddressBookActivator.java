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
import org.osgi.framework.ServiceReference;
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

    private ServiceReference appleCrawlerServiceReference;

    private ServiceReference appleDataSourceServiceReference;
    
    private ServiceReference thunderbirdCrawlerServiceReference;
    
    private ServiceReference thunderbirdDataSourceServiceReference;

    public void start(BundleContext context) throws Exception {

        AddressBookActivator.bc = context;

        appleCrawlerFactory = new AppleAddressbookCrawlerFactory();
        ServiceRegistration registration = bc.registerService(CrawlerFactory.class.getName(),
            appleCrawlerFactory, new Hashtable());
        appleCrawlerServiceReference = registration.getReference();

        appleDataSourceFactory = new AppleAddressbookDataSourceFactory();
        registration = bc.registerService(DataSourceFactory.class.getName(), appleDataSourceFactory,
            new Hashtable());
        appleDataSourceServiceReference = registration.getReference();
        
        thunderbirdCrawlerFactory = new ThunderbirdAddressbookCrawlerFactory();
        registration = bc.registerService(CrawlerFactory.class.getName(),
            thunderbirdCrawlerFactory, new Hashtable());
        thunderbirdCrawlerServiceReference = registration.getReference();

        thunderbirdDataSourceFactory = new ThunderbirdAddressbookDataSourceFactory();
        registration = bc.registerService(DataSourceFactory.class.getName(), thunderbirdDataSourceFactory,
            new Hashtable());
        thunderbirdDataSourceServiceReference = registration.getReference();

        
    }

    public void stop(BundleContext context) throws Exception {
        bc.ungetService(appleCrawlerServiceReference);
        bc.ungetService(appleDataSourceServiceReference);
        bc.ungetService(thunderbirdCrawlerServiceReference);
        bc.ungetService(thunderbirdDataSourceServiceReference);
    }
}
