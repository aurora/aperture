/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.model.base;

import java.io.InputStream;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.access.datasource.DataSource;
import org.semanticdesktop.aperture.model.BinaryObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of BinaryObject. 
 */
public class BinaryObjectBase extends DataObjectBase implements BinaryObject {

    private InputStream content;
    
    public BinaryObjectBase() { }
    
    public BinaryObjectBase(URI id, DataSource dataSource, RDFContainer metadata, InputStream content) {
        super(id, dataSource, metadata);
        this.content = content;
    }
    
    public void setContent(InputStream content) {
        this.content = content;
    }
    
    public InputStream getContent() {
        return content;
    }
}
