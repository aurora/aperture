/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
//import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of FolderDataObject. 
 */
public class FolderDataObjectBase extends DataObjectBase implements FolderDataObject {

    public FolderDataObjectBase(URI id, DataSource dataSource) {
        super(id, dataSource);
    }
    
//
//    public FolderDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata) {
//        super(id, dataSource, metadata);
//    }
}
