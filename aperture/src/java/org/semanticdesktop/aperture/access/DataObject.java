/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

import java.net.URI;

import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFMap;

/**
 * A general interface for data objects. A data object consists of an
 * identifier, and metadata. The object is used primarily to extract information
 * from datasources. For the extraction, both the RDF metadata returned by
 * getMetadata() and methods provided by possible sub-classes are important.
 * Note that in applications you will find instances of DataObject and its
 * child-interfaces DataObjectFile and DataObjectFolder. Handling a DataObject,
 * you should always (via type checking using 'instanceof') handle additional
 * information provided by the sub-interfaces.
 * <h3>metadata and data</h3>
 * Calling the getMetadata() method will retrieve structured data that exists in
 * the datasource already, including important metadata like size in bytes, last
 * change date, title, author, subject, etc. The metadata can be also data of
 * the object: when you access a contact from an address book, the whole contact
 * information (including address, tel, etc) will be inside the metadata object.
 * Also, relations to parents and children related to this DataObject may be
 * inside the metadata. Note, that folder-like relations are always returned
 * when the DataObject is an instance of DataObjectFolder, then the metadata
 * will contain links to all children of the folder (including sub-folders and
 * sub-objects)
 */
public interface DataObject {

    /**
     * Gets the data object's primary identifier.
     * 
     * @return An identifier for this data object.
     */
    public URI getID();

    /**
     * Gets the DataSource from which this DataObject conceptually originated.
     * 
     * @return The DataSource from which this DataObject conceptually
     *         originated.
     */
    public DataSource getDataSource();

    /**
     * Get the source-specific metadata and data. The used keys and values and
     * implementation-dependent. For java1.4 compability reasons, the map is
     * untyped. It is already titled RDFMap to reflect our ideas regarding RDF
     * 
     * @return The scheme-specific metadata.
     */
    public RDFMap getMetadata();

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */