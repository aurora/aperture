/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.semanticdesktop.aperture.mime.identifier.impl.TestDefaultMimeTypeIdentifierRegistry;
import org.semanticdesktop.aperture.mime.identifier.magic.TestMagicMimeTypeIdentifier;

public class TestMimeStuff extends TestSuite {

    public static Test suite() {
        return new TestMimeStuff();
    }
    
    public TestMimeStuff() {
        super("mime");
        addTest(new TestSuite(TestMagicMimeTypeIdentifier.class));
        addTest(new TestSuite(TestDefaultMimeTypeIdentifierRegistry.class));
    }
}

