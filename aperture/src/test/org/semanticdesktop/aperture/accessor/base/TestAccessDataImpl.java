/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;

public class TestAccessDataImpl extends AccessDataTest {

    public void setUp() throws IOException {
        super.setUp(new AccessDataImpl());
    }
}
