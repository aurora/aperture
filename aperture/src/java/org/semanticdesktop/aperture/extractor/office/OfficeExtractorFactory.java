/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.office;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class OfficeExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES = Collections.singleton("application/vnd.ms-office");

	public Extractor get() {
		return new OfficeExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
