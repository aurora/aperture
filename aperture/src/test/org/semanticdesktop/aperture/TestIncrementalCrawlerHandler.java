/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerRegistry;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * A CrawlerHandler implementation for use in incremental crawling tests. By default all data encountered by
 * the crawlers is packed into a single shared model. This behavior may be overrdden by passing in another
 * RDFContainerFactory implementation.
 */
public class TestIncrementalCrawlerHandler extends CrawlerHandlerBase {

    private Model model;

    private int numberOfObjects;

    private SubCrawlerRegistry subCrawlerRegistry;

    private ExtractorRegistry extractorRegistry;

    private MimeTypeIdentifier mimeTypeIdentifier;

    private Set<String> newObjects;

    private Set<String> changedObjects;

    private Set<String> unchangedObjects;

    private Set<String> deletedObjects;

    private RDFContainerFactory containerFactory;
    
    private File file;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a plain crawler handler. No subcrawling or extraction will be done
     */
    public TestIncrementalCrawlerHandler() {
        this(null,null,false);
    }
    
    /**
     * Constructs a crawler handler with a subcrawler registry;
     * 
     * @param subCrawlerRegistry the subcrawler registry, can be null, in which case no subcrawler will be applied
     */
    public TestIncrementalCrawlerHandler(SubCrawlerRegistry subCrawlerRegistry) {
        this(subCrawlerRegistry,null,false);
    }
    
    /**
     * A constructs a crawler handler with an extractor registry
     * 
     * @param extractorRegistry the extractor registry, can be null, in which case no extractor will be
     *            applied
     */
    public TestIncrementalCrawlerHandler(ExtractorRegistry extractorRegistry) {
        this(null, extractorRegistry,false);
    }

    /**
     * The default constructor
     * 
     * @param extractorRegistry the extractor registry, can be null, in which case no extractor will be
     *            applied
     * @param subCrawlerRegistry the subcrawler registry, can be null, in which case no subcrawler will be
     *            applied
     * @param separateModels a flag indicating if the container factory returned by
     *            {@link #getRDFContainerFactory(Crawler, String)} is to return containers backed by a single
     *            shared model (false) or by separate models (true). In the first case (false) disposing the
     *            containers doesn't close the model, in the second case disposing a container closes the
     *            underlying model.
     */
    public TestIncrementalCrawlerHandler(SubCrawlerRegistry subCrawlerRegistry, ExtractorRegistry extractorRegistry, boolean separateModels) {
        model = RDF2Go.getModelFactory().createModel();
        model.open();
        newObjects = new HashSet<String>();
        changedObjects = new HashSet<String>();
        unchangedObjects = new HashSet<String>();
        deletedObjects = new HashSet<String>();
        this.mimeTypeIdentifier = new MagicMimeTypeIdentifier();
        this.subCrawlerRegistry = subCrawlerRegistry;
        this.extractorRegistry = extractorRegistry;
        if (separateModels) {
            this.containerFactory = new RDFContainerFactory() {
                public RDFContainer getRDFContainer(URI uri) {
                    Model lmodel = RDF2Go.getModelFactory().createModel();
                    lmodel.open();
                    return new RDFContainerImpl(lmodel, uri);
                }
            };
        } else {
            this.containerFactory = new RDFContainerFactory() {
                public RDFContainer getRDFContainer(URI uri) {
                    return new RDFContainerImpl(model, uri, true);
                }
            };
        }
        
    }

    /**
     * Closes the underlying model
     */
    public void close() {
        model.close();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////// CRAWLER HANDLER METHODS //////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    public void crawlStarted(Crawler crawler) {
        numberOfObjects = 0;
        newObjects.clear();
        changedObjects.clear();
        unchangedObjects.clear();
        deletedObjects.clear();
    }

    public void objectChanged(Crawler crawler, DataObject object) {
        numberOfObjects++;
        changedObjects.add(object.getID().toString());
        process(object, crawler);
        object.dispose();
    }

    public void objectNew(Crawler crawler, DataObject object) {
        numberOfObjects++;
        newObjects.add(object.getID().toString());
        process(object, crawler);
        object.dispose();
    }

    public void objectNotModified(Crawler crawler, String url) {
        numberOfObjects++;
        unchangedObjects.add(url);
    }

    public void objectRemoved(Crawler crawler, String url) {
        deletedObjects.add(url);
    }

    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return containerFactory;
    }

    private void process(DataObject objectToProcess, Crawler crawler) {
        if (!(objectToProcess instanceof FileDataObject)) {
            return;
        }
        FileDataObject object = (FileDataObject) objectToProcess;
        try {
            URI id = object.getID();
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            InputStream contentStream;
            contentStream = object.getContent();
            contentStream.mark(minimumArrayLength + 10); // add some for safety
            byte[] bytes = IOUtil.readBytes(contentStream, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);
            if (mimeType == null) {
                return;
            }
            contentStream.reset();
            boolean done = applyExtractor(id, contentStream, mimeType, object.getMetadata());
            if (done) {
                return;
            }
            // else try to apply a FileExtractor
            done = applyFileExtractor(object, id, mimeType, object.getMetadata());
            if (done) {
                return;
            }
            // or maybe apply a SubCrawler
            done = applySubCrawler(contentStream, mimeType, object, crawler);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ExtractorException e) {
            e.printStackTrace();
        }
        catch (SubCrawlerException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyExtractor(URI id, InputStream contentStream, String mimeType, RDFContainer metadata)
            throws ExtractorException {
        if (extractorRegistry == null) {
            return false;
        }
        Set extractors = extractorRegistry.getExtractorFactories(mimeType);
        if (!extractors.isEmpty()) {
            ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
            Extractor extractor = factory.get();
            extractor.extract(id, contentStream, null, mimeType, metadata);
            return true;
        }
        else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyFileExtractor(FileDataObject object, URI id, String mimeType, RDFContainer metadata)
            throws ExtractorException, IOException {
        if (extractorRegistry == null) {
            return false;
        }
        Set fileextractors = extractorRegistry.getFileExtractorFactories(mimeType);
        if (!fileextractors.isEmpty()) {
            FileExtractorFactory factory = (FileExtractorFactory) fileextractors.iterator().next();
            FileExtractor extractor = factory.get();
            File originalFile = object.getFile();
            if (originalFile != null) {
                extractor.extract(id, originalFile, null, mimeType, metadata);
                return true;
            }
            else {
                File tempFile = object.downloadContent();
                try {
                    extractor.extract(id, tempFile, null, mimeType, metadata);
                    return true;
                }
                finally {
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
            }
        }
        else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applySubCrawler(InputStream contentStream, String mimeType, DataObject object,
            Crawler crawler) throws SubCrawlerException {
        if (subCrawlerRegistry == null) {
            return false;
        }
        Set subCrawlers = subCrawlerRegistry.get(mimeType);
        if (!subCrawlers.isEmpty()) {
            SubCrawlerFactory factory = (SubCrawlerFactory) subCrawlers.iterator().next();
            SubCrawler subCrawler = factory.get();
            crawler.runSubCrawler(subCrawler, object, contentStream, null, mimeType);
            return true;
        }
        else {
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// RDF CONTAINER FACTORY METHOD //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the underlying model
     * 
     * @return the underlying model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Returns the total number of objects encountered by the crawler
     * 
     * @return the total number of objects encountered by the crawler
     */
    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    /**
     * Returns the number of changed objects
     * 
     * @return the number of changed objects
     */
    public Set<String> getChangedObjects() {
        return changedObjects;
    }

    /**
     * Returns the number of deleted objects
     * 
     * @return the number of deleted objects
     */
    public Set<String> getDeletedObjects() {
        return deletedObjects;
    }

    /**
     * Returns the number of new objects
     * 
     * @return the number of new objects
     */
    public Set<String> getNewObjects() {
        return newObjects;
    }

    /**
     * Returns the number of unchanged objects
     * 
     * @return the number of unchanged objects
     */
    public Set<String> getUnchangedObjects() {
        return unchangedObjects;
    }

    /**
     * Returns the file
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
}
