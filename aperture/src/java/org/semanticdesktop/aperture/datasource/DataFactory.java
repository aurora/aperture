/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.datasource;

import java.util.Properties;

import org.semanticdesktop.aperture.access.DataAccessor;
import org.semanticdesktop.aperture.access.HierarchicalAccess;
import org.semanticdesktop.aperture.crawler.DataCrawler;
import org.semanticdesktop.aperture.opener.DataOpener;

/**
 * An object that can create DataSources and related objects to a DataSource,
 * like crawlers and other stuff. The Factory is one important thing in an OSGI
 * package. Each Factory represents one kind of DataSource object, and will only
 * create objects regarding to this kind of DataSource.
 */
public interface DataFactory {

    /**
     * get some Information that can be displayed to a user when the user wants
     * to register new DataSources. usually, you would return a label, an Icon
     * ????, a description. etc TODO: we could also split this in several
     * methods, easier to do. TODO: when we return an RDFMap, mutlilingual
     * information is already there!!! (which may be very cool)
     */
    public Properties getInformation();

    /**
     * create a new, emtpy and unconfigured, DataSource instance of the
     * DataSource class represented.
     */
    public DataSource createDataSource();

    /**
     * create a DataCrawler for this data source instance. The instance has to
     * be configured already
     */
    public DataCrawler createDataCrawler(DataSource source);

    /**
     * create a DataAccessor for this data source instance. The instance has to
     * be configured already, and you should primarily use its getDataObject()
     * function. The DataCrawler may use its own methods to create a
     * DataAccessor
     */
    public DataAccessor createDataAccessor(DataSource source);

    /**
     * create a HierachicalAccess for this data source instance. There are cases
     * when no hierarchical access is supported. then, this method returns null.
     * 
     * @return a HierachicalAccess or null, when HierachicalAccess not supported
     *         by this kind of DataSource
     */
    public HierarchicalAccess createHierachicalAccess(DataSource source);

    /**
     * create a DataOpener for this data source instance. There are cases when
     * DataObjects of this DataSource cannot be opened. then, this method
     * returns null. Then you may get an opener from somewhere else.
     * 
     * @return a HierachicalAccess or null, when data cannot be opened by the
     *         DAtaAccessor
     */
    public DataOpener createDataOpener(DataSource source);

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */