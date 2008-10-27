/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;

public class ThunderbirdAddressBookActivator extends BaseDataSourceActivator {

    public ThunderbirdAddressBookActivator() {
        super(ThunderbirdAddressbookCrawlerFactory.class, 
            ThunderbirdAddressbookDataSourceFactory.class, 
            ThunderbirdAddressbookDetector.class, 
            null,
            null);
    }
}
