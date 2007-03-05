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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.rdf2go.RepositoryModel;
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

	private static final Logger LOGGER = Logger.getLogger(SimpleCrawlerHandler.class.getName());
	
	protected Model model;
	
	protected int nrObjects;
	
	boolean identifyingMimeType = false;
	
	boolean extractingContents = false;
	
	boolean verbose;
	
	private MimeTypeIdentifier mimeTypeIdentifier;

    private ExtractorRegistry extractorRegistry;
    
    private File repositoryFile;

    public SimpleCrawlerHandler(MimeTypeIdentifier mimeTypeIdentifier, ExtractorRegistry extractorRegistry,
    		File repositoryFile) {
    	try {
        	model = new RepositoryModel(false);
        } catch (ModelException me) {
        	throw new RuntimeException(me);
        }
    	
        // create some identification and extraction components
        if (mimeTypeIdentifier != null) {
        	identifyingMimeType = true;
        	this.mimeTypeIdentifier = mimeTypeIdentifier;
        }
        if (extractorRegistry != null) {
        	this.extractorRegistry = extractorRegistry;
        	extractingContents = true;
        }
        
        verbose = true;
        
        this.repositoryFile = repositoryFile;
    }
    
    public void accessingObject(Crawler crawler, String url) {
        if (verbose) {
            System.out.println("Processing file " + nrObjects + ": " + url + "...");
        }
    }

    public void objectNew(Crawler dataCrawler, DataObject object) {
        nrObjects++;

        // process the contents on an InputStream, if available
        if (object instanceof FileDataObject) {
            try {
                process((FileDataObject) object);
            }
            catch (IOException e) {
                LOGGER.log(Level.WARNING, "IOException while processing " + object.getID(), e);
            }
            catch (ExtractorException e) {
                LOGGER.log(Level.WARNING, "ExtractorException while processing " + object.getID(), e);
            }
        }
        
        object.dispose();
    }

    private void process(FileDataObject object) throws IOException, ExtractorException {
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
            // add the mime type to the metadata
            RDFContainer metadata = object.getMetadata();
            metadata.add(DATA.mimeType, mimeType);

            // apply an Extractor if available
            if (extractingContents) {
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
	        Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
	        model.writeTo(writer,Syntax.RdfXml);
	        writer.close();
	
	        System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
	        System.out.println("Saved RDF model to " + repositoryFile);
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
		return new RDFContainerImpl(model, uri, true);
	}

	protected void printUnexpectedEventWarning(String event) {
	     // as we don't keep track of access data in this example code, some events should never occur
	     LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
	}
}

