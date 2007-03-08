/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

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

    public static URI TESTID = URIImpl.createURIWithoutChecking("urn:test:outlookdatasource");

    public static String TESTROOT = "test:local:outlook:";

    OutlookDataSource olds;

    OutlookCrawler crawler;

    protected void setUp() throws Exception {
        olds = new OutlookDataSource();
        RDFContainer config = createRDFContainer(TESTID);
        ConfigurationUtil.setRootUrl(TESTROOT, config);
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
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);

        crawler.crawl();

        // dump the ModelSet
        ModelSet modelSet = crawlerHandler.getModelSet();
        dump(modelSet);
        modelSet.close();
    }

    private void dump(ModelSet modelSet) {
        try {
            modelSet.writeTo(System.out, Syntax.Ntriples);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ModelException me) {
            me.printStackTrace();
        }
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private ModelSet modelSet;

        int newCount;

        private RDFContainer lastContainer;

        public SimpleCrawlerHandler() throws ModelException {
            ModelFactory factory = RDF2Go.getModelFactory();
            modelSet = factory.createModelSet();

            newCount = 0;
        }

        public ModelSet getModelSet() {
            return modelSet;
        }

        public void crawlStarted(Crawler crawler) {
        // no-op
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            assertEquals(ExitCode.COMPLETED, exitCode);
        }

        public void accessingObject(Crawler crawler, String url) {
        // no-op
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            Model model = modelSet.getModel(uri);
            RDFContainer container = new RDFContainerImpl(model, uri);
            lastContainer = container;
            return container;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            newCount++;

            assertNotNull(object);
            assertSame(lastContainer, object.getMetadata());

            object.dispose();
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            object.dispose();
            fail();
        }

        public void objectNotModified(Crawler crawler, String url) {
            fail();
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            fail();
        }

        public void clearStarted(Crawler crawler) {
            fail();
        }

        public void clearingObject(Crawler crawler, String url) {
            fail();
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            fail();
        }
    }

    // private class UpdatingCrawlerHandler extends SimpleCrawlerHandler
    // {
    // int changedCount = 0;
    // int notModifiedCount = 0;
    // int removedCount = 0;
    // int cleared = 0;
    //    	
    // public UpdatingCrawlerHandler() throws ModelException {
    //
    // }
    //    	
    // public void objectChanged(Crawler dataCrawler, DataObject object) {
    // changedCount++;
    // object.dispose();
    // }
    //
    // public void objectNotModified(Crawler crawler, String url) {
    // notModifiedCount++;
    // }
    //
    // public void objectRemoved(Crawler dataCrawler, String url) {
    // removedCount++;
    // }
    //
    // public void clearStarted(Crawler crawler) {
    // // no-op
    // }
    //
    // public void clearingObject(Crawler crawler, String url) {
    // cleared++;
    // }
    //
    // public void clearFinished(Crawler crawler, ExitCode exitCode) {
    // // no-op
    // }
    //    	
    // }

}
