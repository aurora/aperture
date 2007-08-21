/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.office;

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
 * An Extractor implementation that can be used to process MS Office documents when we don't know its specific
 * subtype (e.g. Word, Excel, PowerPoint) or when we don't have an Extractor for that particular subtype. This
 * extractor is capable of extracting all metadata but not the textual contents.
 */
public class OfficeExtractor implements Extractor {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// do not specify a TextExtractor, PoiUtil will fall-back on using a StringExtractor
		PoiUtil.extractAll(stream, null, result, logger);
        result.add(RDF.type,NFO.Document);
	}
}
