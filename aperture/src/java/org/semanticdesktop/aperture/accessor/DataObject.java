/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A general interface for information items obtained from accessing a DataSource. DataObjects typically
 * represent files, folders, web pages, e-mails, etc.
 * 
 * <p>
 * A DataObject consists of an identifier (a URI) and metadata that describes that identifier.
 * Subinterfaces may define methods for accessing additional information, e.g. an InputStream.
 * 
 * <p>
 * Calling the getMetadata() method will retrieve structured data that exists in the realm of the
 * DataSource. Examples include path and file names, sizes, last modification dates, etc.
 */
public interface DataObject {

    /**
     * Gets the DataObject's primary identifier.
     * 
     * @return An identifier for this DataObject.
     */
    public URI getID();

    /**
     * Gets the DataSource from which this DataObject was obtained.
     * 
     * @return The DataSource from which this DataObject originated.
     */
    public DataSource getDataSource();

    /**
     * Get the metadata describing this DataObject's ID.
     * 
     * @return An RDFContainer containing an RDF model formulated using the Aperture vocabulary.
     */
    public RDFContainer getMetadata();
}
