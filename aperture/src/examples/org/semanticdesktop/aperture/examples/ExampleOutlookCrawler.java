/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.util.Iterator;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.outlook.OutlookCrawler;
import org.semanticdesktop.aperture.outlook.OutlookDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;


public class ExampleOutlookCrawler extends AbstractExampleCrawler {
    
    /**
     * Pass a ROOT_URL that will be the prefix
     */
    public static final String ROOT_URL_OPTION = "-rooturl";
    
    OutlookDataSource source = new OutlookDataSource();
    
    public ExampleOutlookCrawler() {
        // create the data source
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");
        source = new OutlookDataSource();
        source.setConfiguration(configuration);
    }
    
    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        ExampleOutlookCrawler crawler = new ExampleOutlookCrawler();
        
        List<String> remainingOptions = crawler.processCommonOptions(args);
        
        String rootUrl = "semdesk:outlook:";

        // parse the command line options
        Iterator<String> iterator = remainingOptions.iterator();
        if (iterator.hasNext()) {
            String arg = iterator.next();
            if (ROOT_URL_OPTION.equals(arg)) {
                if (!iterator.hasNext())
                    crawler.exitWithUsageMessage();
                rootUrl = iterator.next();
            }
            else {
                crawler.exitWithUsageMessage();
            }
        }
        if (rootUrl != null) {
            crawler.source.setRootUrl(rootUrl);
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    public void crawl() throws ModelException {
        // setup a crawler that can handle this type of DataSource
        OutlookCrawler crawler = new OutlookCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        
        // start crawling
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        return "  -rooturl: define the prefix used for outlook resource URIs. Should begin with 'outlook:' (optional)";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "[-rooturl uriprefix]";
    }
}

