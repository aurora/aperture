/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.addressbook.apple;

import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
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
