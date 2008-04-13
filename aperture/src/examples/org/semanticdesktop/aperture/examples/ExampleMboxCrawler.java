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
import org.semanticdesktop.aperture.crawler.mbox.MboxCrawler;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;

/**
 * Example class demonstrating the usage of an MboxCrawler.
 */
public class ExampleMboxCrawler extends AbstractExampleCrawler {

    private File mboxFile;

    public File getIcalFile() {
        return mboxFile;
    }

    public void setIcalFile(File mboxFile) {
        this.mboxFile = mboxFile;
    }
    
    @Override
    public ModelTester[] getAdditionalModelTesters() {
        return new ModelTester [] { };
    }

    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleMboxCrawler crawler = new ExampleMboxCrawler();
        
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
        if (mboxFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");

        // create the data source
        MboxDataSource source = new MboxDataSource();
        source.setConfiguration(configuration);
        source.setMboxPath(mboxFile.getAbsolutePath());

        // setup a crawler that can handle this type of DataSource
        MboxCrawler crawler = new MboxCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
            
        // start crawling
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        return "  mboxFile - the path to the mbox file to be crawled";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "mboxFile";
    }
}
