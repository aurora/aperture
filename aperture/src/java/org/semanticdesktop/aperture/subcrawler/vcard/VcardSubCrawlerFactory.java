/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

public class VcardSubCrawlerFactory implements SubCrawlerFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("text/x-vcard");

		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public SubCrawler get() {
		return new VcardSubCrawler();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}
