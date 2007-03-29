/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

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
        model.open();
        RDFContainer configuration = new RDFContainerImpl(model, URIImpl.create("source:testSource"), false);
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

    // our 'persistent' modelSet
    private ModelSet modelSet;

    public TutorialCrawlerHandler() throws ModelException {
        modelSet = RDF2Go.getModelFactory().createModelSet();
        modelSet.open();
    }

    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
        try {
            modelSet.writeTo(System.out, Syntax.Trix);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        modelSet.close();
    }

    public RDFContainer getRDFContainer(URI uri) {
        // we create a new in-memory temporary model for each data source
        Model model = RDF2Go.getModelFactory().createModel(uri);
        model.open();
        return new RDFContainerImpl(model, uri);
    }
    
    public void objectNew(Crawler crawler, DataObject object) {
        // first we try to extract the information from the binary file
        processBinary(object);
        // then we add this information to our persistent model
        modelSet.addModel(object.getMetadata().getModel());
        // don't forget to dipose of the DataObject
        object.dispose();
    }

    public void objectChanged(Crawler crawler, DataObject object) {
        // first we remove old information about the data object
        modelSet.removeModel(object.getID());
        // then we try to extract metadata and fulltext from the file
        processBinary(object);
        // an then we add the information from the temporary model to our
        // 'persistent' model
        modelSet.addModel(object.getMetadata().getModel());
        // don't forget to dispose of the DataObject
        object.dispose();
    }
    
    public void objectRemoved(Crawler crawler, URI uri) {
        modelSet.removeModel(uri);
    }
}
}
