/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import org.openrdf.model.URI;
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
