/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.rdf;

import org.ontoware.rdf2go.model.node.URI;

/**
 * RDFContainerFactories create new RDFContainers on demand.
 */
public interface RDFContainerFactory {

    /**
     * Create a new empty RDFContainer describing the specified ID.
     * 
     * @param id The URI of the described resource.
     * @return A new empty RDFContainer that will describe the specified ID.
     */
    public RDFContainer newInstance(String id);

    /**
     * Create a new empty RDFContainer describing the specified ID.
     * 
     * @param id The URI of the described resource.
     * @return A new empty RDFContainer that will describe the specified ID.
     */
    public RDFContainer newInstance(URI id);
}
