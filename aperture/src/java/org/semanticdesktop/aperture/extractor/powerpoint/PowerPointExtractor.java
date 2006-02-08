/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.StringExtractor;

public class PowerPointExtractor implements Extractor {

	private static final Logger LOGGER = Logger.getLogger(PowerPointExtractor.class.getName());

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// mark the stream with a sufficiently large buffer so that, when POI chokes on a document, there is a
		// good chance we can reset to the beginning of the buffer and apply a StringExtractor
		int bufferSize = PoiUtil.getBufferSize("aperture.powerPointExtractor.bufferSize", 4 * 1024 * 1024);
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, bufferSize);
		}
		stream.mark(bufferSize);

		// apply the POI-based extraction code
		try {
			applyPoi(stream, result);
		}
		catch (Exception e) {
			// we catch Exception rather than IOException because in our experience POI can sometimes throw
			// NullPointerExceptions, ArrayIndexOOBExceptions, etc., in which case we can still fall-back on a
			// StringExtractor
			LOGGER.log(Level.INFO,
				"Exception while processing Excel document, switching to heuristic string extraction: " + id,
				e);

			try {
				stream.reset();
				applyStringExtractor(stream, result);
			}
			catch (IOException exc) {
				throw new ExtractorException(exc);
			}
		}
	}

	private void applyPoi(InputStream stream, RDFContainer result) throws IOException {
		// setup a POIFSFileSystem
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(stream);

		// let POI's PowerPointExtractor extract the document text
		org.apache.poi.hslf.extractor.PowerPointExtractor extractor = new org.apache.poi.hslf.extractor.PowerPointExtractor(
				poiFileSystem);

		try {
			// extract the slide text and notes
			String text = extractor.getText(true, true);
			if (text != null) {
				text = text.trim();
				if (!text.equals("")) {
					result.put(AccessVocabulary.FULL_TEXT, text);
				}
			}
			
			// extract the document metadata
			PoiUtil.extractMetadata(poiFileSystem, result);
		}
		finally {
			extractor.close();
		}
	}

	private void applyStringExtractor(InputStream stream, RDFContainer result) throws IOException {
		StringExtractor extractor = new StringExtractor();
		String text = extractor.extract(stream).trim();
		if (!text.equals("")) {
			result.put(AccessVocabulary.FULL_TEXT, text);
		}
	}
}
