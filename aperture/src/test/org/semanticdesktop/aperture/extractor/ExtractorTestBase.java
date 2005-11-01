/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

public class ExtractorTestBase extends TestCase {

    public RDFContainerSesame extract(String resourceName, Extractor extractor) throws URISyntaxException,
            ExtractorException, IOException {
        // setup some info
        String uriString = "http://docs-r-us.com/dummy";
        URI id = new URI(uriString);
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourceName);
        RDFContainerSesame rdfContainer = new RDFContainerSesame(id);

        // apply the extractor
        extractor.extract(id, stream, null, null, rdfContainer);
        stream.close();

        return rdfContainer;
    }
}
