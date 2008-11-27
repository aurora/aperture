/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.visio;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Extractor implementation for MS Visio documents. This implementation uses heuristic string extraction,
 * so results may be imperfect.
 */
public class VisioExtractor implements Extractor {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// do not specify a TextExtractor, PoiUtil will fall-back on using a StringExtractor
		PoiUtil.extractAll(stream, null, result, logger);
        result.add(RDF.type,NFO.VectorImage);
	}
}
