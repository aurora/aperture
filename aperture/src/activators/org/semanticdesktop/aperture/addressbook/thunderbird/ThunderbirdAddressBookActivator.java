/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

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

public class ThunderbirdAddressBookActivator implements BundleActivator {

    public static BundleContext bc;

    private ThunderbirdAddressbookCrawlerFactory thunderbirdCrawlerFactory;

    private ThunderbirdAddressbookDataSourceFactory thunderbirdDataSourceFactory;
    
    private ServiceRegistration thunderbirdCrawlerServiceRegistration;
    
    private ServiceRegistration thunderbirdDataSourceServiceRegistration;

    public void start(BundleContext context) throws Exception {

        ThunderbirdAddressBookActivator.bc = context;
        
        thunderbirdCrawlerFactory = new ThunderbirdAddressbookCrawlerFactory();
        thunderbirdCrawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(),
            thunderbirdCrawlerFactory, null);

        thunderbirdDataSourceFactory = new ThunderbirdAddressbookDataSourceFactory();
        thunderbirdDataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), thunderbirdDataSourceFactory,
            null);

        
    }

    public void stop(BundleContext context) throws Exception {
        thunderbirdCrawlerServiceRegistration.unregister();
        thunderbirdDataSourceServiceRegistration.unregister();
    }
}
