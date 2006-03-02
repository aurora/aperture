/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * Manages instances of ImapCrawler. 
 */
public class ImapCrawlerFactory implements CrawlerFactory, DataAccessorFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(DATASOURCE_GEN.IMAPDataSource);

    private static final Set SUPPORTED_SCHEMES = Collections.singleton("imap");
    
    public Set getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public Crawler getCrawler(DataSource dataSource) {
        ImapCrawler crawler = new ImapCrawler();
        crawler.setDataSource(dataSource);
        return crawler;
    }

    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }

    public DataAccessor get() {
        return new ImapCrawler();
    }
}
