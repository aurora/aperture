/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * An Extractor implementation for MS Visio documents. This implementation uses heuristic string extraction,
 * so results may be imperfect.
 */
public class VisioExtractor implements Extractor {

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// do not specify a TextExtractor, PoiUtil will fall-back on using a StringExtractor
		PoiUtil.extractAll(stream, null, result);
	}
}
