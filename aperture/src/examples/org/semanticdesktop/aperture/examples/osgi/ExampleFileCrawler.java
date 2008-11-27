/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.osgi;

import java.io.File;
import java.util.Iterator;

import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.datasource.filesystem.FILESYSTEMDS;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;

public class ExampleFileCrawler {

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
        

        // create the data source
        DataSourceFactory sourceFactory = (DataSourceFactory) dataSourceRegistry.get(
            FILESYSTEMDS.FileSystemDataSource).iterator().next();
        FileSystemDataSource source = (FileSystemDataSource)sourceFactory.newInstance();
        source.setConfiguration(configuration);
        
        source.setRootFolder(rootFile.getAbsolutePath());
        source.setMaximumDepth(1);

        CrawlerHandler handler = null;

        Iterator it = mimeIdentifierRegistry.getAll().iterator();
        MimeTypeIdentifierFactory mimeIdentifierFactory = (MimeTypeIdentifierFactory) it.next();
        MimeTypeIdentifier mimeIdentifier = mimeIdentifierFactory.get();

        handler = new SimpleCrawlerHandler(mimeIdentifier, extractorRegistry);

        // setup a crawler that can handle this type of DataSource
        it = crawlerRegistry.get(FILESYSTEMDS.FileSystemDataSource).iterator();
        Crawler crawler = ((CrawlerFactory) it.next()).getCrawler(source);
        crawler.setDataAccessorRegistry(accessorRegistry);
        crawler.setCrawlerHandler(handler);

        // start crawling
        crawler.crawl();
    }
}
