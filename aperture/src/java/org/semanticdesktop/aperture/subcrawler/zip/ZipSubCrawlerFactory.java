/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

public class ZipSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("application/zip");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new ZipSubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
