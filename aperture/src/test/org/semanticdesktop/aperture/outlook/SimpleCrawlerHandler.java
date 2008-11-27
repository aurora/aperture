/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * An example of a simple (but non-trivial) crawler handler.
 */
public class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

    // The main model set, that will contain all data 
    private ModelSet modelSet;

    
    ////////////////// OBSERVABLE PROPERTIES ///////////////////// 
    private int nrObjects;

    private long startTime = 0L;

    private long finishTime = 0L;

    private String currentURL;

    private ExitCode exitCode;
    

    // the mime type identifier
    private MimeTypeIdentifier mimeTypeIdentifier;

    private ExtractorRegistry extractorRegistry;

    private boolean identifyingMimeType;

    private boolean extractingContents;

    private boolean noOutput;
    
    private boolean verbose;
    
    private File outputFile;

    /**
     * Constructor.
     * 
     * @param identifyingMimeType 'true' if the crawler is to use a MIME type identifier on each
     *            FileDataObject it gets, 'false' if not
     * @param extractingContents 'true' if the crawler is to use an extractor on each DataObject it gets
     *            'false' if not
     * @param verbose 'true' if the crawler is to print verbose messages on what it is doing, false otherwise
     * @param outputFile the file where the extracted RDF metadata is to be stored. This argument can also be
     *            set to 'null', in which case the RDF metadata will not be stored in a file. This setting is
     *            useful for performance measurements.
     * @throws ModelException
     */
    public SimpleCrawlerHandler(boolean identifyingMimeType, boolean extractingContents, boolean verbose, File outputFile) {
        // create a ModelSet that will hold the RDF Models of all crawled files and folders
        ModelFactory factory = RDF2Go.getModelFactory();
        modelSet = factory.createModelSet();
        modelSet.open();

        // create some identification and extraction components
        this.identifyingMimeType = identifyingMimeType;
        if (identifyingMimeType) {
            mimeTypeIdentifier = new MagicMimeTypeIdentifier();
        }
        this.extractingContents = extractingContents;
        if (extractingContents) {
            extractorRegistry = new DefaultExtractorRegistry();
        }

        if (outputFile == null) {
            this.outputFile = null;
            this.noOutput = true;
        }
        else {
            this.outputFile = outputFile;
            this.noOutput = false;
        }
        this.verbose = verbose;
    }

    /**
     * This method gets called when the crawl has been started
     * 
     * @param crawler the crawler that started the crawl.
     */
    public void crawlStarted(Crawler crawler) {
        nrObjects = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * This method gets called when the crawler has began accessing an object.
     * 
     * @param crawler the crawler
     * @param url the URI of the object
     */
    public void accessingObject(Crawler crawler, String url) {
        this.currentURL = url;
        if (verbose) {
            System.out.println("Processing file " + nrObjects + ": " + url + "...");
        }
    }

    /**
     * This method gets called when the crawler has encountered a new DataObject
     * 
     * @param dataCrawler the crawler
     * @param object the DataObject
     */
    public void objectNew(Crawler dataCrawler, DataObject object) {
        nrObjects++;
        this.currentURL = object.getID().toString();
        if (nrObjects % 300 == 0)
            // call garbage collector from time to time
            System.gc();

        // process the contents of an InputStream, if available
        if (object instanceof FileDataObject) {
            String s = null;
            try {
                process((FileDataObject) object);
            }
            catch (Exception e) {
                System.err.println("Exception while processing file size (" + s + ") of " + object.getID());
                e.printStackTrace();
            }
        }
        disposeDataObject(object);
    }
    
    protected void disposeDataObject(DataObject object) {
        // really dispose the RDFContainer when noOutput
        object.dispose();
        if (noOutput) {
            try {
                ((Repository) object.getMetadata().getModel().getUnderlyingModelImplementation()).shutDown();
            }
            catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void process(FileDataObject object) throws IOException, ExtractorException, ModelException {
        // we cannot do anything when MIME type identification is disabled
        if (!identifyingMimeType) {
            return;
        }

        URI id = object.getID();

        // Create a buffer around the object's stream large enough to be able to reset the stream
        // after MIME type identification has taken place. Add some extra to the minimum array
        // length required by the MimeTypeIdentifier for safety.
        int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
        int bufferSize = Math.max(minimumArrayLength, 8192);
        BufferedInputStream buffer = new BufferedInputStream(object.getContent(), bufferSize);
        buffer.mark(minimumArrayLength + 10); // add some for safety

        // apply the MimeTypeIdentifier
        byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);
        String mimeType = mimeTypeIdentifier.identify(bytes, null, id);

        if (mimeType != null) {
            // add the MIME type to the metadata
            RDFContainer metadata = object.getMetadata();
            metadata.add(NIE.mimeType, mimeType);

            
            if (extractingContents) {
                buffer.reset();
                
                // apply an Extractor if available
                Set extractors = extractorRegistry.get(mimeType);
                if (!extractors.isEmpty()) {
                    ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                    Extractor extractor = factory.get();
                    extractor.extract(id, buffer, null, mimeType, metadata);
                }
                
                // else try to apply a FileExtractor
                Set fileextractors = extractorRegistry.getFileExtractorFactories(mimeType);
                if (!fileextractors.isEmpty()) {
                    FileExtractorFactory factory = (FileExtractorFactory) fileextractors.iterator().next();
                    FileExtractor extractor = factory.get();
                    File originalFile = object.getFile();
                    if (originalFile != null) {
                        extractor.extract(id, originalFile, null, mimeType, metadata);
                    } else {
                        File tempFile = object.downloadContent();
                        extractor.extract(id, tempFile, null, mimeType, metadata);
                        tempFile.delete();
                    }
                }
            }
        }
    }

    /**
     * This method gets called when the crawler has encountered an object that has been modified
     * 
     * @param dataCrawler the crawler
     * @param object the DataObject
     */
    public void objectChanged(Crawler dataCrawler, DataObject object) {
        // as we do not use incremental crawling, this should not happen
        object.dispose();
        this.currentURL = object.getID().toString();
        printUnexpectedEventWarning("changed");
    }

    /**
     * This method gets called when the crawler has encountered an object that has not been modified.
     * 
     * @param crawler the crawler
     * @param url the URI of the object.
     */
    public void objectNotModified(Crawler crawler, String url) {
        // as we do not use incremental crawling, this should not happen
        this.currentURL = url;
        printUnexpectedEventWarning("unmodified");
    }

    /**
     * This method gets called when the crawler has encountered an object that has been removed from the data
     * source
     * 
     * @param dataCrawler the crawler
     * @param url the URI of the DataObject
     */
    public void objectRemoved(Crawler dataCrawler, String url) {
        // as we do not use incremental crawling, this should not happen
        printUnexpectedEventWarning("removed");
        this.currentURL = url;
    }

    /**
     * This method gets called when the crawler started clearing the data source.
     * @param crawler the crawler
     */
    public void clearStarted(Crawler crawler) {
        // as we do not use incremental crawling, this should not happen
        printUnexpectedEventWarning("clearStarted");
    }

    /**
     * This method gets called when the crawler clears an object
     * @param crawler the crawler
     * @param url the URI of the data object
     */
    public void clearingObject(Crawler crawler, String url) {
        // as we do not use incremental crawling, this should not happen
        printUnexpectedEventWarning("clearingObject");
    }

    /**
     * This method gets called when the crawler has finished clearing
     * @param crawler the crawler
     * @param exitCode the exitCode
     */
    public void clearFinished(Crawler crawler, ExitCode exitCode) {
        // as we do not use incremental crawling, this should not happen
        printUnexpectedEventWarning("clear finished");
    }

    /**
     * Returns an RDFContainerFactory
     * @param crawler the Crawler that requests and RDFContainer
     * @param url the URL 
     * @return an RDFContainerFactory
     */
    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return this;
    }

    /**
     * Returns an RDFContainer for a particular uri
     * @param uri the URIs
     * @return an RDFContainer for a particular URI
     */
    public RDFContainer getRDFContainer(URI uri) {
        // note: by using ModelSet.getModel, all statements added to this Model are added to the ModelSet
        // automatically, unlike ModelFactory.createModel, which creates stand-alone models.

        // when running performance tests, we dump the dataobjects,
        // otherwise we channel the triples into the modelSet
        Model model = (noOutput) ? RDF2Go.getModelFactory().createModel() : modelSet.getModel(uri);
        model.open();
        return new RDFContainerImpl(model, uri);
    }

    private void printUnexpectedEventWarning(String event) {
        // as we don't keep track of access data in this example code, some events should never occur
        System.err.println("encountered unexpected event (" + event + ") with non-incremental crawler");
    }

    /**
     * This method gets called when the crawler finishes crawling a data source
     * @param crawler the crawler
     * @param code the exit code.
     */
    public void crawlStopped(Crawler crawler, ExitCode code) {
        printAndCloseModelSet();
        this.finishTime = System.currentTimeMillis();
        this.exitCode = code;
    }
    
    protected void printAndCloseModelSet() {
        try {
            if (!noOutput) {
                OutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile));
                modelSet.writeTo(stream, Syntax.RdfXml);
                stream.close();
                System.out.println("Saved RDF model to " + outputFile);
            } else {
                System.out.println("Output discarded");
            }
            modelSet.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ModelSet getModelSet() {
        return modelSet;
    }

    
    /**
     * @return Returns the startTime.
     */
    public long getStartTime() {
        return startTime;
    }

    
    /**
     * @return Returns the finishTime.
     */
    public long getFinishTime() {
        return finishTime;
    }

    
    /**
     * @return Returns the currentURL.
     */
    public String getCurrentURL() {
        return currentURL;
    }

    
    /**
     * @return Returns the exitCode.
     */
    public ExitCode getExitCode() {
        return exitCode;
    }

    
    /**
     * @return Returns the nrObjects.
     */
    public int getNrObjects() {
        return nrObjects;
    }
}
