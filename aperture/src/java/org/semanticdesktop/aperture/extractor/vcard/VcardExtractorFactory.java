/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.vcard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class VcardExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("text/x-vcard");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new VcardExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
