/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.rdf;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.rdf.impl.TestRdfContainerImpl;

public class TestRDFContainers extends TestSuite {

    public static Test suite() {
        return new TestRDFContainers();
    }
    
    public TestRDFContainers() {
        super("RDF containers");
        
        addTest(new TestSuite(TestRdfContainerImpl.class));
    }
}
