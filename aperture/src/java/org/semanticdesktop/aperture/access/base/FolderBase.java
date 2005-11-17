/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.access.base;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.access.Folder;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of Folder. 
 */
public class FolderBase extends DataObjectBase implements Folder {

    public FolderBase(URI id, DataSource dataSource, RDFContainer metadata) {
        super(id, dataSource, metadata);
    }
}
