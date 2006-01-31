/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class RtfExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES = Collections.singleton("text/rtf");

	public RtfExtractor extractor;

	public Extractor get() {
		if (extractor == null) {
			extractor = new RtfExtractor();
		}
		return extractor;
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
