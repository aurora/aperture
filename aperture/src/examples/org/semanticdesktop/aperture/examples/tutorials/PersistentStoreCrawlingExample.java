/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.tutorials;

import java.io.File;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModelSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.impl.DefaultMimeTypeIdentifierRegistry;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.impl.DefaultSubCrawlerRegistry;

/**
 * A simple example how to use an Aperture crawler with persistent storage
 */
public class PersistentStoreCrawlingExample {

    /**
     * A simplistic main method
     * 
     * @param args command line arguments
     * @throws Exception if something goes wrong
     */
    public static void main(String[] args) throws Exception {
        // create a new ExampleFileCrawler instance
        PersistentStoreCrawlingExample crawler = new PersistentStoreCrawlingExample();

        if (args.length != 1) {
            System.err.println("Specify the root folder");
            System.exit(-1);
        }

        // start crawling and exit afterwards
        crawler.doCrawling(new File(args[0]));
    }

    /**
     * Initializes the crawler and performs the actual crawling
     * 
     * @param rootFile a File instance representing the folder that is to be crawled
     * @throws Exception if something goes wrong in the process
     */
    public void doCrawling(File rootFile) throws Exception {
        // First we need a model that will store the data source configuration
        Model model = RDF2Go.getModelFactory().createModel();
        // Don't forget to open it before it can be used
        model.open();
        // Then we wrap it in an RDFContainer
        RDFContainer configuration = new RDFContainerImpl(model, new URIImpl("source:testSource"), false);

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        // note that the setConfiguration method must be called before setRootFolder
        source.setConfiguration(configuration);
        // now we can specify the folder where the crawling should begin
        source.setRootFolder(rootFile.getAbsolutePath());

        // this is the folder where we want to store the extracted data
        File nativeStoreFolder = new File("NativeStoreFolder");
        // this call will prepare a persistent modelSet that will store information
        // in the folder defined above
        ModelSet modelSet = createPersistentModelSet(nativeStoreFolder);
        System.out.println("Storing results into folder "+nativeStoreFolder.getAbsolutePath());
        // this crawler handler implementation (see below) will store information
        // in the provided model set
        CrawlerHandler handler = new UserDefinedStoreCrawlerHandler(modelSet);

        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        // Each crawler needs a data source
        crawler.setDataSource(source);
        // most crawlers work with accessors, it's always good for a crawler
        // to be equipped with an accessor registry, in this case we use
        // a DefaultDataAccessorRegistry which is initialized with all available
        // accessor implementations in Aperture
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        // the crawler itself doesn't know what to do with the extracted information
        // this method sets the crawler handler, written by us, which will
        // perform any post-processing necessary for our application - in this
        // case the CrawlerHandler will simply store the extracted data in a
        // persistent store
        crawler.setCrawlerHandler(handler);

        // start crawling
        crawler.crawl();
    }

    private ModelSet createPersistentModelSet(File nativeStoreFolder) {
        // First we create a native store that will contain the extracted data
        NativeStore nativeStore = new NativeStore(nativeStoreFolder);
        // Next we wrap the native store in a repository, that's the way it goes in Sesame
        Repository repository = new SailRepository(nativeStore);
        try {
            // Before we can use the repository it has to be initialized
            repository.initialize();
        }
        catch (RepositoryException e) {
            e.printStackTrace();
        }
        // Now we can wrap the repository in a Sesame-specific ModelSet implementation
        ModelSet modelSet = new RepositoryModelSet(repository);
        // ... and open it
        modelSet.open();
        // We can return the newly created persistent model set.
        return modelSet;
    }
}

class UserDefinedStoreCrawlerHandler extends CrawlerHandlerBase {

    /** The model set used to store all extracted data */
    private ModelSet modelSet;

    /**
     * Default constructor
     * 
     * @param modelSet the ModelSet used to store the extracted data.
     */
    public UserDefinedStoreCrawlerHandler(ModelSet modelSet) {
        super (new MagicMimeTypeIdentifier(), new DefaultExtractorRegistry(), 
            new DefaultSubCrawlerRegistry());
        this.modelSet = modelSet;
    }

    /**
     * This method is called by the crawler when the crawl is stopped.
     * 
     * @param crawler the crawler that has been stopped
     * @param exitCode the exit code.
     */
    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
        // since we don't need the ModelSet anymore, we can close it now
        modelSet.close();
    }

    /**
     * Returns an RDFContainer. This method is called by the crawler when it wants to store some data about an
     * object with a given UI. In this example we use an in-memory model as a transfer object. We consider it
     * a good practice not to give the crawlers access to a persistent store. Many things may go wrong between
     * the call to this method and returning the data as a DataObject with a call to the objectNew or
     * objectModified method.
     * 
     * @param uri the URI of the data object
     * @return an RDFContainer instance describing the given uri, backed by a Model chosen by the user - in
     *         this case a simple in-memory transfer model
     */
    public RDFContainer getRDFContainer(URI uri) {
        // we create a new in-memory temporary model for each data object
        Model model = RDF2Go.getModelFactory().createModel(uri);
        // remember to open it before you can use it
        model.open();
        // and return the model wrapped in an appropriate RDFContainerImpl instance
        return new RDFContainerImpl(model, uri);
    }

    /**
     * Called when the crawler wants to report a new DataObject. This method copies the data about the data object 
     * to the central model set. (Which is backed by a persistent RDF Store. 
     * @param crawler the crawler that makes the call
     * @param object the new data object
     */
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

    /**
     * Called when the crawler wants to report that a data object has been modified. This method is quite similar 
     * to objectNew with the exception that it deletes the old data from the central model set before copying
     * the new.
     * @param crawler the crawler that makes the call
     * @param object the data object found to have been modified
     */
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

    /**
     * Called when the crawler wants to report that a data object has been removed. This method deletes the data
     * about the data object from the ModelSet.
     * @param crawler
     * @param uri
     */
    public void objectRemoved(Crawler crawler, URI uri) {
        modelSet.removeModel(uri);
    }
}
    