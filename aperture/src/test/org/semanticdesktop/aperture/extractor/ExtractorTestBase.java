/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;

public class ExtractorTestBase extends ApertureTestBase {

    public RDF2GoRDFContainer extract(String resourceName, Extractor extractor) throws ExtractorException, IOException {
        // setup some info
        String uriString = "http://docs-r-us.com/dummy";
        URI id = URIImpl.createURIWithoutChecking(uriString);
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourceName);
        assertNotNull(stream);
        Model model = createModel();
        RDF2GoRDFContainer rdfContainer = new RDF2GoRDFContainer(model,id);

        // apply the extractor
        extractor.extract(id, stream, null, null, rdfContainer);
        stream.close();

        return rdfContainer;
    }
}
