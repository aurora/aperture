/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.model.base;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.model.DataObject;
import org.semanticdesktop.aperture.model.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of DataObject. 
 */
public class DataObjectBase implements DataObject {

    private URI id;
    
    private DataSource dataSource;
    
    private RDFContainer metadata;
    
    public DataObjectBase() { }
    
    public DataObjectBase(URI id, DataSource dataSource, RDFContainer metadata) {
        this.id = id;
        this.dataSource = dataSource;
        this.metadata = metadata;
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
}
