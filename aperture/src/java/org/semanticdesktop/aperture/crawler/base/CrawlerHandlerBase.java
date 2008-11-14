/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
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
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A trivial default implementation of the CrawlerHandler interface. The method implementations are simplest
 * possible, that fulfill the contract. The applications are expected to override the methods they need.
 * 
 * The {@link #processBinary(DataObject)} object is provided as a reference implementation to
 * show how to use the MIMEtype-detector and the extractors.
 */
public class CrawlerHandlerBase implements CrawlerHandler {

    /**
     * should binaries be processed?
     */
    protected boolean extractingContents = true;
    
    /**
     * Mime-type identifier, must be set by overriding classes
     * to use processBinary
     */
    protected MimeTypeIdentifier mimeTypeIdentifier;
    
    /**
     * Extractor registry,  must be set by overriding classes
     * to use processBinary
     */
    protected ExtractorRegistry extractorRegistry;

    /**
     * Returns an rdf container factory. This method implementation returns a factory which delivers simple
     * RDFContainers backed by in-memory models obtained from the {@link RDF2Go#getModelFactory()} method.
     * Each model is separate.
     * 
     * @see CrawlerHandler#getRDFContainerFactory(Crawler, String)
     */
    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return new RDFContainerFactory() {

            public RDFContainer getRDFContainer(URI uri) {
                Model model = RDF2Go.getModelFactory().createModel();
                model.open();
                return new RDFContainerImpl(model, uri);
            }
        };
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#accessingObject(Crawler, String)
     */
    public void accessingObject(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearFinished(Crawler, ExitCode)
     */
    public void clearFinished(Crawler crawler, ExitCode exitCode) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearingObject(Crawler, String)
     */
    public void clearingObject(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearStarted(Crawler)
     */
    public void clearStarted(Crawler crawler) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#crawlStarted(Crawler)
     */
    public void crawlStarted(Crawler crawler) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#crawlStopped(Crawler, ExitCode)
     */
    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
    // don't do anything, please override me
    }

    /**
     * This method implementation only disposes the data object and does nothing more. It is meant to be
     * overridden.
     * 
     * @see CrawlerHandler#objectChanged(Crawler, DataObject)
     */
    public void objectChanged(Crawler crawler, DataObject object) {
    // don't do anything, please override me
    }

    /**
     * This method implementation only disposes the data object and does nothing more. It is meant to be
     * overridden.
     * 
     * @see CrawlerHandler#objectNew(Crawler, DataObject)
     */
    public void objectNew(Crawler crawler, DataObject object) {
        object.dispose();
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#objectNotModified(Crawler, String)
     */
    public void objectNotModified(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#objectRemoved(Crawler, String)
     */
    public void objectRemoved(Crawler crawler, String url) {
    // don't do anything, please override me
    }
    
    /**
     * Default and reference implementation of the
     * handling of objects found in the crawling process:
     * Identify the mime-type, invoke Extractors.
     * Interprets the boolean value "extractingContents"
     * which is by default true.
     * @param dataObject the data object to process.
     * When the passed DataObject is not a FileDataObject,
     * nothing will be done.
     * @throws IOException when the stream cannot be read
     * @throws ExctractorException when the extractor fails
     */
    protected void processBinary(DataObject dataObject) throws IOException, ExtractorException {
        // we cannot do anything when MIME type identification is disabled
        if (!extractingContents) {
            return;
        }
        // check prerequisites
        if (mimeTypeIdentifier == null)
            throw new RuntimeException("mimeTypeIdentifier is not set");
        if (extractorRegistry == null)
            throw new RuntimeException("extractorRegistry is not set");
        // process the contents on an InputStream, if available
        if (dataObject instanceof FileDataObject) {
            FileDataObject object = (FileDataObject) dataObject;

            URI id = object.getID();

            // Create a buffer around the object's stream large enough to be able to reset the stream
            // after MIME type identification has taken place. Add some extra to the minimum array
            // length required by the MimeTypeIdentifier for safety.
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            int bufferSize = Math.max(minimumArrayLength, 8192);
            BufferedInputStream bufferedStream = new BufferedInputStream(object.getContent(), bufferSize);
            bufferedStream.mark(minimumArrayLength + 10); // add some for safety

            // apply the MimeTypeIdentifier
            byte[] bytes = IOUtil.readBytes(bufferedStream, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);

            if (mimeType != null) {
                // add the mime type to the metadata
                RDFContainer metadata = object.getMetadata();
                metadata.add(NIE.mimeType, mimeType);

                bufferedStream.reset();
                
                // apply an Extractor if available
                Set extractors = extractorRegistry.getExtractorFactories(mimeType);
                if (!extractors.isEmpty()) {
                    ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                    Extractor extractor = factory.get();
                    extractor.extract(id, bufferedStream, null, mimeType, metadata);
                    return;
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
     * should binaries be processed?
     * @return true, when binaries are processed
     */
    public boolean isExtractingContents() {
        return extractingContents;
    }

    /**
     * should binaries be processed?
     * @param extractingContents set to true to extract the contents when calling
     * {@link #processBinary(DataObject)} 
     */
    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

}
