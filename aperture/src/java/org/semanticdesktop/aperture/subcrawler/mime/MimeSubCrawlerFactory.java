/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

/**
 * A factory for MimeSubCrawlers
 */
public class MimeSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;
	
	public static String MIME_URI_PREFIX = "mime";

	static {
		HashSet set = new HashSet();
		set.add("message/rfc822");
		set.add("message/news");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new MimeSubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}

    public String getUriPrefix() {
        return MIME_URI_PREFIX;
    }
}
