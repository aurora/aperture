/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.powerpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.extractor.util.PoiUtil.TextExtractor;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public class PowerPointExtractor implements Extractor {

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		PoiUtil.extractAll(stream, new PowerPointTextExtractor(), result);
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
