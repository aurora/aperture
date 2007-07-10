/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.outlook.OutlookCrawler;
import org.semanticdesktop.aperture.outlook.OutlookDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;


public class ExampleOutlookCrawler {
    
    /**
     * Pass a ROOT_URL that will be the prefix
     */
    public static final String ROOT_URL_OPTION = "-rooturl";
    
    
//  create a data source configuration
    RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
    RDFContainer configuration = factory.newInstance("source:testSource");

    private File outputFile;

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    public static void main(String[] args) throws ModelException {
        // create a new ExampleFileCrawler instance
        ExampleOutlookCrawler crawler = new ExampleOutlookCrawler();
        
        String rootUrl = "semdesk:outlook:";

        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (ROOT_URL_OPTION.equals(arg)) {
                if (i == args.length-1)
                    exitWithUsageMessage();
                i++;
                rootUrl = args[i];
            }
            if (crawler.getOutputFile() == null) {
                crawler.setOutputFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getOutputFile() == null) {
            exitWithUsageMessage();
        }
        
        // set config
        ConfigurationUtil.setRootUrl(rootUrl, crawler.configuration);

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleOutlookCrawler.class.getName() + " [-rooturl uriprefix] outputFile");
        System.err.println(" -rooturl: define the prefix used for outlook resource URIs.");
        System.exit(-1);
    }

    public void crawl() throws ModelException {
        // create the data source
        OutlookDataSource source = new OutlookDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        OutlookCrawler crawler = new OutlookCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new SimpleCrawlerHandler(false,false,false,outputFile));

        // start crawling
        crawler.crawl();
    }

}

