/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.poi.hdf.extractor.WordDocument;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public class WordExtractor implements Extractor {

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		try {
			WordDocument document = new WordDocument(stream);
			StringWriter writer = new StringWriter(64 * 1024);
			document.writeAllText(writer);
			String text = writer.toString().trim();
			if (!text.equals("")) {
				result.put(AccessVocabulary.FULL_TEXT, text);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}
}
