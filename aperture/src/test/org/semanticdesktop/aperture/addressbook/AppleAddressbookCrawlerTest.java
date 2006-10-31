/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import junit.framework.TestCase;

import org.openrdf.model.URI;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/** 
 * It's nigh on impossible to test this without actually running on a mac and running addressbook. 
 * Also, WHEN running on a mac, there is little control of what data exists.. 
 * There is therefore no real tests here, i.e. it accepts any data as long as no exceptions are thrown.
 * 
 * @author grimnes
 * $Id$
 */
public class AppleAddressbookCrawlerTest extends TestCase implements CrawlerHandler, RDFContainerFactory {
	private Repository repository;
	int objects;
	private ExitCode code;
	
	public void testCrawl() throws Exception { 
		DataSource ds=new AddressbookDataSource();
		
		ds.setConfiguration(new SesameRDFContainer("urn:TestTAddressbookDataSource"));
		ds.getConfiguration().put(DATASOURCE.flavour,AppleAddressbookCrawler.TYPE);
		
		CrawlerFactory cf=new AddressbookCrawlerFactory();
		
		Crawler c=cf.getCrawler(ds);
		
		c.setAccessData(new AccessDataImpl());
		c.setCrawlerHandler(this);
		
		repository=new Repository(new MemoryStore());
		repository.initialize();
		
		System.err.println("Crawling addressbook... ");
		c.crawl();
		assertEquals("Crawling must have succeeded.",code,ExitCode.COMPLETED);
		
		System.err.println("Objects crawler: "+objects);
		repository.export(new RDFXMLWriter(System.err));
	}
	
	public RDFContainer getRDFContainer(URI uri) {
		return new SesameRDFContainer(repository,uri);
	}
	
	public void crawlStarted(Crawler crawler) {
		// TODO Auto-generated method stub
		
	}
	
	public void crawlStopped(Crawler crawler, ExitCode exitCode) {
		code=exitCode;		
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

