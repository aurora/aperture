/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.tar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

/**
 * A factory for TarSubCrawlers
 */
public class TarSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;
	
	/** Prefix used for uris of entries inside tar archives */
	public static final String TAR_URI_PREFIX = "tar";

	static {
		HashSet set = new HashSet();
		set.add("application/x-tar");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new TarSubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}

    public String getUriPrefix() {
        return TAR_URI_PREFIX;
    }
}
