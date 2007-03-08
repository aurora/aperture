/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.PoiUtil.TextExtractor;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public class PowerPointExtractor implements Extractor {

    private static final Logger LOGGER = Logger.getLogger(PowerPointExtractor.class.getName()); 
    
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		PoiUtil.extractAll(stream, new PowerPointTextExtractor(), result, LOGGER);
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
