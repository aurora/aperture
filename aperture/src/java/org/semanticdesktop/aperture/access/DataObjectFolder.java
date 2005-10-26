/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

import java.util.Iterator;

import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A general interface for folder data objects. Folders are important for
 * structured datasources. They will also contain all the links from the folder
 * to its containing children (using the appropriate relating property) in its
 * getMetadata() RDF Map. So you can get the identifiers of children either
 * using hte getChildren() method or via getMetadata() where all the children
 * are also listed. Listing the relations between folders and their children is
 * very useful for large search databases, that can make use of the semantic
 * structure.
 * 
 */
public interface DataObjectFolder {

    /**
     * Gets the data object's parent, if any.
     * 
     * @return the parent DataObject, or null when this DataObject has no
     *         parent.
     */
    public DataObject getParent();

    /**
     * Gets the data object's children, if any. This may be null to indicate
     * that there are no children.
     * 
     * @return an Iterator over DataObject objects
     */
    public Iterator getChildren();

    /**
     * Get the source-specific metadata and data. The used keys and values and
     * implementation-dependent. For java1.4 compability reasons, the map is
     * untyped. It is already titled RDFMap to reflect our ideas regarding RDF
     * 
     * @return The scheme-specific metadata.
     */
    public RDFContainer getMetadata();

}

/*
 * $Log$
 * Revision 1.2  2005/10/26 14:08:59  leo_sauermann
 * added the sesame-model and began with RDFContainer
 *
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */