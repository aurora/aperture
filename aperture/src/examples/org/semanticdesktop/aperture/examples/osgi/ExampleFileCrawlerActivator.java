/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierRegistry;

public class ExampleFileCrawlerActivator implements BundleActivator {

    public static final String DIRECTORY = System.getProperty("user.home");
    
    private CrawlerRegistry crawlerRegistry;

    private DataAccessorRegistry dataAccessorRegistry;

    private ExtractorRegistry extractorRegistry;

    private MimeTypeIdentifierRegistry mimeTypeIdentifierRegistry;

    private DataSourceRegistry dataSourceRegistry;

    public void start(BundleContext context) throws Exception {
        ServiceReference crawlerReference = context.getServiceReference(CrawlerRegistry.class.getName());
        ServiceReference accessorReference = context
                .getServiceReference(DataAccessorRegistry.class.getName());
        ServiceReference extractorReference = context.getServiceReference(ExtractorRegistry.class.getName());
        ServiceReference mimeIdentifierReference = context
                .getServiceReference(MimeTypeIdentifierRegistry.class.getName());
        ServiceReference dataSourceReference = context
                .getServiceReference(DataSourceRegistry.class.getName());

        if (crawlerReference != null && accessorReference != null && extractorReference != null
                && mimeIdentifierReference != null && dataSourceReference != null) {
            System.out.println("All registries successfully found");
        }

        crawlerRegistry = (CrawlerRegistry) context.getService(crawlerReference);
        dataAccessorRegistry = (DataAccessorRegistry) context.getService(accessorReference);
        extractorRegistry = (ExtractorRegistry) context.getService(extractorReference);
        mimeTypeIdentifierRegistry = (MimeTypeIdentifierRegistry) context.getService(mimeIdentifierReference);
        dataSourceRegistry = (DataSourceRegistry) context.getService(dataSourceReference);

        doCrawl();
    }

    public void stop(BundleContext context) throws Exception {}

    private void doCrawl() {
        // create a new ExampleFileCrawler instance
        ExampleFileCrawler exCrawler = new ExampleFileCrawler(crawlerRegistry, dataAccessorRegistry,
                extractorRegistry, mimeTypeIdentifierRegistry, dataSourceRegistry);
        exCrawler.setIdentifyingMimeType(true);
        exCrawler.setExtractingContents(true);
        exCrawler.setVerbose(true);
        exCrawler.crawl(DIRECTORY);
    }
}
