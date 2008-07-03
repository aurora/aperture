/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSourceFactory;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.detector.filesystem.HomeFolderDetector;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;

public class FilesystemCrawlerActivator extends BaseDataSourceActivator {

	public FilesystemCrawlerActivator() {
        super(FileSystemCrawlerFactory.class, 
            FileSystemDataSourceFactory.class, 
            HomeFolderDetector.class, 
            null,
            null);
    }

}

