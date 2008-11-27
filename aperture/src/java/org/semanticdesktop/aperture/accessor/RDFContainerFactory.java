/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * An RDFContainerFactory delivers a RDFContainer on-demand to a DataAccessor.
 * 
 * <p>
 * By letting instantiation of an RDFContainer go via a separate factory interface, implementors can
 * decide whether to always return a single shared RDFContainer instance or whether each call returns a
 * new instance, without having to create them upfront in the latter case.
 */
public interface RDFContainerFactory {

    /**
     * Returns a RDFContainer instance. This may be either a shared instance or a new instance.
     * 
     * @return a RDFContainer instance.
     */
    public RDFContainer getRDFContainer(URI uri);
}
