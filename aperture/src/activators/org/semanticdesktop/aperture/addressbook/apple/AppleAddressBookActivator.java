/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.apple;

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
import org.semanticdesktop.aperture.util.OSUtils;

public class AppleAddressBookActivator implements BundleActivator {

    public static BundleContext bc;

    private AppleAddressbookCrawlerFactory appleCrawlerFactory;

    private AppleAddressbookDataSourceFactory appleDataSourceFactory;

    private ServiceRegistration appleCrawlerServiceRegistration;

    private ServiceRegistration appleDataSourceServiceRegistration;

    public void start(BundleContext context) throws Exception {
        AppleAddressBookActivator.bc = context;
        if (OSUtils.isMac()) {
            appleCrawlerFactory = new AppleAddressbookCrawlerFactory();
            appleCrawlerServiceRegistration = bc.registerService(CrawlerFactory.class.getName(),
                appleCrawlerFactory, null);
    
            appleDataSourceFactory = new AppleAddressbookDataSourceFactory();
            appleDataSourceServiceRegistration = bc.registerService(DataSourceFactory.class.getName(), appleDataSourceFactory,
                null);
        }
    }

    public void stop(BundleContext context) throws Exception {
        if (OSUtils.isMac()) {
            appleCrawlerServiceRegistration.unregister();
            appleDataSourceServiceRegistration.unregister();
            appleCrawlerServiceRegistration = null;
            appleDataSourceServiceRegistration = null;
        }
    }
}
