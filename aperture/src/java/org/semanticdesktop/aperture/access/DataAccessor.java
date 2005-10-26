/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

import java.io.IOException;
import java.util.Map;

import org.semanticdesktop.aperture.crawler.CrawlData;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A DataAccessor provides access to physical resources by creating DataObjects
 * representing the resource, based on a url and optionally data about a
 * previous access and other parameters. The main task of a DataAccessor is to
 * find the resource identified by the URL String and create a DataObject that
 * represents the resource. When crawling, the DataAccessor additionally uses
 * the passed CrawlData interface to check and update information about the last
 * crawl. About the returned DataObject: i n most cases, the DataObject is just
 * a passive container of information, the DataAccessor will have filled it with
 * information. However, it may also have returned a dedicated DataObject
 * implementation that determines some things dynamically, that is up to the
 * DataAccessor to decide. During one call, the DataAccessor has the following
 * tasks:
 * <ul>
 * <li>check if the URL is ok
 * <li> check redirects: if the URL is redirected to antother URI, the
 * DataObject will have the new URI as identifier
 * <li>check changes (was the object changed since last crawl); only needed in
 * getCrawl()
 * <li> if crawling: update the CrawlData with new datetime, size, etc.
 * <li>detect if the DataObject is going to be a DataObject, DataObjectFile or
 * DataObjectFolder. go on accordingly.
 * <li>open the stream
 * <li>detect mime-type (using all tricks available: http headers, file
 * extensions, magic bytes)
 * <li>detect byte-size
 * <li>extract the most basic metadata (only the data that is there already)
 * <li>create a new DataObject with all of the above - and return it
 * </ul>
 */
public interface DataAccessor {

    /**
     * Get a DataObject for the specified url during crawling. The resulting
     * DataObject's ID may differ from the specified url due to normalization
     * schemes, following of redirected URLs, etc.
     * 
     * An AccessData instance has to be specified with which the DataAccessor
     * has to store and retrieve information about previous accesses to
     * resources. This is mostly useful for DataCrawlers who want to be able to
     * incrementally scan a DataSource. The resulting DataObject can be null,
     * indicating that the binary resource has not been modified since the last
     * access.
     * 
     * A DataAccessor is always required to store something in the AccessData
     * when a url is accessed, so that afterwards AccessData.isKnownId will
     * return true.
     * 
     * Specific DataAccessor implementations may accept additional parameters
     * through the params Map.
     * 
     * @param url
     *            The url locator of the resource. If the resource is identified
     *            by some other URI, then the DataAccessor will follow redirects
     *            accordingly.
     * @param dataSource
     *            The source that will be registered as the source of the
     *            DataObject.
     * @param crawlData
     *            database containing information about previous accesses and
     *            where this access is stored.
     * @param params
     *            Optional additional parameters needed to access the physical
     *            resource. also, parameters may be passed that determine how
     *            the metadata should be extracted or which detail of metadata
     *            is needed. Applications may pass params through the whole
     *            chain.
     * @return A DataObject for the specified URI, or null when the binary
     *         resource has not been modified since the last access.
     * @throws UrlNotFoundException
     *             when the binary resource could not be found
     * @throws IOException
     *             When any other kind of I/O error occurs.
     */
    public DataObject getDataObjectCrawl(String url, DataSource source,
            CrawlData crawlData, Map params) throws UrlNotFoundException,
            IOException;

    /**
     * Get a DataObject for the specified url. The resulting DataObject's ID may
     * differ from the specified url due to normalization schemes, following of
     * redirected URLs, etc. This method is independent from access during
     * crawling sessions.
     * 
     * Specific DataAccessor implementations may accept additional parameters
     * through the params Map.
     * 
     * @param url
     *            The url locator of the resource. If the resource is identified
     *            by some other URI, then the DataAccessor will follow redirects
     *            accordingly.
     * @param dataSource
     *            The source that will be registered as the source of the
     *            DataObject.
     * @param params
     *            Optional additional parameters needed to access the physical
     *            resource. also, parameters may be passed that determine how
     *            the metadata should be extracted or which detail of metadata
     *            is needed. Applications may pass params through the whole
     *            chain.
     * @return A DataObject for the specified URI
     * @throws UrlNotFoundException
     *             when the binary resource could not be found
     * @throws IOException
     *             When any other kind of I/O error occurs.
     */
    public DataObject getDataObject(String url, DataSource source, Map params)
            throws UrlNotFoundException, IOException;

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */