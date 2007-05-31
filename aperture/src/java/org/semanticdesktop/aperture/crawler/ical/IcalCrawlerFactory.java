/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * Provides instances of IcalCrawler.
 */
public class IcalCrawlerFactory implements CrawlerFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(DATASOURCE_GEN.IcalDataSource);

    public Set getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public Crawler getCrawler(DataSource dataSource) {
        IcalCrawler crawler = new IcalCrawler();
        crawler.setDataSource(dataSource);
        return crawler;
    }
}
