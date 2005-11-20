/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.InputStream;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of FileDataObject. 
 */
public class FileDataObjectBase extends DataObjectBase implements FileDataObject {

    private InputStream content;
    
    public FileDataObjectBase() { }
    
    public FileDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, InputStream content) {
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
