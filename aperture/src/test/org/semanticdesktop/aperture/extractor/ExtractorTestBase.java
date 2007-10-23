/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class ExtractorTestBase extends ApertureTestBase {

    public RDFContainer extract(String resourceName, Extractor extractor) throws ExtractorException,
            IOException {
        // setup some info
        String uriString = "http://docs-r-us.com/dummy";
        URI id = URIImpl.createURIWithoutChecking(uriString);

        // create a stream that provides access to the test document
        InputStream stream = ResourceUtil.getInputStream(resourceName, this.getClass());
        assertNotNull(stream);

        // create a container in which the extraction results can be stored
        RDFContainerFactory factory = new RDFContainerFactoryImpl();
        RDFContainer rdfContainer = factory.newInstance(id);

        // apply the extractor
        extractor.extract(id, stream, null, null, rdfContainer);
        stream.close();

        // return the extraction results
        return rdfContainer;
    }
}
