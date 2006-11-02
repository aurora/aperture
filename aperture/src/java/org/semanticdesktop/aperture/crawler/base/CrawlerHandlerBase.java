/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
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
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * Utility class. Provides default implementations for most methods of crawler handler. May facilitate the
 * integration of aperture into existing applications. The only thing the user has to provide are the
 * RDFContainerFactory methods and the crawlStopped method (what to do with the generated RDF data).
 */
public abstract class CrawlerHandlerBase implements CrawlerHandler, RDFContainerFactory {

	private static Logger LOGGER = Logger.getLogger(CrawlerHandlerBase.class.getName());

	private MimeTypeIdentifier mimeTypeIdentifier;

	private ExtractorRegistry extractorRegistry;

	private boolean extractContents;

	public CrawlerHandlerBase() {
		// create some identification and extraction components
		extractContents = true;
		mimeTypeIdentifier = new MagicMimeTypeIdentifier();
		extractorRegistry = new DefaultExtractorRegistry();
	}

	public void crawlStarted(Crawler crawler) {
		
	}

	public abstract void crawlStopped(Crawler crawler, ExitCode exitCode);

	public void accessingObject(Crawler crawler, String url) {

	}

	public void objectNew(Crawler dataCrawler, DataObject object) {
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

	protected void process(FileDataObject object) throws IOException, ExtractorException {
		// we cannot do anything when MIME type identification is disabled
		if (!extractContents) {
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

			buffer.reset();

			Set extractors = extractorRegistry.get(mimeType);
			if (!extractors.isEmpty()) {
				ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
				Extractor extractor = factory.get();
				extractor.extract(id, buffer, null, mimeType, metadata);
			}
		}
	}

	protected void printUnexpectedEventWarning(String event) {
		// as we don't keep track of access data in this example code, some events should never occur
		LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
	}

	
	public boolean isExtractContents() {
		return extractContents;
	}

	
	public void setExtractContents(boolean extractContents) {
		this.extractContents = extractContents;
	}
}
