/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.ical.IcalCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.websites.iphoto.IPhotoKeywordCrawler;
import org.semanticdesktop.aperture.websites.iphoto.IPhotoKeywordDataSource;

public class ExampleIPhotoCrawler extends AbstractExampleCrawler {

    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleIPhotoCrawler crawler = new ExampleIPhotoCrawler();

        List<String> remainingOptions = crawler.processCommonOptions(args);
        
        if (remainingOptions.size() > 0) {
            crawler.exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }


    public void crawl() throws ModelException {

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");

        // create the data source
        IPhotoKeywordDataSource source = new IPhotoKeywordDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        IPhotoKeywordCrawler crawler = new IPhotoKeywordCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        return "";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "";
    }
}
