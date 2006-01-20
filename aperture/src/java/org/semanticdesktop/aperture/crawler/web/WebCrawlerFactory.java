/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.web;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;

public class WebCrawlerFactory implements CrawlerFactory {

    private static final Set SUPPORTED_TYPES = Collections.singleton(Vocabulary.WEB_DATA_SOURCE);

    private DataAccessorRegistry dataAccessorRegistry;

    private MimeTypeIdentifier mimeTypeIdentifier;

    private LinkExtractorRegistry linkExtractorRegistry;

    public DataAccessorRegistry getDataAccessorRegistry() {
        return dataAccessorRegistry;
    }

    public void setDataAccessorRegistry(DataAccessorRegistry dataAccessorRegistry) {
        this.dataAccessorRegistry = dataAccessorRegistry;
    }

    public LinkExtractorRegistry getLinkExtractorRegistry() {
        return linkExtractorRegistry;
    }

    public void setLinkExtractorRegistry(LinkExtractorRegistry linkExtractorRegistry) {
        this.linkExtractorRegistry = linkExtractorRegistry;
    }

    public MimeTypeIdentifier getMimeTypeIdentifier() {
        return mimeTypeIdentifier;
    }

    public void setMimeTypeIdentifier(MimeTypeIdentifier mimeTypeIdentifier) {
        this.mimeTypeIdentifier = mimeTypeIdentifier;
    }

    public Set getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public Crawler getCrawler(DataSource source) {
        WebCrawler crawler = new WebCrawler();

        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(dataAccessorRegistry);
        crawler.setMimeTypeIdentifier(mimeTypeIdentifier);
        crawler.setLinkExtractorRegistry(linkExtractorRegistry);

        return crawler;
    }
}
