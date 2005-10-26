/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

import java.util.Iterator;

import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * convenience access to the data inside a DataSource. Not all DataSources will
 * support HierachicalAccess, if a DataSource does, this class provides the user
 * a way to see the structure inside the DataSource without having to crawl it
 * completely. A preview to the extracted data can be given using the
 * HierachicalAccess or the hierachical structure can be extracted without
 * having to extract all data.
 */
public interface HierarchicalAccess {

    /**
     * Returns the DataSource on which this HierachicalAccess works.
     */
    public DataSource getDataSource();

    /**
     * get the uri of the root folder of this datasource. This is the first
     * folder to crawl, its subfolders can be retrieved using getSubFolders and
     * then incrementally.
     */
    public String getRootFolder();

    /**
     * get the detailed data of one object, this is costly. If the DataObject is
     * a file, then the InputStream of the file is not converted to metadata
     * yet, use the Extractors assigned to the mime-type. Internally, this uses
     * a suitable DataAccessor to access the DataObject from inside the
     * datasource.
     */
    public DataObject getDataObject(String uri);

    /**
     * List sub-folders of this folder. Iterator contains folder uris as
     * Strings. this may also return the uris of objects, if the objects can
     * contain sub-objects. (IMAP-attachments)-but this is bad as detection of
     * sub-objects of emails is costly. the first call of this method would be
     * with the getRootUri()
     */
    public Iterator listSubFolders(String uri);

    /**
     * List objects inside the passed folder, Iterator contains uris of objects
     * as Strings. To get the metadata and data of the object, use getDataObject
     * with the returned uri.
     */
    public Iterator listSubObjects(String uri);

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */