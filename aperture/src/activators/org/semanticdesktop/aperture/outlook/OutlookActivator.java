/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
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
import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.util.OSUtils;

public class OutlookActivator extends BaseDataSourceActivator {

	public OutlookActivator() {
        super(OutlookCrawlerFactory.class, 
            OutlookDataSourceFactory.class,
            OutlookDataSourceDetector.class,
            OutlookAccessorFactory.class,
            OutlookOpenerFactory.class);
    }

    public void start(BundleContext context) throws Exception {
        if (OSUtils.isWindows()) {
            super.start(context);
        }
    }

	public void stop(BundleContext context) throws Exception {
        if (OSUtils.isWindows()) {
            super.stop(context);
        }
	}
}
