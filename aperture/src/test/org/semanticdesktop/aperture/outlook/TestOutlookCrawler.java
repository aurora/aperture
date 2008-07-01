/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.RDFTool;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * Crawl through the locally installed outlook.
 * 
 * Note that this test is pretty useless when you are not running Outlook and have not included the testdata
 * file.
 * 
 * 
 * @author sauermann $Id$
 */
public class TestOutlookCrawler extends ApertureTestBase {

    public static URI TESTID = new URIImpl("urn:test:outlookdatasource",false);

    public static String TESTROOT = "outlook:test:local";

    public static String TESTAPPOINTMENTURI = TESTROOT + OutlookResource.Appointment.ITEMTYPE
            + "/00000000B2CDC30BFF2EED4ABA9C61436A07FE3384002000";

    public static String TESTTASKURI = TESTROOT + OutlookResource.Task.ITEMTYPE
            + "/00000000B2CDC30BFF2EED4ABA9C61436A07FE33C4002000";

    OutlookDataSource olds;

    OutlookCrawler crawler;

    protected void setUp() throws Exception {
        olds = new OutlookDataSource();
        RDFContainer config = createRDFContainer(TESTID);
        olds.setConfiguration(config);
        olds.setRootUrl(TESTROOT);

        // exclude leo's normal outlook file
        DomainBoundaries bound = new DomainBoundaries();
        // 
        // this is the test: folder/00000000B2CDC30BFF2EED4ABA9C61436A07FE3322800000
        // exclude this: folder/00000000ECD4B99358B9814B9DAFE2255CD8AE9A22800000
        bound.addExcludePattern(new SubstringPattern(TESTROOT
                + "folder/00000000ECD4B99358B9814B9DAFE2255CD8AE9A22800000", SubstringCondition.STARTS_WITH));
        ConfigurationUtil.setDomainBoundaries(bound, config);
        olds.setConfiguration(config);

        // create a Crawler for this DataSource (hardcoded for now)
        crawler = new OutlookCrawler();

        crawler.setDataSource(olds);

    }

    protected void tearDown() throws Exception {
        olds.getConfiguration().getModel().close();
        olds = null;
        crawler.getDataSource().getConfiguration().getModel().close();
        crawler = null;
    }
    
    public ModelSet crawlData() throws Exception {
        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);

        crawler.crawl();

        return crawlerHandler.getModelSet();
    }

    public void testCrawl() throws Exception {
        // dump the ModelSet
        ModelSet modelSet = crawlData();
        dump(modelSet);
        modelSet.close();
    }
    
    public void testIds() throws Exception {
        ModelSet modelSet = crawlData();
        // check the ids
        String id = RDFTool.getSingleValueString(modelSet, modelSet.createURI(TESTAPPOINTMENTURI), NAO.identifier);
        System.out.println(id);
        for (ClosableIterator<Statement> i = modelSet.findStatements(Variable.ANY, modelSet.createURI(TESTAPPOINTMENTURI), Variable.ANY, Variable.ANY); i.hasNext();)
            System.out.println("stx:"+i.next());
        assertTrue(modelSet.containsStatements(Variable.ANY, 
            modelSet.createURI(TESTAPPOINTMENTURI), NAO.identifier, 
            modelSet.createPlainLiteral(
            OutlookCrawler.ITEMID_IDENTIFIERPREFIX+"00000000B2CDC30BFF2EED4ABA9C61436A07FE3384002000")));

        assertTrue(modelSet.containsStatements(Variable.ANY, 
            modelSet.createURI(TESTTASKURI), NAO.identifier, 
            modelSet.createPlainLiteral(
                OutlookCrawler.ITEMID_IDENTIFIERPREFIX+"00000000B2CDC30BFF2EED4ABA9C61436A07FE33C4002000")));
        
        
    }

    public void testCrawlWithDataAccess() throws Exception {

        // setup a CrawlerHandler
        UpdatingCrawlerHandler crawlerHandler = new UpdatingCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);

        AccessDataImpl access = new AccessDataImpl();

        crawler.setAccessData(access);

        crawler.crawl();

        // dump the repo
        System.out.println("fist run: ");
        ModelSet modelSet = crawlerHandler.getModelSet();
        dump(modelSet);
        modelSet.close();
        
        // second run
        crawlerHandler = new UpdatingCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);
        crawler.crawl();
        assertEquals("changed", 7, crawlerHandler.changedCount);
        assertEquals("not modified", 7, crawlerHandler.notModifiedCount);
        assertEquals("removed", 0, crawlerHandler.removedCount);
        assertEquals("new objects", 0, crawlerHandler.newCount);
        crawlerHandler.getModelSet().close();
    }

    public void testAccessor() throws UrlNotFoundException, IOException {
        OutlookAccessor access = new OutlookAccessor();
        DataObject o = access.getDataObject(TESTAPPOINTMENTURI, olds, null, new RDFContainerFactoryImpl());
        RDFContainer rdf = o.getMetadata();
        String s = rdf.getString(NIE.title);
        assertEquals("title wrong", "Test Termin", s);
        o.dispose();
        rdf.getModel().close();
    }

    public void testAccessorMoreAccess() throws UrlNotFoundException, IOException {
        OutlookAccessor access = new OutlookAccessor();
        {
            DataObject o = access
                    .getDataObject(TESTAPPOINTMENTURI, olds, null, new RDFContainerFactoryImpl());
            RDFContainer rdf = o.getMetadata();
            String s = rdf.getString(NIE.title);
            assertEquals("title wrong", "Test Termin", s);
            o.dispose();
            rdf.getModel().close();
        }
        {
            DataObject o = access.getDataObject(TESTTASKURI, olds, null, new RDFContainerFactoryImpl());
            RDFContainer rdf = o.getMetadata();
            String s = rdf.getString(NIE.title);
            assertEquals("title wrong", "Test this stuff now", s);
            o.dispose();
            rdf.getModel().close();
        }
        {
            DataObject o = access
                    .getDataObject(TESTAPPOINTMENTURI, olds, null, new RDFContainerFactoryImpl());
            RDFContainer rdf = o.getMetadata();
            String s = rdf.getString(NIE.title);
            assertEquals("title wrong", "Test Termin", s);
            o.dispose();
            rdf.getModel().close();
        }
    }

    private void dump(ModelSet modelSet) {
        try {
            modelSet.writeTo(System.out, Syntax.Trig);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ModelRuntimeException me) {
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
            modelSet.open();

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
            RDFContainer result = new RDFContainerImpl(model, uri);
            lastContainer = result;
            return result;
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

    private class UpdatingCrawlerHandler extends SimpleCrawlerHandler {

        int changedCount = 0;

        int notModifiedCount = 0;

        int removedCount = 0;

        int cleared = 0;

        public UpdatingCrawlerHandler() throws ModelException {

        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            changedCount++;
            object.dispose();
        }

        public void objectNotModified(Crawler crawler, String url) {
            notModifiedCount++;
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            removedCount++;
        }

        public void clearStarted(Crawler crawler) {
        // no-op
        }

        public void clearingObject(Crawler crawler, String url) {
            cleared++;
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
        // no-op
        }

    }

}
