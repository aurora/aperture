/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.PoiUtil.TextExtractor;
import org.semanticdesktop.aperture.rdf.RDFContainer;

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

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		PoiUtil.extractAll(stream, new WordTextExtractor(), result);
	}

	private static class WordTextExtractor implements TextExtractor {
	public String getText(POIFSFileSystem fileSystem) throws IOException {
		HWPFDocument doc = new HWPFDocument(fileSystem);

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

		// return the extracted full-text
		return buffer.toString().trim();
	}
	}
}
