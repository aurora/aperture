/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.Vocabulary;

/**
 * Provides instances of FileSystemCrawler.
 */
public class FileSystemCrawlerFactory implements CrawlerFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(Vocabulary.FILE_SYSTEM_DATA_SOURCE);

    private DataAccessorRegistry accessorRegistry;

    public Set getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public void setDataAccessorRegistry(DataAccessorRegistry registry) {
        this.accessorRegistry = registry;
    }

    public DataAccessorRegistry getDataAccessorRegistry() {
        return accessorRegistry;
    }

    public Crawler getCrawler(DataSource dataSource) {
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(dataSource);
        crawler.setDataAccessorRegistry(accessorRegistry);
        return crawler;
    }
}
