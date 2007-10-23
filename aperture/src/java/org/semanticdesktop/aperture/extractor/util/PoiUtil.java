/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;

/**
 * Features Apache POI-specific utility methods for text and metadata extraction purposes.
 * 
 * <p>
 * Some methods use a buffer to be able to reset the InputStream to its start. The buffer size can be altered
 * by giving the "aperture.poiUtil.bufferSize" system property a value holding the number of bytes that the
 * buffer may use.
 * 
 * @link http://jakarta.apache.org/poi/
 */
public class PoiUtil {

	private static final String BUFFER_SIZE_PROPERTY = "aperture.poiUtil.bufferSize";

	/**
	 * Returns the SummaryInformation holding the document metadata from a POIFSFileSystem. Any POI-related or
	 * I/O Exceptions that may occur during this operation are ignored and 'null' is returned in those cases.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @return A populated SummaryInformation, or 'null' when the relevant document parts could not be
	 *         located.
	 */
	public static SummaryInformation getSummaryInformation(POIFSFileSystem poiFileSystem) {
		SummaryInformation summary = null;

		try {
			DocumentInputStream docInputStream = poiFileSystem
					.createDocumentInputStream(SummaryInformation.DEFAULT_STREAM_NAME);
			summary = (SummaryInformation) PropertySetFactory.create(docInputStream);
			docInputStream.close();
		}
		catch (Exception e) {
			// ignore
		}

		return summary;
	}

	/**
	 * Extract all metadata from an OLE document.
	 * 
	 * @param stream The stream containing the OLD document.
	 * @param resetStream Specified whether the stream should be buffered and reset. The buffer size can be
	 *            determined by the system property described in the class documentation.
	 * @param container The RDFContainer to store the metadata in.
	 * @return If the stream passed as the input parameter supported mark() it is returned, otherwise the
	 *         stream is wrapped in a BufferedInputStream which supports mark/reset and the 
	 *         BufferedInputStream is returned
	 * @throws IOException When resetting of the buffer resulted in an IOException.
	 */
	public static InputStream extractMetadata(InputStream stream, boolean resetStream, RDFContainer container)
			throws IOException {
		if (resetStream) {
			int bufferSize = getBufferSize();
			if (!stream.markSupported()) {
				stream = new BufferedInputStream(stream, bufferSize);
			}
			stream.mark(bufferSize);
		}

		POIFSFileSystem fileSystem = new POIFSFileSystem(stream);
		extractMetadata(fileSystem, container);

		if (resetStream) {
			stream.reset();
		}
		return stream;
	}

	/**
	 * Extracts all metadata from the POIFSFileSystem's SummaryInformation and transforms it to RDF statements
	 * that are stored in the specified RDFContainer.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @param container The RDFContainer to store the created RDF statements in.
	 */
	public static void extractMetadata(POIFSFileSystem poiFileSystem, RDFContainer container) {
		SummaryInformation summary = getSummaryInformation(poiFileSystem);
		if (summary != null) {
			copyString(summary.getTitle(), NIE.title, container);
			copyString(summary.getSubject(), NIE.subject, container);
			copyString(summary.getComments(), NIE.description, container);
			copyString(summary.getApplicationName(), NIE.generator, container);
			copyContact(summary.getAuthor(), NCO.creator, container);
			copyContact(summary.getLastAuthor(), NCO.contributor, container);

			copyDate(summary.getCreateDateTime(), NIE.contentCreated, container);
			copyDate(summary.getLastSaveDateTime(), NIE.contentLastModified, container);

			int nrPages = summary.getPageCount();
			if (nrPages > 1) {
				// '0' means 'unknown' according to POI's API (<sigh>)
				// '1' is often erroneously returned and can thus not be trusted
				// higher values tend to be right (not seen a counter example yet) and are
				// therefore included
                container.add(RDF.type,NFO.PaginatedTextDocument);
				container.add(NFO.pageCount, nrPages);
			}

			String keywords = summary.getKeywords();
			if (keywords != null) {
				StringTokenizer tokenizer = new StringTokenizer(keywords, " \t.,;|/\\", false);
				while (tokenizer.hasMoreTokens()) {
					String keyword = tokenizer.nextToken();
					container.add(NIE.keyword, keyword);
				}
			}
		}
	}

