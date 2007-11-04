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
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testers.DataObjectTreeModelTester;

/**
 * Example class demonstrating the usage of an IcalCrawler.
 */
public class ExampleIcalCrawler extends AbstractExampleCrawler {

    private File icalFile;

    public File getIcalFile() {
        return icalFile;
    }

    public void setIcalFile(File icalFile) {
        this.icalFile = icalFile;
    }
    
    
    /**
     * The ical crawler satisfies a more strict constraint. It produces a valid
     * DataObject tree.
     */
    @Override
    public ModelTester[] getAdditionalModelTesters() {
        return new ModelTester [] {new DataObjectTreeModelTester() };
    }

    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleIcalCrawler crawler = new ExampleIcalCrawler();
        
        List<String> remainingOptions = crawler.processCommonOptions(args);

        // parse the command line options
        for (String arg : remainingOptions) {
            if (crawler.getIcalFile() == null) {
                crawler.setIcalFile(new File(arg));
            }
            else {
                crawler.exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getIcalFile() == null) {
            crawler.exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    public void crawl() throws ModelException {
        if (icalFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");

        // create the data source
        IcalDataSource source = new IcalDataSource();
        source.setConfiguration(configuration);
        source.setRootUrl(icalFile.getAbsolutePath());

        // setup a crawler that can handle this type of DataSource
        IcalCrawler crawler = new IcalCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        return "  icalFile - the path to the ical file to be crawled";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "icalFile";
    }
}
