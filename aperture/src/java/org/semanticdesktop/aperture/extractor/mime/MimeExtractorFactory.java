/*
 * Copyright (c) 2005 - 2008 Aduna.
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
import org.semanticdesktop.aperture.subcrawler.mime.MimeSubCrawler;
import org.semanticdesktop.aperture.subcrawler.mime.MimeSubCrawlerFactory;

/**
 * A factory for MimeExtractor instances.
 * 
 * @deprecated Use {@link MimeSubCrawlerFactory} and {@link MimeSubCrawler}s instead
 */
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
