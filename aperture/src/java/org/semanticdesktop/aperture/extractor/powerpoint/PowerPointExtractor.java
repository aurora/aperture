/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.PoiUtil.TextExtractor;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerPointExtractor implements Extractor {

    private Logger logger = LoggerFactory.getLogger(getClass()); 
    
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		PoiUtil.extractAll(stream, new PowerPointTextExtractor(), result, logger);
        result.add(RDF.type,NFO.Presentation);
	}

	private static class PowerPointTextExtractor implements TextExtractor {

		public String getText(POIFSFileSystem fileSystem) throws IOException {
			org.apache.poi.hslf.extractor.PowerPointExtractor extractor = new org.apache.poi.hslf.extractor.PowerPointExtractor(
					fileSystem);
			String result = extractor.getText(true, true);
			if (result == null) {
				result = "";
			}
			return result;
		}
	}
}
