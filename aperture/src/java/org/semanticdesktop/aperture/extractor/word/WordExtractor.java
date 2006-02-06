/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.StringExtractor;

public class WordExtractor implements Extractor {

	// The following code was tested to extract the document text:
	//
	// WordDocument document = new WordDocument(stream);
	// StringWriter writer = new StringWriter(64 * 1024);
	// document.writeAllText(writer);
	// String text = writer.toString().trim();
	//
	// This code was problematic for two reasons:
	// - It crashed on about 25% of the documents (tested on 300 Word docs from arbitrary sources).
	// - It internally creates a PoiFSFileSystem but this is not accessible, making it impossible
	// to retrieve the SummaryInformation without buffering the entire document.
	//
	// The code used below does allow us to retrieve the metadata, while crashing on approx. 5% of the
	// documents. These cases are typically handled very well by the StringExtractor, as the cost of a more
	// sloppy extraction and the loss of the metadata.

	private static final String END_OF_LINE = System.getProperty("line.separator", "\n");

	private static final Logger LOGGER = Logger.getLogger(WordExtractor.class.getName());

	private static final int BUFFER_SIZE;

	static {
		int size = -1;

		String property = System.getProperty("aperture.wordExtractor.bufferSize");
		if (property != null && !property.equals("")) {
			try {
				size = Integer.parseInt(property);
			}
			catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "invalid buffer size: " + property);
			}
		}

		if (size < 0) {
			size = 4 * 1024 * 1024;
		}

		BUFFER_SIZE = size;
	}

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// mark the stream with a sufficiently large buffer so that, when POI chokes on a document, there is a
		// good chance we can reset to the beginning of the buffer and apply a StringExtractor
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, BUFFER_SIZE);
		}
		stream.mark(BUFFER_SIZE);

		// apply the POI-based extraction code
		try {
			applyPoi(stream, result);
		}
		catch (Exception e) {
			// we catch Exception rather than IOException because in our experience POI can sometimes throw
			// NullPointerExceptions, ArrayIndexOOBExceptions, etc., in which case we can still fall-back on a
			// StringExtractor
			LOGGER.log(Level.INFO,
				"Exception while processing Word document, switching to heuristic string extraction: " + id,
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
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(stream);
		HWPFDocument doc = new HWPFDocument(poiFileSystem);

		StringBuffer buffer = new StringBuffer(4096);

		Iterator textPieces = doc.getTextTable().getTextPieces().iterator();
		while (textPieces.hasNext()) {
			TextPiece piece = (TextPiece) textPieces.next();

			// the following is derived from http://article.gmane.org/gmane.comp.jakarta.poi.devel/7406
			String encoding = "Cp1252";
			if (piece.usesUnicode()) {
				encoding = "UTF-16LE";
			}
			buffer.append(new String(piece.getRawBytes(), encoding));
		}

		// normalize end-of-line characters and remove any lines containing macros
		BufferedReader reader = new BufferedReader(new StringReader(buffer.toString()));
		buffer.setLength(0);

		String line;
		while ((line = reader.readLine()) != null) {
			if (line.indexOf("DOCPROPERTY") == -1) {
				buffer.append(line);
				buffer.append(END_OF_LINE);
			}
		}

		// store full-text
		String text = buffer.toString().trim();
		if (!text.equals("")) {
			result.put(AccessVocabulary.FULL_TEXT, text);
		}

		// extract all metadata
		// exceptions occurring here are ignored as we've already successfully handled the
		// full-text: no need to fall-back to the heuristic StringExtractor
		SummaryInformation summary = getSummaryInformation(poiFileSystem);
		if (summary != null) {
			copyString(summary.getTitle(), AccessVocabulary.TITLE, result);
			copyString(summary.getSubject(), AccessVocabulary.SUBJECT, result);
			copyString(summary.getComments(), AccessVocabulary.DESCRIPTION, result);
			copyString(summary.getApplicationName(), AccessVocabulary.GENERATOR, result);
			copyString(summary.getAuthor(), AccessVocabulary.CREATOR, result);
			copyString(summary.getLastAuthor(), AccessVocabulary.CREATOR, result);

			copyDate(summary.getCreateDateTime(), AccessVocabulary.CREATION_DATE, result);
			copyDate(summary.getLastSaveDateTime(), AccessVocabulary.DATE, result);

			int nrPages = summary.getPageCount();
			if (nrPages > 1) {
				// '0' means 'unknown' according to POI's API (<sigh>)
				// '1' is often erroneously returned and can thus not be trusted
				// higher values tend to be right (not seen a counter example yet) and are
				// therefore included
				System.out.println(nrPages);
				result.put(AccessVocabulary.PAGE_COUNT, nrPages);
			}

			String keywords = summary.getKeywords();
			if (keywords != null) {
				StringTokenizer tokenizer = new StringTokenizer(keywords, " \t,;", false);
				while (tokenizer.hasMoreTokens()) {
					String keyword = tokenizer.nextToken();
					result.add(AccessVocabulary.KEYWORD, keyword);
				}
			}
		}
	}

	private SummaryInformation getSummaryInformation(POIFSFileSystem poiFileSystem) {
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

	private void copyString(String value, URI property, RDFContainer container) {
		if (value != null) {
			value = value.trim();
			if (!value.equals("")) {
				// NOTE: "add", not "put", as some properties will be used multiple times!!
				container.add(property, value);
			}
		}
	}

	private void copyDate(Date date, URI property, RDFContainer container) {
		if (date != null) {
			container.add(property, date);
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
