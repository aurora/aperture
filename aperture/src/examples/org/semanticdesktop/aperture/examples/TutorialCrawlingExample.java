/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.io.PrintWriter;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;


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
        ModelFactory factory = RDF2Go.getModelFactory();
        Model model = factory.createModel();
        RDFContainer configuration = new RDF2GoRDFContainer(model, URIImpl.create("source:testSource"), false);
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
		
		Model sharedModel;
		
		public TutorialCrawlerHandler() throws Exception {
			sharedModel = new RepositoryModel(false);
		}
		// let's dump the contents onto the standard output	
		public void crawlStopped(Crawler crawler, ExitCode exitCode) {
			try {
				sharedModel.writeTo(new PrintWriter(System.out), Syntax.Trix );
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			sharedModel.close();
		}

		public RDFContainer getRDFContainer(URI uri) {
			Model newModel = null;
			try {
				newModel = new RepositoryModel(false);
			} catch (ModelException me) {
				me.printStackTrace();
				throw new RuntimeException(me);
			}
			return new RDF2GoRDFContainer(newModel,uri);
		}
		
		public void objectChanged(Crawler crawler, DataObject object) {
			processBinary(object);
			try {
				
				ClosableIterable<? extends Statement> iterable 
						= object.getMetadata().getModel().findStatements(Variable.ANY, Variable.ANY, Variable.ANY);
				ClosableIterator<? extends Statement> iterator = iterable.iterator();
				sharedModel.addAll(iterator);
				iterator.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			object.dispose();
		}
		
		public void objectNew(Crawler crawler, DataObject object) {
			processBinary(object);
			try {
				ClosableIterable<? extends Statement> iterable 
						= object.getMetadata().getModel().findStatements(Variable.ANY, Variable.ANY, Variable.ANY);
				ClosableIterator<? extends Statement> iterator = iterable.iterator();
				sharedModel.addAll(iterator);
				iterator.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			object.dispose();
		}
	}
}

