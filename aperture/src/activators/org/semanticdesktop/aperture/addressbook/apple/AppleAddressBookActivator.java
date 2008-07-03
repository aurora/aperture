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
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookCrawlerFactory;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookDataSourceFactory;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressbookCrawlerFactory;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressbookDataSourceFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.util.OSUtils;

public class AppleAddressBookActivator extends BaseDataSourceActivator {
    
    public AppleAddressBookActivator() {
        super(AppleAddressbookCrawlerFactory.class, 
            AppleAddressbookDataSourceFactory.class, 
            AppleAddressbookDetector.class, 
            null,
            null);
    }

    public void start(BundleContext context) throws Exception {
        if (OSUtils.isMac()) {
            super.start(context);
        }
    }

    public void stop(BundleContext context) throws Exception {
        if (OSUtils.isMac()) {
            super.stop(context);
        }
    }
}
