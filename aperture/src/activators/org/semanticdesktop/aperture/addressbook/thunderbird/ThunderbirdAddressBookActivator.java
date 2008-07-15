/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

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

public class ThunderbirdAddressBookActivator extends BaseDataSourceActivator {

    public ThunderbirdAddressBookActivator() {
        super(ThunderbirdAddressbookCrawlerFactory.class, 
            ThunderbirdAddressbookDataSourceFactory.class, 
            ThunderbirdAddressbookDetector.class, 
            null,
            null);
    }
}
