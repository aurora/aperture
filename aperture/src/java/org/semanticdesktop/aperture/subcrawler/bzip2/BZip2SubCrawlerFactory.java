/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.bzip2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

/**
 * Produces BZip2SubCrawler instances
 */
@SuppressWarnings("unchecked")
public class BZip2SubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;
	
	/** Prefix used for uris of entries inside bzip2 archives */
    public static final String BZIP2_URI_PREFIX = "bzip2";

	static {
		HashSet set = new HashSet();
		set.add("application/bzip2");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new BZip2SubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}

    public String getUriPrefix() {
        return BZIP2_URI_PREFIX;
    }
}
