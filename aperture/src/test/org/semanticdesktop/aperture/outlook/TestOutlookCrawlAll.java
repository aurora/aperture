/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.io.PrintWriter;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
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
public class TestOutlookCrawlAll extends ApertureTestBase {
	
	public static URI TESTID = URIImpl.createURIWithoutChecking("urn:test:outlookdatasource");
	public static String TESTROOT = "test:local:outlook:";

	OutlookDataSource olds;
	OutlookCrawler crawler;
	
	protected void setUp() throws Exception {
		LogUtil.setFullLogging();
		olds = new OutlookDataSource();
		RDFContainer config = createRDFContainer(TESTID);
		ConfigurationUtil.setRootUrl(TESTROOT, config);
		olds.setConfiguration(config);
		
		//		 create a Crawler for this DataSource (hardcoded for now)
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
        
        // dump the repo
        
        dumpRepo(crawlerHandler.getModel());
        crawlerHandler.getModel().close();
	}
	
	private void dumpRepo(Model model) {
		try {
			model.writeTo(new PrintWriter(System.out), Syntax.Ntriples);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ModelException me) {
			me.printStackTrace();
		}
	}

	private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private Model model;

        int newCount;

        private RDFContainer lastContainer;
        
        public SimpleCrawlerHandler() throws ModelException {
            // create a Repository
            model = new RepositoryModel(false);

            newCount = 0;
        }

        public Model getModel() {
            return model;
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
            // an rdf2go way to return a container, backed by a model, backed by a repository, which
			// actually is the private repository common to all return RDFContainers, but with a 
			// different context
			Model newModel = null;
			try {
				newModel = new RepositoryModel(uri,(Repository)model.getUnderlyingModelImplementation());
			} catch (ModelException me) {
				return null;
			}
			
			RDF2GoRDFContainer container = new RDF2GoRDFContainer(newModel, uri);

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
    
//    private class UpdatingCrawlerHandler extends SimpleCrawlerHandler
//    {
//    	int changedCount = 0;
//    	int notModifiedCount = 0;
//    	int removedCount = 0;
//    	int cleared = 0;
//    	
//    	public UpdatingCrawlerHandler() throws ModelException {
//
//        }
//    	
//        public void objectChanged(Crawler dataCrawler, DataObject object) {
//        	changedCount++;
//            object.dispose();
//        }
//
//        public void objectNotModified(Crawler crawler, String url) {
//        	notModifiedCount++;
//        }
//
//        public void objectRemoved(Crawler dataCrawler, String url) {
//            removedCount++;
//        }
//
//        public void clearStarted(Crawler crawler) {
//            // no-op
//        }
//
//        public void clearingObject(Crawler crawler, String url) {
//        	cleared++;
//        }
//
//        public void clearFinished(Crawler crawler, ExitCode exitCode) {
//            // no-op
//        }
//    	
//    }


}

