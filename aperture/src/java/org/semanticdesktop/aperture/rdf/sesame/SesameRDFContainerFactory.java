/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;

/**
 * Creates SesameRDFContainer instances. 
 */
public class SesameRDFContainerFactory implements RDFContainerFactory, org.semanticdesktop.aperture.accessor.RDFContainerFactory {

    public RDFContainer newInstance(String uri) {
        return new SesameRDFContainer(uri);
    }
    
    public RDFContainer newInstance(URI uri) {
        return new SesameRDFContainer(uri);
    }

	public RDFContainer getRDFContainer(URI uri) {
		return new SesameRDFContainer(uri);
	}
	
	public static final SesameRDFContainerFactory DEFAULTFACTORY = new SesameRDFContainerFactory();
}
