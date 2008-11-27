/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;

import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
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
