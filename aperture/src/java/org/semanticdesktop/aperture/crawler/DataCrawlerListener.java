/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.crawler;

import java.net.URI;

import org.semanticdesktop.aperture.access.DataObject;

/**
 * A listener for events from a DataCrawler. The listener is responsible to
 * store the DataObjects in the database of choice. DataCrawlerListener are
 * implemented by application developers using Aperture to extract data. The
 * information whether an object is new, changed, deleted or unchanged is
 * detected by the crawler.
 */
public interface DataCrawlerListener {

    /**
     * Notification that the specified DataCrawler has started scanning its
     * domain for data objects.
     * 
     * @param dataSource
     *            The concerning data source.
     */
    public void scanStarted(DataCrawler dataCrawler);

    /**
     * Notification that the specified DataCrawler has stopped scanning its
     * DataSource for DataObjects. The crawler might have completed scanning its
     * domain, it might have been requested to stop by someone or it might have
     * stopped because of a fatal exception.
     * 
     * @param dataSource
     *            The concerning DataCrawler.
     * @param exitCode
     *            The status with which the scan method stopped.
     */
    public void scanStopped(DataCrawler dataCrawler,
            int exitCode);

    /**
     * Notification that the DataCrawler is going to start scanning the
     * specified data object.
     * 
     * @param url
     *            The url of the data object that is going to be scanned.
     */
    public void scanningObject(DataCrawler dataCrawler, URI uri);

    /**
     * Notification that the DataCrawler has found a new DataObject in its scan
     * domain.
     * 
     * @param dataObject
     *            The DataObject that has been constructed.
     */
    public void objectNew(DataCrawler dataCrawler, DataObject dataObject);

    /**
     * Notification that the DataCrawler has found a changed DataObject in its
     * scan domain.
     * 
     * @param dataObject
     *            The DataObject that has been constructed.
     * @param newObject
     *            Flag indicating whether the scanned object is new (true) or
     *            that it has been scanned before but has changed (false).
     */
    public void objectChanged(DataCrawler dataCrawler, DataObject dataObject);

    /**
     * Notification that the DataCrawler has found a data object that has not
     * been modified since the previous scan.
     * 
     * @param url
     *            The url of the unmodified data object.
     */
    public void objectNotModified(DataCrawler dataCrawler, URI uri);

    /**
     * Notification that the data object with the specified url that was found
     * in an earlier scan could no longer be found in this scan. This may
     * indicate that the physical resource no longer exists or that it now falls
     * outside the defined domain.
     * 
     * @param id
     *            The id of the data object that could no longer be found.
     */
    public void objectRemoved(DataCrawler dataCrawler, URI uri);

    /**
     * Notification that the specified DataCrawler has started clearing its scan
     * results cache.
     * 
     * @param dataSource
     *            The concerning DataCrawler.
     */
    public void clearStarted(DataCrawler dataCrawler);

    /**
     * Notification that the specified DataCrawler has finished clearing its
     * scan results cache.
     * 
     * @param dataSource
     *            The concerning DataCrawler.
     */
    public void clearFinished(DataCrawler dataCrawler);

    /**
     * Notification that the data object with the specified url is going to be
     * removed from the DataCrawler's scan results cache. This method is
     * typically called at the start of a full rescan.
     */
    public void clearingObject(DataCrawler dataCrawler, URI url);
}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */