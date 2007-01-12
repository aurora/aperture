/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.rdf2go;

import java.util.Properties;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;

/**
 * Creates RDF2GoRDFContainer instances. 
 */
public class RDF2GoRDFContainerFactory implements RDFContainerFactory,
		org.semanticdesktop.aperture.accessor.RDFContainerFactory {
	
	private ModelFactory factory;
	
	public RDF2GoRDFContainerFactory() {
		factory = RDF2Go.getModelFactory();
	}

    public RDFContainer newInstance(String uri) {
    	try {
    		Model newModel = factory.createModel();
        	return new RDF2GoRDFContainer(newModel,uri);
    	} catch (ModelException me) {
    		throw new RuntimeException(me);
    	}
    }
    
    public RDFContainer newInstance(URI uri) {
    	try {
    		Model newModel = factory.createModel();
        	return new RDF2GoRDFContainer(newModel,uri);
    	} catch (ModelException me) {
    		throw new RuntimeException(me);
    	}
    }

	public RDFContainer getRDFContainer(URI uri) {
		return newInstance(uri);
	}
}
