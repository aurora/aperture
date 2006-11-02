/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.io.PrintWriter;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainerFactory;


public class TutorialCrawlingExample {

	public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        TutorialCrawlingExample crawler = new TutorialCrawlingExample();
        
        if (args.length != 1) {
        	System.err.println("Specify the root folder");
        	System.exit(-1);
        }

        // start crawling and exit afterwards
        crawler.doCrawling(new File(args[0]));
    }	
	
	public void doCrawling(File rootFile) throws Exception {
        // create a data source configuration
        RDFContainer configuration = new SesameRDFContainer(new URIImpl("source:testSource"));
        ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);

        // create the data source
        DataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);
        
        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new TutorialCrawlerHandler());

        // start crawling
        crawler.crawl();
	}
	
	private class TutorialCrawlerHandler extends CrawlerHandlerBase {
		
		Repository repository;
		RDFContainerFactory factory;
		
		public TutorialCrawlerHandler() throws Exception {
			repository = new Repository(new MemoryStore());
			repository.initialize();
			repository.setAutoCommit(false);
			factory = new SesameRDFContainerFactory();
		}
		// let's dump the contents onto the standard output	
		public void crawlStopped(Crawler crawler, ExitCode exitCode) {
			try {
				repository.commit();
				RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, new PrintWriter(System.out));
				repository.export(rdfWriter);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public RDFContainer getRDFContainer(URI uri) {
			return factory.getRDFContainer(uri);
		}
		
		public void objectChanged(Crawler crawler, DataObject object) {
			processBinary(object);
			try {
				repository.remove(null, null, null, object.getID());
				CloseableIterator<RStatement> iterator 
						= ((Repository)object.getMetadata().getModel()).extractStatements();
				repository.add(iterator,object.getID());
				iterator.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			object.dispose();
		}
		
		public void objectNew(Crawler crawler, DataObject object) {
			processBinary(object);
			try {
				CloseableIterator<RStatement> iterator 
						= ((Repository)object.getMetadata().getModel()).extractStatements();
				repository.add(iterator,object.getID());
				iterator.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			object.dispose();
		}
	}
}

