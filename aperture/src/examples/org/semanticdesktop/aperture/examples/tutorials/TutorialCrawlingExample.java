/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.tutorials;

import java.io.File;

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
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.impl.DefaultSubCrawlerRegistry;

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
        // create a model that will store the data source configuration
        Model model = RDF2Go.getModelFactory().createModel();
        // open the model
        model.open();
        // .. and wrap it in an RDFContainer
        RDFContainer configuration = new RDFContainerImpl(model, new URIImpl("source:testSource"), false);
        
        // now create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        // and set the configuration container
        source.setConfiguration(configuration);
        // now we can call the type-specific setters in each DataSource class
        source.setRootFolder(rootFile.getAbsolutePath());
        
        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new TutorialCrawlerHandler());

        // start crawling
        crawler.crawl();
    }
}

class TutorialCrawlerHandler extends CrawlerHandlerBase {

    // our 'persistent' modelSet
    private ModelSet modelSet;

    public TutorialCrawlerHandler() throws ModelException {
        super (new MagicMimeTypeIdentifier(), new DefaultExtractorRegistry(), 
            new DefaultSubCrawlerRegistry());
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
        finally {
            modelSet.close();
        }
    }

    public RDFContainer getRDFContainer(URI uri) {
        // we create a new in-memory temporary model for each data source
        Model model = RDF2Go.getModelFactory().createModel(uri);
        // A model needs to be opened before being wrapped in an RDFContainer
        model.open();
        return new RDFContainerImpl(model, uri);
    }
    
    public void objectNew(Crawler crawler, DataObject object) {
        // first we try to extract the information from the binary file
        try {
            processBinary(crawler, object);
        } catch (Exception x) {
            // do some proper logging now in real applications
            x.printStackTrace();
        }
        // then we add this information to our persistent model
        modelSet.addModel(object.getMetadata().getModel());
        // don't forget to dispose of the DataObject
        object.dispose();
    }

    public void objectChanged(Crawler crawler, DataObject object) {
        // first we remove old information about the data object
        modelSet.removeModel(object.getID());
        // then we try to extract metadata and fulltext from the file
        try {
            processBinary(crawler, object);
        } catch (Exception x) {
            // do some proper logging now in real applications
            x.printStackTrace();
        }
        // an then we add the information from the temporary model to our
        // 'persistent' model
        modelSet.addModel(object.getMetadata().getModel());
        // don't forget to dispose of the DataObject
        object.dispose();
    }
    
    public void objectRemoved(Crawler crawler, URI uri) {
        // an object has been removed, we delete it from the rdf store
        modelSet.removeModel(uri);
    }
}