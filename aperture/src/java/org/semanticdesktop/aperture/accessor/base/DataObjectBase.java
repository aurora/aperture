/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A trivial default implementation of DataObject.
 */
public class DataObjectBase implements DataObject {

    private URI id;

    private DataSource dataSource;

    private RDFContainer metadata;

    private boolean disposed;

    public DataObjectBase() {}

    public DataObjectBase(URI id, DataSource dataSource, RDFContainer metadata) {
        this.id = id;
        this.dataSource = dataSource;
        this.metadata = metadata;
        disposed = false;
        metadata.add(RDF.type, NIE.DataObject);
    }

    public void finalize() throws Throwable {
        // if the object is garbage-collected without having been disposed, this probably signals an
        // implementation error on the side of the integrator. Issue a warning, as especially in the case
        // of FileDataObjectBase this may lead to unclosed streams. This is a trick learned from PDFBox.
        if (!disposed) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("DataObject has not been disposed. URI: " + id + " From DataSource: "
                    + dataSource.getName() + " [" + dataSource.getID() + "]");

            // try to dispose of it now
            try {
                dispose();
            }
            catch (Throwable t) {
                logger.error("Error while disposing DataObject", t);
            }
        }

        super.finalize();
    }

    public void setID(URI id) {
        this.id = id;
    }

    public URI getID() {
        return id;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setMetadata(RDFContainer metadata) {
        this.metadata = metadata;
    }

    public RDFContainer getMetadata() {
        return metadata;
    }

    public void dispose() {
        disposed = true;
        // Added after discussion on the aperture-devel mailing list. 10.11.2006
        metadata.dispose();
    }
}
