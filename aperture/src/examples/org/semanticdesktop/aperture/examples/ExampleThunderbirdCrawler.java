/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdAddressbookDataSource;
import org.semanticdesktop.aperture.addressbook.thunderbird.ThunderbirdCrawler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testers.DataObjectTreeModelTester;

/**
 * DON'T USE it's a copy-paste, doesn't work yet
 */
public class ExampleThunderbirdCrawler extends AbstractExampleCrawler {

    String thunderbirdAddressbookPath;
    
    
    /**
     * @return Returns the thunderbirdAddressbookPath.
     */
    public String getThunderbirdAddressbookPath() {
        return thunderbirdAddressbookPath;
    }

    
    /**
     * @param thunderbirdAddressbookPath The thunderbirdAddressbookPath to set.
     */
    public void setThunderbirdAddressbookPath(String thunderbirdAddressbookPath) {
        this.thunderbirdAddressbookPath = thunderbirdAddressbookPath;
    }
    
    /**
     * The thunderbird crawler satisfies a more strict constraint. It produces a valid
     * DataObject tree.
     */
    @Override
    public ModelTester[] getAdditionalModelTesters() {
        return new ModelTester [] {new DataObjectTreeModelTester() };
    }

    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleThunderbirdCrawler crawler = new ExampleThunderbirdCrawler();

        List<String> remainingOptions = crawler.processCommonOptions(args);
        
        if (remainingOptions.size() != 1) {
            crawler.exitWithUsageMessage();
        } else {
            crawler.setThunderbirdAddressbookPath(remainingOptions.get(0));
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    public void crawl() throws ModelException {
        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");

        // create the data source
        ThunderbirdAddressbookDataSource source = new ThunderbirdAddressbookDataSource();
        source.setConfiguration(configuration);
        source.setThunderbirdAddressbookPath(thunderbirdAddressbookPath);

        // setup a crawler that can handle this type of DataSource
        ThunderbirdCrawler crawler = new ThunderbirdCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        
        // start crawling
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        return "   inputFile - file where your thunderbird addressbook is stored";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "inputFile";
    }
}
