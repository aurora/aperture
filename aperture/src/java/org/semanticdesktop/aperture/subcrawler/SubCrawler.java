/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.io.InputStream;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A SubCrawler accesses an InputStream and produces a stream of other DataObjects representing the resources
 * found "inside".
 * 
 * <p>
 * An AccessData instance can optionally be specified to a SubCrawler, allowing it to perform incremental
 * crawling, i.e. to scan and report the differences in the stream since the last crawl.
 */
public interface SubCrawler {

    /**
     * Starts crawling the given stream and to report the encountered DataObjects to the given
     * SubCrawlerHandler. If an AccessData instance is passed, it is used to check if the data objects are to
     * be reported as new, modified, or unmodified. Note that the SubCrawler will not report deleted objects.
     * 
     * @param stream the stream to be crawled.
     * @param accessData the AccessData used to determine if the encountered objects are to be returned as
     *            new, modified, unmodified or deleted. Information about new or modified objects is stored
     *            within for use in future crawls. This parameter may be null if this functionality is not
     *            desired, in which case all DataObjects will be reported as new.
     * @param handler The crawler handler that is to receive the notifications from the SubCrawler
     * @param parentMetadata The 'parent' RDFContainer, that will contain the metadata about the top-level
     *            entity in the stream. A SubCrawler may (in some cases) limit itself to augmenting the
     *            metadata in this RDFContainer without delivering any additional DataObjects.
     */
    public void subCrawl(InputStream stream, AccessData accessData, SubCrawlerHandler handler,
            RDFContainer parentMetadata) throws SubCrawlerException;

    /**
     * Stops a running crawl as fast as possible. This method may return before the crawling has actually
     * stopped.
     */
    public void stopSubCrawler();
}
