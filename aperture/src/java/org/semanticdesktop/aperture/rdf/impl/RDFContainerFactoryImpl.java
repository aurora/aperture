/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.impl;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;

/**
 * Creates RDFContainerImpl instances. The Model wrapped in these RDFContainerImpls are created by the
 * ModelFactory returned by RDF2Go.getModelFactory.
 */
public class RDFContainerFactoryImpl implements RDFContainerFactory,
        org.semanticdesktop.aperture.accessor.RDFContainerFactory {

    private ModelFactory factory;

    public RDFContainerFactoryImpl() {
        factory = RDF2Go.getModelFactory();
    }

    public RDFContainerImpl newInstance(String uri) {
        try {
            Model newModel = factory.createModel();
            return new RDFContainerImpl(newModel, uri);
        }
        catch (ModelRuntimeException me) {
            throw new RuntimeException(me);
        }
    }

    public RDFContainerImpl newInstance(URI uri) {
        try {
            Model newModel = factory.createModel();
            return new RDFContainerImpl(newModel, uri);
        }
        catch (ModelRuntimeException me) {
            throw new RuntimeException(me);
        }
    }

    public RDFContainerImpl getRDFContainer(URI uri) {
        return newInstance(uri);
    }
}
