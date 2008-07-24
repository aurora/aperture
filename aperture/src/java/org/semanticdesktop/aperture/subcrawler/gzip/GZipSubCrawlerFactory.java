/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

/**
 * Produces GzipSubCrawlers
 */
@SuppressWarnings("unchecked")
public class GZipSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/gzip");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new GZipSubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
