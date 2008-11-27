/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.DataObjectTreeModelTester;


public class ThunderbirdCrawlerTest extends ApertureTestBase implements CrawlerHandler, RDFContainerFactory {

	private static final String URN_TEST_THUNDER_BIRD_DATA_SOURCE = "urn:TestThunderBirdDataSource";
    private static String data="/org/semanticdesktop/aperture/docs/thunderbird-addressbook.mab";
	private Model model;
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
		
		ThunderbirdAddressbookDataSource ds=new ThunderbirdAddressbookDataSource();
		
		ds.setConfiguration(createRDFContainer(URN_TEST_THUNDER_BIRD_DATA_SOURCE));
        
		//ConfigurationUtil.setBasepath(makeFileFromResource(data),ds.getConfiguration());
        ds.setThunderbirdAddressbookPath(makeFileFromResource(data));
        
        // Removed by Antoni Mylka on 15.01.2007 - after the refactoring we don't need this anymore
		// ds.getConfiguration().put(DATASOURCE.flavour,"thunderbird");
		
		CrawlerFactory cf=new ThunderbirdAddressbookCrawlerFactory();
		
		Crawler c=cf.getCrawler(ds);
		
		c.setAccessData(new AccessDataImpl());
		c.setCrawlerHandler(this);
		
        model = createModel();
		c.crawl();

		// Originally there were 179 objects, but after adding a ContactList object
		// that contains all contacts, the number rose to 180
		assertEquals(objects,180);
		
		// test serialisation and parsing
		StringWriter xml=new StringWriter();
		//model.writeTo(xml,Syntax.RdfXml);

        validate(
            model,
            true, 
            model.createURI(URN_TEST_THUNDER_BIRD_DATA_SOURCE),
            new DataObjectTreeModelTester());
		model.close();
	}

	public RDFContainer getRDFContainer(URI uri) {
		return new RDFContainerImpl(model,uri,true);
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

