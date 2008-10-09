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

/**
 * A factory for {@link ZipSubCrawler} instances
 */
public class ZipSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;
	
	/** Prefix used for uris of entries inside zip archives */
    public static final String ZIP_URI_PREFIX = "zip";

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

    public String getUriPrefix() {
        return ZIP_URI_PREFIX;
    }
}
