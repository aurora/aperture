/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.semanticdesktop.aperture.extractor.impl.TestExtractorRegistryImpl;
import org.semanticdesktop.aperture.rdf.TestRDFContainerSesame;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestPackage extends TestSuite {

    static public Test suite() {
        return new TestPackage();
    }

    private TestPackage() {
        super("aperture");
        addTest(new TestSuite(TestRDFContainerSesame.class));
        addTest(new TestSuite(TestExtractorRegistryImpl.class));
    }
}
