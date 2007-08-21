/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;

/**
 * Example class that crawls a file system and stores all extracted metadata in a RDF file.
 * @author fluit, sauermann, klinkigt
 */
public class ExampleFileCrawler extends AbstractExampleCrawler {

    private File rootFile;
    
    public void crawl() throws ModelException {
        if (rootFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testsource");

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);

        source.setRootFolder(rootFile.getAbsolutePath());
        
        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }
    
    public File getRootFile() {
        return rootFile;
    }
    
    /**
     * The main method
     * @param args command line arguments
     * @throws ModelException
     */
    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleFileCrawler crawler = new ExampleFileCrawler();

        // parse the command line options
        
        List<String> remaining = crawler.processCommonOptions(args);
        
        for (String arg : remaining) {
            if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                crawler.exitWithUsageMessage();
            } else if (crawler.getRootFile() == null) {
                crawler.setRootFile(new File(arg));
            }
            else {
                crawler.exitWithUsageMessage();
            }
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }
    
    @Override
    protected String getSpecificExplanationPart() {
        return "  <root-folder>  - the directory to start crawling";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "<root-folder>";
        
    }
}
