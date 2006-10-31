/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
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
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


public class ThunderbirdCrawlerTest extends TestCase implements CrawlerHandler, RDFContainerFactory {

	private static String data="/org/semanticdesktop/aperture/docs/thunderbird-addressbook.mab";
	private Repository repository;
	private int objects;
	
	private String makeFileFromResource(String path) throws IOException {
		File f=File.createTempFile("thunderbirdTest",".mab");
		f.deleteOnExit();
		
		FileOutputStream fos=new FileOutputStream(f);
		InputStream is=getClass().getResourceAsStream(path);
		
		//java sucks
		byte[] buffer = new byte[512];
		int read;
		while ((read=is.read(buffer)) >0) {
		   fos.write(buffer, 0, read);
		}
		fos.flush();
		fos.close();
		
		return f.getAbsolutePath();
	}
	
	public void testThunderbird() throws Exception { 
		
		DataSource ds=new AddressbookDataSource();
		
		ds.setConfiguration(new SesameRDFContainer("urn:TestThunderBirdDataSource"));
		ConfigurationUtil.setBasepath(makeFileFromResource(data),ds.getConfiguration());
		ds.getConfiguration().put(DATASOURCE.flavour,"thunderbird");
		
		CrawlerFactory cf=new AddressbookCrawlerFactory();
		
		Crawler c=cf.getCrawler(ds);
		
		c.setAccessData(new AccessDataImpl());
		c.setCrawlerHandler(this);
		
		repository=new Repository(new MemoryStore());
		repository.initialize();
		
		c.crawl();

		assertEquals(objects,179);
		
		//repository.export(new N3Writer(System.out));
		
		//test serialisation and parsing
		StringWriter xml=new StringWriter();
		repository.export(new RDFXMLWriter(xml));

		File tmpfile=File.createTempFile("abook",".rdfxml");
		
		repository.export(new RDFXMLWriter(new FileOutputStream(tmpfile)));
		
		Repository rep2=new Repository(new MemoryStore());
		rep2.initialize();
		rep2.add(tmpfile,"",RDFFormat.RDFXML);

		//tmpfile.deleteOnExit();

	}

	public RDFContainer getRDFContainer(URI uri) {
		return new SesameRDFContainer(repository,uri);
	}

	public void crawlStarted(Crawler crawler) {
		// TODO Auto-generated method stub
		
	}

	public void crawlStopped(Crawler crawler, ExitCode exitCode) {
		// TODO Auto-generated method stub
		
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

