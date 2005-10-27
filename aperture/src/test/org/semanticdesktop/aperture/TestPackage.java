/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.extractor.TestExtractors;
import org.semanticdesktop.aperture.rdf.TestRDFContainerSesame;

public class TestPackage extends TestSuite {

    static public Test suite() {
        return new TestPackage();
    }

    private TestPackage() {
        super("aperture");
        addTest(new TestSuite(TestRDFContainerSesame.class));
        addTest(TestExtractors.suite());
    }
}
