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
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookCrawler;
import org.semanticdesktop.aperture.addressbook.apple.AppleAddressbookDataSource;
import org.semanticdesktop.aperture.crawler.ical.IcalCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;

/**
 * DON'T USE it's a copy-paste, doesn't work yet
 */
public class ExampleAppleAddressbookCrawler extends AbstractExampleCrawler {

    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleAppleAddressbookCrawler crawler = new ExampleAppleAddressbookCrawler();

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
        AppleAddressbookDataSource source = new AppleAddressbookDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        AppleAddressbookCrawler crawler = new AppleAddressbookCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());

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
