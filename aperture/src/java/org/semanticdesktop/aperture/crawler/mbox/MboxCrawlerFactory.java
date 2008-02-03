/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.imap.IMAPDS;
import org.semanticdesktop.aperture.datasource.mbox.MBOXDS;

/**
 * Manages instances of the MboxCrawler. 
 */
@SuppressWarnings("unchecked")
public class MboxCrawlerFactory implements CrawlerFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(MBOXDS.MboxDataSource);
    
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
        MboxCrawler crawler = new MboxCrawler();
        crawler.setDataSource(dataSource);
        return crawler;
    }
}
