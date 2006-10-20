/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class ExtractorTestBase extends ApertureTestBase {

    public SesameRDFContainer extract(String resourceName, Extractor extractor) throws ExtractorException, IOException {
        // setup some info
        String uriString = "http://docs-r-us.com/dummy";
        URI id = new URIImpl(uriString);
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourceName);
        SesameRDFContainer rdfContainer = new SesameRDFContainer(id);

        // apply the extractor
        extractor.extract(id, stream, null, null, rdfContainer);
        stream.close();

        return rdfContainer;
    }
}
