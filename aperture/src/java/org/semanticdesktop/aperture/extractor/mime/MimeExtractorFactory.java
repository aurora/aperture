/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class MimeExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("message/rfc822");
		set.add("message/news");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new MimeExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
