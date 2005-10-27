/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary defines a comprehensive list of URIs to be used in communication with various Aperture
 * components.
 */
public class Vocabulary {
   
    public static final String NS = "http://aperture.semanticdesktop.org/ontology/data#";
    
    public static final String FULL_TEXT = NS + "full-text";
    public static final String CREATOR = NS + "creator";
    
    static {
        ValueFactory factory = new ValueFactoryImpl();
        FULL_TEXT_URI = factory.createURI(FULL_TEXT);
        CREATOR_URI = factory.createURI(CREATOR);
    }
    
    public static final URI FULL_TEXT_URI;
    public static final URI CREATOR_URI;

    private Vocabulary() {
    	// prevents instantiation of this class
    }
}
