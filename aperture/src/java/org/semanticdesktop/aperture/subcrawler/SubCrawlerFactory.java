/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.util.Set;

/**
 * SubCrawlerFactories create SubCrawlers capable of crawling FileDataObjects of specific mime types.
 */
public interface SubCrawlerFactory {

    /**
     * Returns a set of Strings indicating the MIME types that are supported by the SubCrawler implementation
     * provided by this SubCrawlerFactory.
     * 
     * @return A Set of mime type strings
     */
    public Set getSupportedTypes();

    /**
     * Return a SubCrawler instance.
     * 
     * @return A SubCrawler instance.
     */
    public SubCrawler get();
}
