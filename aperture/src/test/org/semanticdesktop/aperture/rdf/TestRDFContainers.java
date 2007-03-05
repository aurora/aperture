/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.semanticdesktop.aperture.rdf.impl.TestRdfContainerImpl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestRDFContainers extends TestSuite {

    public static Test suite() {
        return new TestRDFContainers();
    }
    
    public TestRDFContainers() {
        super("RDF containers");
        
        addTest(new TestSuite(TestRdfContainerImpl.class));
    }
}
