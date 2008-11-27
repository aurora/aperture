/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * crawl through the locally installed outlook.
 * 
 * Note that this test is pretty useless when you are not running Outlook and have not included the testdata
 * file.
 * 
 * Also we have not found a way to include one branch of outlook easily, have to think about that.
 * 
 * @author sauermann $Id$
 */
public class TestOutlookCrawlAll extends ApertureTestBase {

    public static URI TESTID = new URIImpl("urn:test:outlookdatasource");

    public static String TESTROOT = "test:local:outlook:";

    OutlookDataSource olds;

    OutlookCrawler crawler;

    protected void setUp() throws Exception {
        olds = new OutlookDataSource();
        RDFContainer config = createRDFContainer(TESTID);
        olds.setConfiguration(config);
        olds.setRootUrl(TESTROOT);
        olds.setConfiguration(config);

        // create a Crawler for this DataSource (hardcoded for now)
        crawler = new OutlookCrawler();

        crawler.setDataSource(olds);

    }

    protected void tearDown() throws Exception {
        olds.dispose();
        olds = null;
        crawler = null;
    }

    public void testCrawl() throws Exception {
        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = 
            new SimpleCrawlerHandler(true, true, true, null);
        crawler.setCrawlerHandler(crawlerHandler);

        crawler.crawl();

        // dump the ModelSet
        ModelSet modelSet = crawlerHandler.getModelSet();
        dump(modelSet);
        modelSet.close();
    }

    private void dump(ModelSet modelSet) {
        try {
            modelSet.open();
            modelSet.writeTo(System.out, Syntax.Ntriples);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ModelRuntimeException me) {
            me.printStackTrace();
        }
    }

}
