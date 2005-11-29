/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.semanticdesktop.aperture.datasource.impl.TestDefaultDataSourceRegistry;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests all DataSource implementations and related classes.
 */
public class TestDataSources extends TestSuite {

    public static Test suite() {
        return new TestDataSources();
    }
    
    private TestDataSources() {
        super("crawlers");
        addTest(new TestSuite(TestDefaultDataSourceRegistry.class));
    }
}
