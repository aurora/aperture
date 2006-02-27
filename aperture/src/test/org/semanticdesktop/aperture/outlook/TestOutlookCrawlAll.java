/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import junit.framework.TestCase;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.outlook.OutlookCrawler;
import org.semanticdesktop.aperture.outlook.OutlookDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.LogUtil;

/**
 * crawl through the locally installed outlook.
 * 
 * Note that this test is pretty useless when you are not running Outlook and have not included
 * the testdata file.
 * 
 * Also we have not found a way to include one branch of outlook easily, have to think about that.
 * 
 * @author sauermann
 * $Id$
 */
public class TestOutlookCrawlAll extends TestCase {
	
	public static URIImpl TESTID = new URIImpl("urn:test:outlookdatasource");
	public static String TESTROOT = "test:local:outlook:";

	OutlookDataSource olds;
	OutlookCrawler crawler;
	
	protected void setUp() throws Exception {
		LogUtil.setFullLogging();
		olds = new OutlookDataSource();
		olds.setID(TESTID);
		SesameRDFContainer config = new SesameRDFContainer(TESTID);
		ConfigurationUtil.setRootUrl(TESTROOT, config);
		olds.setConfiguration(config);
		
		//		 create a Crawler for this DataSource (hardcoded for now)
		crawler = new OutlookCrawler();
		
		crawler.setDataSource(olds);
		

	}

	protected void tearDown() throws Exception {
		olds = null;
		crawler = null;
	}
	
	public void testCrawl() throws Exception {
        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);
        
        crawler.crawl();
        
        // dump the repo
        
        dumpRepo(crawlerHandler.getRepository());
	}
	
	
	
    private void dumpRepo(Repository repository) throws RDFHandlerException, SailUpdateException {
    	repository.changeNamespacePrefix("http://www.gnowsis.org/ont/vcard#", "vcard");
       	repository.changeNamespacePrefix("http://www.w3.org/2002/12/cal/ical#", "ical");
    	repository.changeNamespacePrefix("http://aperture.semanticdesktop.org/ontology/data#", "data");
    	N3Writer w = new N3Writer(System.out);
    	repository.export(w);
		
	}


	private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private Repository repository;

        int newCount;

        private SesameRDFContainer lastContainer;
        
        public SimpleCrawlerHandler() {
            // create a Repository
            repository = new Repository(new MemoryStore());

            try {
                repository.initialize();
            }
            catch (SailInitializationException e) {
                // we cannot effectively continue
                throw new RuntimeException(e);
            }

            // set auto-commit off so that all additions and deletions between two commits become a
            // single transaction
            try {
                repository.setAutoCommit(false);
            }
            catch (SailUpdateException e) {
                // we could theoretically continue (although much slower), but as this is a unit test,
                // exit anyway
                throw new RuntimeException(e);
            }

            newCount = 0;
        }

        public Repository getRepository() {
            return repository;
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
            SesameRDFContainer container = new SesameRDFContainer(repository, uri);
            container.setContext(uri);
            
            lastContainer = container;
            
            return container;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
        	newCount++;

            assertNotNull(object);
            assertSame(lastContainer, object.getMetadata());

            String uri = object.getID().toString();
            
            object.dispose();
            
            try {
                repository.commit();
            }
            catch (SailUpdateException e) {
                fail();
            }
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
    
    private class UpdatingCrawlerHandler extends SimpleCrawlerHandler
    {
    	int changedCount = 0;
    	int notModifiedCount = 0;
    	int removedCount = 0;
    	int cleared = 0;
    	
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

