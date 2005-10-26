/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.crawler;

import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A DataCrawler accesses the physical source represented by a DataSource and
 * delivers a stream of DataObjects representing the individual items in that
 * source. To access the DataObjects, the DataCrawler itself will use
 * DataAccessor objects.
 */
public interface DataCrawler {

    /**
     * Constants used to indicate why a scan was stopped.
     */
    /**
     * the scan procedure terminated normally
     */
    public static final int EXIT_COMPLETED = 1;

    /**
     * the DataCrawler was requested to abort the scan procedure
     */
    public static final int EXIT_BY_REQUEST = 2;

    /**
     * an error occurred that made further scanning impossible
     */
    public static final int FATAL_EXCEPTION = 3;

    /**
     * Returns the DataSource crawler by this DataCrawler.
     */
    public DataSource getDataSource();

    /**
     * Starts a scan for DataObjects over the configured domain defined in the
     * DataSource. If this is not the first run of this DataCrawler, it will
     * only report the differences with the previous run, unless the previous
     * scan results have been cleared.
     */
    public void scan();

    /**
     * Stops a running scan operation as fast as possible. This method may
     * return before the operation has actually been stopped.
     */
    public void stopScanning();

    /**
     * Clears all stored scan results. Any listeners registered with this data
     * source will be notified of the removal of the data objects. The next call
     * to scan() will again report all data objects in the configured domain.
     */
    public void clearScanResults();

    /**
     * Gets the ScanReport of the last performed scan, or the current scan when
     * a scan is in progress. Returns null when no scan was performed in this
     * session and there is no scan report available from the previous session.
     * 
     * @return The ScanReport of the last session, or null when this is not
     *         available.
     */
    public ScanReport getLastScanReport();

    /**
     * Adds a DataSourceListener to which this data source should report any
     * scanned or cleared data objects.
     * 
     * @param listener
     *            The DataCrawlerListener to add.
     */
    public void addListener(DataCrawlerListener listener);

    /**
     * Removes a DataSourceListener from this data source.
     * 
     * @param listener
     *            The DataCrawlerListener to remove.
     */
    public void removeListener(DataCrawlerListener listener);
}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */