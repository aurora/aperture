/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.filesystem.FILESYSTEMDS;

/**
 * Provides instances of FileSystemCrawler.
 */
@SuppressWarnings("unchecked")
public class FileSystemCrawlerFactory implements CrawlerFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(FILESYSTEMDS.FileSystemDataSource);

    /**
     * @see CrawlerFactory#getSupportedTypes()
     */
    public Set getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * @see CrawlerFactory#getCrawler(DataSource)
     */
    public Crawler getCrawler(DataSource dataSource) {
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(dataSource);
        return crawler;
    }
}
