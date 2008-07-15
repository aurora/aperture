/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.publisher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.StringExtractor;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * An Extractor implementation for MS Publisher documents. This implementation uses heuristic string
 * extraction, so results may be imperfect.
 */
public class PublisherExtractor implements Extractor {

	private static final String[] EXCLUDE_LINES = { "fdpc", "syid", "syidz", "chnkink", "btep", "btec",
			"font", "fontj", "mcld", "ontd", "quill96 story group class" };

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// perform metadata extraction
		try {
			stream = PoiUtil.extractMetadata(stream, true, result);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}

		// perform full text extraction
		FullTextExtractor extractor = new FullTextExtractor();
		try {
			String text = extractor.extract(stream).trim();
			if (text.length() > 0) {
				result.add(NIE.plainTextContent, text);
			}
            result.add(RDF.type,NFO.TextDocument);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private static class FullTextExtractor extends StringExtractor {

		protected boolean isValidLine(String lineLowerCase) {
			for (int i = 0; i < EXCLUDE_LINES.length; i++) {
				if (lineLowerCase.equals(EXCLUDE_LINES[i])) {
					return false;
				}
			}

			return super.isValidLine(lineLowerCase);
		}
	}
}
