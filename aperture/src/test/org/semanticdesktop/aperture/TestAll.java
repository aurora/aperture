/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.accessor.TestAccessors;
import org.semanticdesktop.aperture.crawler.TestCrawlers;
import org.semanticdesktop.aperture.extractor.TestExtractors;
import org.semanticdesktop.aperture.mime.identifier.magic.TestMagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.TestSesameRDFContainer;

public class TestAll extends TestSuite {

    static public Test suite() {
        return new TestAll();
    }

    private TestAll() {
        super("aperture");
        addTest(new TestSuite(TestSesameRDFContainer.class));
        addTest(TestAccessors.suite());
        addTest(TestCrawlers.suite());
        addTest(TestExtractors.suite());
        addTest(new TestSuite(TestMagicMimeTypeIdentifier.class));
    }
}
