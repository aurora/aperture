/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * A trivial default implementation of FolderDataObject. 
 */
public class FolderDataObjectBase extends DataObjectBase implements FolderDataObject {

    public FolderDataObjectBase() { }
    
    public FolderDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata) {
        super(id, dataSource, metadata);
        // Add RDF type info
        metadata.add(RDF.type, DATA.FolderDataObject);
    }
}
