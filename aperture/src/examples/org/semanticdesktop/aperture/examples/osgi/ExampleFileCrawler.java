/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.osgi;

import java.io.File;
import java.util.Iterator;

import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * Example class that crawls a file system and puts all extracted metadata in a RDF file.
 */
public class ExampleFileCrawler {

    public static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

    public static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    public static final String VERBOSE_OPTION = "-verbose";

    private File rootFile;

    private boolean identifyingMimeType = false;

    private boolean extractingContents = false;

    private boolean verbose = false;

    private CrawlerRegistry crawlerRegistry;

    private DataAccessorRegistry accessorRegistry;

    private ExtractorRegistry extractorRegistry;

    private MimeTypeIdentifierRegistry mimeIdentifierRegistry;

    private DataSourceRegistry dataSourceRegistry;

    public ExampleFileCrawler(CrawlerRegistry crawlerRegistry, DataAccessorRegistry accessorRegistry,
            ExtractorRegistry extractorRegistry, MimeTypeIdentifierRegistry mimeIdentifierRegistry,
            DataSourceRegistry dataSourceRegistry) {
        this.accessorRegistry = accessorRegistry;
        this.crawlerRegistry = crawlerRegistry;
        this.extractorRegistry = extractorRegistry;
        this.mimeIdentifierRegistry = mimeIdentifierRegistry;
        this.dataSourceRegistry = dataSourceRegistry;
    }

    public boolean isExtractingContents() {
        return extractingContents;
    }

    public boolean isIdentifyingMimeType() {
        return identifyingMimeType;
    }

    public File getRootFile() {
        return rootFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

    public void setIdentifyingMimeType(boolean identifyingMimeType) {
        this.identifyingMimeType = identifyingMimeType;
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void crawl(String rootDir) {
        System.out.println("Trying to crawl the dir: " + rootDir);
        System.out.println("RDF will be printed to the standard output");
        setRootFile(new File(rootDir));

        if (rootFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testsource");
        ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);
        ConfigurationUtil.setMaximumDepth(1, configuration);

        // create the data source
        DataSourceFactory sourceFactory = (DataSourceFactory) dataSourceRegistry.get(
            DATASOURCE.FileSystemDataSource).iterator().next();
        DataSource source = sourceFactory.newInstance();
        source.setConfiguration(configuration);

        CrawlerHandler handler = null;

        Iterator it = mimeIdentifierRegistry.getAll().iterator();
        MimeTypeIdentifierFactory mimeIdentifierFactory = (MimeTypeIdentifierFactory) it.next();
        MimeTypeIdentifier mimeIdentifier = mimeIdentifierFactory.get();

        handler = new SimpleCrawlerHandler(mimeIdentifier, extractorRegistry);

        // setup a crawler that can handle this type of DataSource
        it = crawlerRegistry.get(DATASOURCE.FileSystemDataSource).iterator();
        Crawler crawler = ((CrawlerFactory) it.next()).getCrawler(source);
        crawler.setDataAccessorRegistry(accessorRegistry);
        crawler.setCrawlerHandler(handler);

        // start crawling
        crawler.crawl();
    }
}
