/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A SubCrawler accesses an InputStream and produces a stream of other DataObjects representing the resources
 * found "inside".
 * 
 * <p>
 * An AccessData instance can optionally be specified to a SubCrawler, allowing it to perform incremental
 * crawling, i.e. to scan and report the differences in the stream since the last crawl.
 * </p>
 * 
 * <p>
 * The uris of the data objects found inside other data objects have a fixed form, consisting of three basic
 * parts:
 * </p>
 * 
 * <pre>
 * &lt;prefix&gt;:&lt;parent-object-uri&gt;!/&lt;path&gt;
 * </pre>
 * 
 * <ul>
 * <li>&lt;prefix&gt; - the uri prefix, characteristic for a particular SubCrawler, returned by the
 * {@link SubCrawlerFactory#getUriPrefix()} method</li>
 * <li>&lt;parent-object-uri&gt; - the uri of the parent data object, it is obtained from the parentMetadata
 * parameter to the {@link #subCrawl} method, by calling {@link RDFContainer#getDescribedUri()}</li>
 * <li>&lt;path&gt; - an internal path of the 'child' data object inside the 'parent' data object</li>
 * </ul>
 * 
 * This scheme has been inspired by the apache commons VFS project, homepaged under <a
 * href="http://commons.apache.org/vfs/">http://commons.apache.org/vfs</a>
 */
public interface SubCrawler {

    /**
     * Starts crawling the given stream and to report the encountered DataObjects to the given
     * SubCrawlerHandler. If an AccessData instance is passed, it is used to check if the data objects are to
     * be reported as new, modified, or unmodified. Note that the SubCrawler will not report deleted objects.
     * 
     * @param id the URI identifying the object (e.g. a file or web page) from which the stream was obtained.
     *            This URI is treated as the URI of the parent object, all objects encountered in the stream
     *            are considered to be contained within the parent object. (optional, the implementation may
     *            use this uri or the one returned from the {@link RDFContainer#getDescribedUri()} method of
     *            the parentMetadata)
     * @param stream the stream to be crawled. (obligatory)
     * @param accessData the AccessData used to determine if the encountered objects are to be returned as
     *            new, modified, unmodified or deleted. Information about new or modified objects is stored
     *            within for use in future crawls. This parameter may be null if this functionality is not
     *            desired, in which case all DataObjects will be reported as new. (optional)
     * @param handler The crawler handler that is to receive the notifications from the SubCrawler
     *            (obligatory)
     * @param dataSource the data source that will be returned by the {@link DataObject#getDataSource()}
     *            method of the returned data objects. Some implementations may require that this reference is
     *            not null and that it contains some particular information
     * @param charset the charset in which the input stream is encoded (optional).
     * @param mimeType the MIME type of the passed stream (optional).
     * @param parentMetadata The 'parent' RDFContainer, that will contain the metadata about the top-level
     *            entity in the stream. A SubCrawler may (in some cases) limit itself to augmenting the
     *            metadata in this RDFContainer without delivering any additional DataObjects. (obligatory)
     * @throws SubCrawlerException if any of the obligatory parameters is null or if any error during the
     *             crawling process occured
     */
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata)
            throws SubCrawlerException;

    /**
     * Get a DataObject from the specified stream with the given path.
     * 
     * @param parentUri the URI of the parent object where the path will be looked for
     * @param path the path of the requested resource
     * @param stream the stream that contains the resource
     * @param dataSource data source that will be returned by the {@link DataObject#getDataSource()} method of
     *            the returned data object. Some implementations may require that this reference is not null
     *            and that it contains some particular information
     * @param charset the charset in which the input stream is encoded (optional).
     * @param mimeType the MIME type of the passed stream (optional).
     * @param factory An RDFContainerFactory that delivers the RDFContainer to which the metadata of the
     *            DataObject should be added. The provided RDFContainer can later be retrieved as the
     *            DataObject's metadata container.
     * @return The DataObject extracted from the given stream with the given path
     * @throws SubCrawlerException if any I/O error occurs
     * @throws PathNotFoundException if the requested path is not found
     */
    public DataObject getDataObject(URI parentUri, String path, InputStream stream, DataSource dataSource, Charset charset,
            String mimeType, RDFContainerFactory factory) throws SubCrawlerException, PathNotFoundException;
    
    /**
     * Stops a running crawl as fast as possible. This method may return before the crawling has actually
     * stopped.
     */
    public void stopSubCrawler();
}
