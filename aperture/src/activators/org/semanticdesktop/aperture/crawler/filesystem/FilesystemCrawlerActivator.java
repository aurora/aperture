/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import org.semanticdesktop.aperture.datasource.BaseDataSourceActivator;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSourceFactory;
import org.semanticdesktop.aperture.detector.filesystem.HomeFolderDetector;

public class FilesystemCrawlerActivator extends BaseDataSourceActivator {

	public FilesystemCrawlerActivator() {
        super(FileSystemCrawlerFactory.class, 
            FileSystemDataSourceFactory.class, 
            HomeFolderDetector.class, 
            null,
            null);
    }

}

