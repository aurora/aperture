/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.util.LinkedList;
import java.util.List;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.TestIncrementalCrawlerHandler;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

/**
 * A test suite that tests the basic functionality provided by the abstract AddresbookCrawler.
 */
public class AbstractAddressbookCrawlerTest extends ApertureTestBase {

    /**
     * Tests a situation when the crawler is stopped inside the {@link AddressbookCrawler#crawlAddressbook()}
     * method. The expected behavior is that no object is to be returned to the crawler handler, all generated
     * objects must be disposed properly. The exit code must be STOP_REQUESTED. This is supposed to work
     * without any AccessData instance
     */
    public void testStopWhileCrawlingAddressbook() {
        final EndlessAddressbookCrawler crawler = new EndlessAddressbookCrawler();
        TestIncrementalCrawlerHandler handler = new TestIncrementalCrawlerHandler(null,null,true);
        crawler.setCrawlerHandler(handler);
        crawler.setDataSource(getDummyDataSource());
        Thread thread = new Thread() { public void run() { crawler.crawl(); } }; 
        thread.start(); // crawl in a separate thread
        safelySleep(300); // wait a little, allow the crawler to crawl some objects into the result list
        crawler.stop(); // stop the crawler
        safelyJoin(thread,500); // wait until the crawler actually stops
        assertNewModUnmodDel(handler, 0, 0, 0, 0); // nothing should have reached the handler
        // right now there should be about 9 or 10 objects in the result list of the dummy addressbook crawler
        // all must be properly disposed
        for (DataObject object : crawler.result) {
            assertFalse(object.getMetadata().getModel().isOpen());
        }
        // the exit code of the crawler is supposed to be STOP_REQUESTED
        assertEquals(ExitCode.STOP_REQUESTED, crawler.getCrawlReport().getExitCode());
        crawler.getDataSource().getConfiguration().dispose(); // dispose the dummy data source configuration
    }

    /**
     * Tests a situation where the crawler is stopped outside the crawlAddressbook method. The desired behavior
     * is: all data objects that managed to reach the handler are OK. All the data objects that were obtained
     * from the crawlAddressbook method, but haven't reached the handler must be properly disposed by the
     * crawler itself, before it returns from the crawl() method. This is supposed to work without any
     * AccessData instance.
     */
    public void testStopWhileReturningDataObjects() {
        final TenObjectsAddressbookCrawler crawler = new TenObjectsAddressbookCrawler();
        VerySlowCrawlerHandler handler = new VerySlowCrawlerHandler();
        crawler.setCrawlerHandler(handler);
        crawler.setDataSource(getDummyDataSource());
        Thread thread = new Thread() { public void run() { crawler.crawl(); } };
        thread.start(); // crawl in a separate thread
        safelySleep(250); // wait a little, the processing of 10 data objects in the crawler handler will take
        // 50 miliseconds, by waiting 250 ms, we'll stop the crawler exactly in the middle of processing
        crawler.stop(); // stop the crawler
        safelyJoin(thread,500); // wait until the crawler actually stops
        assertTrue(handler.getNewObjects().size() > 2); // something should have reached the handler
        // two is a conservative estimate, it should be about 4,5 or 6
        // all data objects that reached the handler will be disposed by the handler methods, the objects that
        // remained within the crawler must be disposed by the crawler itself, we test it by ensuring that
        // the entire result list is properly disposed
        for (DataObject object : crawler.result) {
            assertFalse(object.getMetadata().getModel().isOpen());
        }
        // the exit code of the crawler is supposed to be STOP_REQUESTED
        assertEquals(ExitCode.STOP_REQUESTED, crawler.getCrawlReport().getExitCode());
        crawler.getDataSource().getConfiguration().dispose(); // dispose the dummy data source configuration
    }
    
    /**
     * This class simulates an address book crawler that works with a very big address book, the
     * crawlAddressbook() method is endless. It only stops when a stop has been requested.
     */
    private class EndlessAddressbookCrawler extends AddressbookCrawler {
        List<DataObject> result = new LinkedList<DataObject>();
        @SuppressWarnings("unchecked")
        public List crawlAddressbook() throws Exception {
            int counter = 1;
            while (!isStopRequested()) {
                URI uri = new URIImpl("uri:testobject:" + (counter++));
                RDFContainer container = getRDFContainerFactory(uri.toString()).getRDFContainer(uri);
                DataObject object = new DataObjectBase(uri,null,container);
                result.add(object);
                safelySleep(30);
            }
            return result;
        }

        public URI getContactListUri() {
            return new URIImpl("uri:contactlist");
        }
    }
    
    /**
     * This class simulates an address book crawler that yields 10 data objects quickly.
     */
    private class TenObjectsAddressbookCrawler extends AddressbookCrawler {
        List<DataObject> result = new LinkedList<DataObject>();
        @SuppressWarnings("unchecked")
        public List crawlAddressbook() throws Exception {
            for (int i = 0; i < 10; i++) {
                URI uri = new URIImpl("uri:testobject:" + i);
                RDFContainer container = getRDFContainerFactory(uri.toString()).getRDFContainer(uri);
                DataObject object = new DataObjectBase(uri,null,container);
                result.add(object);
            }
            return result;
        }

        public URI getContactListUri() {
            return new URIImpl("uri:contactlist");
        }
    }
    
    /**
     * This class simulates an application that does some heavy processing of the data objects returned by the
     * crawler. That's why we wait for 50 ms after each data object is processed.
     */
    private class VerySlowCrawlerHandler extends TestIncrementalCrawlerHandler {
        public VerySlowCrawlerHandler() {super(null,null,true);} // we want separate models for each container
        public void objectNew(Crawler crawler, DataObject object) {
            super.objectNew(crawler, object);
            safelySleep(50);
        }
    }
    
    private DataSource getDummyDataSource() {
        URI id = new URIImpl("uri:datasource");
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        return new DataSourceBase(new RDFContainerImpl(model,id)) {
            public URI getType() {
                return new URIImpl("uri:type");
            }
        };
    }
}
