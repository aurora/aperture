/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.apple;

import java.io.PrintWriter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * It's nigh on impossible to test this without actually running on a mac and running addressbook. Also, WHEN
 * running on a mac, there is little control of what data exists.. There is therefore no real tests here, i.e.
 * it accepts any data as long as no exceptions are thrown.
 */
public class AppleAddressbookCrawlerTest extends ApertureTestBase implements CrawlerHandler,
        RDFContainerFactory {

    private Model model;

    int objects;

    private ExitCode code;

    public void testCrawl() throws Exception {
        DataSource ds = new AppleAddressbookDataSource();

        ds.setConfiguration(createRDFContainer("urn:TestTAddressbookDataSource"));
        // Removed by Antoni Mylka on 15.01.2007 - after the refactoring we don't need this anymore
        // ds.getConfiguration().put(DATASOURCE.flavour,AppleAddressbookCrawler.TYPE);

        CrawlerFactory cf = new AppleAddressbookCrawlerFactory();

        Crawler c = cf.getCrawler(ds);

        c.setAccessData(new AccessDataImpl());
        c.setCrawlerHandler(this);

        model = createModel();

        System.err.println("Crawling addressbook... ");
        c.crawl();
        assertEquals("Crawling must have succeeded.", code, ExitCode.COMPLETED);

        System.err.println("Objects crawler: " + objects);
        model.writeTo(new PrintWriter(System.out), Syntax.RdfXml);

        model.close();
    }

    public RDFContainer getRDFContainer(URI uri) {
        return createRDFContainer(uri);
    }

    public void crawlStarted(Crawler crawler) {
    // TODO Auto-generated method stub

    }

    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
        code = exitCode;
    }

    public void accessingObject(Crawler crawler, String url) {
    // TODO Auto-generated method stub

    }

    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return this;
    }

    public void objectNew(Crawler crawler, DataObject object) {
        objects++;
        object.dispose();

    }

    public void objectChanged(Crawler crawler, DataObject object) {
        object.dispose();
    }

    public void objectNotModified(Crawler crawler, String url) {
    // TODO Auto-generated method stub

    }

    public void objectRemoved(Crawler crawler, String url) {
    // TODO Auto-generated method stub

    }

    public void clearStarted(Crawler crawler) {
    // TODO Auto-generated method stub

    }

    public void clearingObject(Crawler crawler, String url) {
    // TODO Auto-generated method stub

    }

    public void clearFinished(Crawler crawler, ExitCode exitCode) {
    // TODO Auto-generated method stub

    }
}
