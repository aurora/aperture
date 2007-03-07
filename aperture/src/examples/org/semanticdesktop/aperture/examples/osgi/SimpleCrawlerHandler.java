/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.osgi;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
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
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

    private ModelSet modelSet;

    private int nrObjects;

    private MimeTypeIdentifier mimeTypeIdentifier;

    private ExtractorRegistry extractorRegistry;

    private File outputFile;

    public SimpleCrawlerHandler(MimeTypeIdentifier mimeTypeIdentifier, ExtractorRegistry extractorRegistry,
            File outputFile) {
        this.mimeTypeIdentifier = mimeTypeIdentifier;
        this.extractorRegistry = extractorRegistry;
        this.outputFile = outputFile;

        // create a ModelSet that will gather all extracted metadata
        ModelFactory factory = RDF2Go.getModelFactory();
        try {
            modelSet = factory.createModelSet();
        }
        catch (ModelException e) {
            throw new RuntimeException(e);
        }
    }

    public void accessingObject(Crawler crawler, String url) {
        System.out.println("Processing file " + nrObjects + ": " + url + "...");
    }

    public void objectNew(Crawler dataCrawler, DataObject object) {
        nrObjects++;

        // process the contents of an InputStream, if available
        if (object instanceof FileDataObject) {
            try {
                process((FileDataObject) object);
            }
            catch (Exception e) {
                System.err.println("Exception while processing " + object.getID());
                e.printStackTrace();
            }
        }

        object.dispose();
    }

    private void process(FileDataObject object) throws IOException, ExtractorException {
        // we cannot do anything when MIME type identification is disabled
        if (mimeTypeIdentifier == null) {
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
            // add the mime type to the metadata
            RDFContainer metadata = object.getMetadata();
            metadata.add(DATA.mimeType, mimeType);

            // apply an Extractor if available
            if (extractorRegistry != null) {
                buffer.reset();

                Set extractors = extractorRegistry.get(mimeType);
                if (!extractors.isEmpty()) {
                    ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                    Extractor extractor = factory.get();
                    extractor.extract(id, buffer, null, mimeType, metadata);
                }
            }
        }
    }

    public void crawlStarted(Crawler crawler) {
        nrObjects = 0;
    }

    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
        try {
            Writer writer = new BufferedWriter(new FileWriter(outputFile));
            modelSet.writeTo(writer, Syntax.Trix);
            writer.close();
            modelSet.close();

            System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
            System.out.println("Saved RDF model to " + outputFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void objectChanged(Crawler dataCrawler, DataObject object) {
        object.dispose();
        printUnexpectedEventWarning("changed");
    }

    public void objectNotModified(Crawler crawler, String url) {
        printUnexpectedEventWarning("unmodified");
    }

    public void objectRemoved(Crawler dataCrawler, String url) {
        printUnexpectedEventWarning("removed");
    }

    public void clearStarted(Crawler crawler) {
        printUnexpectedEventWarning("clearStarted");
    }

    public void clearingObject(Crawler crawler, String url) {
        printUnexpectedEventWarning("clearingObject");
    }

    public void clearFinished(Crawler crawler, ExitCode exitCode) {
        printUnexpectedEventWarning("clear finished");
    }

    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return this;
    }

    public RDFContainer getRDFContainer(URI uri) {
        Model model = modelSet.getModel(uri);
        return new RDFContainerImpl(model, uri);
    }

    private void printUnexpectedEventWarning(String event) {
        // as we don't keep track of access data in this example code, some events should never occur
        System.err.println("encountered unexpected event (" + event + ") with non-incremental crawler");
    }
}
