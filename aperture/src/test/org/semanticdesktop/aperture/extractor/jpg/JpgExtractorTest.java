/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.jpg;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;

import com.drew.imaging.jpeg.JpegProcessingException;

public class JpgExtractorTest extends ExtractorTestBase {

    public void testExtraction() throws ExtractorException, IOException, ModelException {
        // apply the extractor on an example file
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-exif-img_9367.JPG", extractor);

        // check the extraction results
        checkStatement(NEXIF.width, "100", container);
        checkStatement(NEXIF.height, "67", container);
        checkStatement(NEXIF.flash, "16", container);
        validate(container);
        container.dispose();
    }

    public void testZeroLength() throws ExtractorException, IOException {
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-exif-zerolength.jpg", extractor);
        validate(container);
        container.dispose();
    }
    
    public void testGeoTagged() throws ExtractorException, IOException {
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-geotagged.jpg", extractor);
        
        URI point = container.getURI(NEXIF.gps);
        Model model = container.getModel();
        assertTrue(model.contains(point,RDF.type,GEO.Point));
        assertTrue(model.contains(point,GEO.long_,"13.37523758"));
        assertTrue(model.contains(point,GEO.lat,"52.51860058"));
        validate(container);
        container.dispose();
    }
    
    public void testGeoTaggedBuenosAires() throws ExtractorException, IOException {
        ExtractorFactory factory = new JpgExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "jpg-geotagged-ipanema.jpg", extractor);
        
        URI point = container.getURI(NEXIF.gps);
        Model model = container.getModel();
        assertTrue(model.contains(point,RDF.type,GEO.Point));
        assertTrue(model.contains(point,GEO.long_,"-43.20515156"));
        assertTrue(model.contains(point,GEO.lat,"-22.98725664"));
        validate(container);
        container.dispose();
    }
}