    private static void copyString(String value, URI property, RDFContainer container) {
		if (value != null) {
			value = value.trim();
			if (!value.equals("")) {
				// NOTE: "add", not "put", as some properties will be used multiple times!!
				container.add(property, value);
			}
		}
	}

	private static void copyDate(Date date, URI property, RDFContainer container) {
		if (date != null) {
			container.add(property, date);
		}
	}
    
    private static void copyContact(String name, URI property, RDFContainer container) {
        if (name != null) {
            Model model = container.getModel();
            Resource resource = UriUtil.generateRandomResource(model);
            model.addStatement(container.getDescribedUri(), property, resource);
            model.addStatement(resource,RDF.type,NCO.Contact);
            model.addStatement(resource,NCO.fullname,name);
        }
    }

	/**
	 * Extract full-text and metadata from an MS Office document contained in the specified stream. A
	 * TextExtractor is specified to handle the specifics of full-text extraction for this particular MS
	 * Office document type.
	 * @return If the stream passed as the input parameter supported mark() it is returned, otherwise the
     *         stream is wrapped in a BufferedInputStream which supports mark/reset and the 
     *         BufferedInputStream is returned
	 */
	public static InputStream extractAll(InputStream stream, TextExtractor textExtractor, RDFContainer container, Logger logger) {
		// mark the stream with a sufficiently large buffer so that, when POI chokes on a document, there is a
		// good chance we can reset to the beginning of the buffer and apply a StringExtractor
		int bufferSize = getBufferSize();
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, bufferSize);
		}
		stream.mark(bufferSize);

		// apply the POI-based extraction code
		String text = null;

		try {
			// try to create a POI file system
			POIFSFileSystem fileSystem = new POIFSFileSystem(stream);

			// try to extract the text, ignoring any exceptions as metadata extraction may still succeed
			try {
				if (textExtractor != null) {
					text = textExtractor.getText(fileSystem);
				}
			}
			catch (Exception e) {
				// ignore
			}

			PoiUtil.extractMetadata(fileSystem, container);
		}
		catch (IOException e1) {
			// ignore
		}

		// if text extraction was not successfull, try a StringExtractor as a fallback
		if (text == null) {
			if (textExtractor != null) {
				logger.info("regular POI-based processing failed, falling back to heuristic string extraction for "
							+ container.getDescribedUri());
			}

			try {
				stream.reset();
				StringExtractor extractor = new StringExtractor();
				text = extractor.extract(stream);
			}
			catch (IOException e) {
				logger.warn("IOException while processing " + container.getDescribedUri(), e);
			}
		}

		// store the full-text, if any
		if (text != null) {
			text = text.trim();
			if (!text.equals("")) {
				container.add(NIE.plainTextContent, text);
			}
		}
		
		try {
		    stream.reset();
		} catch (Exception e) {
		    // ignore
		}
		
		return stream;
	}

	/**
	 * Returns the buffer size to use when buffering the contents of a document.
	 * 
	 * @param systemProperty The system property that contains the buffer size.
	 * @param defaultSize The default buffer size, in case the system property is not set or does not contain
	 *            a valid value.
	 * @return The specified buffer size to use, or the default size when the indicated system property is not
	 *         set or has an illegal value.
	 */
	private static int getBufferSize() {
		int result = 4 * 1024 * 1024;

		// see if the system property is set
		String property = System.getProperty(BUFFER_SIZE_PROPERTY);
		if (property != null && !property.equals("")) {
		    result = Integer.parseInt(property);
		}

        // make sure it is valid
		if (result < 0) {
		    throw new IllegalArgumentException("Negative buffer sizes not allowed: " + result);
        }

		return result;
	}

	/**
	 * A TextExtractor is a delegate that extracts the full-text from an MS Office document using a
	 * POIFSFileSystem. Implementations typically support specific MS Office document types, such as Word,
	 * Excel and PowerPoint.
	 */
	public static interface TextExtractor {

		/**
		 * Extract the full-text from an MS Office document.
		 * 
		 * @param fileSystem The POIFSFileSystem providing structural access to the MS Office document.
		 * @return A String containing the full-text of the document.
		 * @throws IOException whenever access to the POIFSFileSystem caused an IOException.
		 */
		public String getText(POIFSFileSystem fileSystem) throws IOException;
	}
}
