/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A DomainBoundaries uses UrlPatterns (regular expressions or substrings checks) to determine whether a URL
 * belongs to a DataSource domain or not.
 * 
 * <p>
 * Each DomainBoundaries maintains lists of include and exclude patterns. A URL is matched against these two
 * pattern lists to determine whether it is inside or outside the domain. A URL is inside the domain when it
 * matches at least one of the include patterns but none of the exclude patterns. In case no include patterns
 * are specified, all URLs that don't match any of the exclude patterns are included.
 */
public class DomainBoundaries {

	private ArrayList includePatterns;

	private ArrayList excludePatterns;

	public DomainBoundaries() {
		this(new ArrayList(1), new ArrayList(1));
	}

	public DomainBoundaries(List includePatterns, List excludePatterns) {
		this.includePatterns = new ArrayList(includePatterns);
		this.excludePatterns = new ArrayList(excludePatterns);
	}

	public void addIncludePattern(UrlPattern pattern) {
		includePatterns.add(pattern);
	}

	public boolean removeIncludePattern(UrlPattern pattern) {
		return includePatterns.remove(pattern);
	}

	public void removeAllIncludePatterns() {
		includePatterns.clear();
	}

	/**
	 * @return a read-only version of the internal include-list
	 */
	public List getIncludePatterns() {
		return Collections.unmodifiableList(includePatterns);
	}

	public void setIncludePatterns(List includePatterns) {
		this.includePatterns = new ArrayList(includePatterns);
	}

	public void addExcludePattern(UrlPattern pattern) {
		excludePatterns.add(pattern);
	}

	public boolean removeExcludePattern(UrlPattern pattern) {
		return excludePatterns.remove(pattern);
	}

	public void removeAllExcludePatterns() {
		excludePatterns.clear();
	}

	/**
	 * @return a read-only version of the internal exclude-list
	 */
	public List getExcludePatterns() {
		return Collections.unmodifiableList(excludePatterns);
	}

	public void setExcludePatterns(List excludePatterns) {
		this.excludePatterns = new ArrayList(excludePatterns);
	}

	public void removeAllPatterns() {
		removeAllIncludePatterns();
		removeAllExcludePatterns();
	}

	/**
	 * Checks whether the supplied URL falls inside the specified boundaries.
	 * 
	 * @param url The URL to check.
	 * @return 'true' if the URL is inside the crawl domain, 'false' otherwise.
	 */
	public boolean inDomain(String url) {
		UrlPattern pattern;

		boolean insideDomain = false;

		int nrIncludePatterns = includePatterns.size();
		if (nrIncludePatterns == 0) {
			insideDomain = true;
		}
		else {
			for (int i = 0; i < nrIncludePatterns; i++) {
				pattern = (UrlPattern) includePatterns.get(i);
				if (pattern.matches(url)) {
					insideDomain = true;
					break;
				}
			}
		}

		if (insideDomain) {
			int nrExcludePatterns = excludePatterns.size();
			for (int i = 0; i < nrExcludePatterns; i++) {
				pattern = (UrlPattern) excludePatterns.get(i);
				if (pattern.matches(url)) {
					insideDomain = false;
					break;
				}
			}
		}

		return insideDomain;
	}
}
