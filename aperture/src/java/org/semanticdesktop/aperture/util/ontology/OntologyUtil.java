/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util.ontology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

public class OntologyUtil {

    private static final String RESOURCE_PACKAGE = OntologyUtil.class.getPackage().getName()
            .replace('.', '/');

    private static final String SOURCE_ONTOLOGY = RESOURCE_PACKAGE + "/source.rdfs";

    private static final String SOURCEFORMAT_ONTOLOGY = RESOURCE_PACKAGE + "/sourceformat.rdfs";

    /**
     * Puts the source ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSourceOntology(Model model) throws Exception {
        readFileFromResource(model, SOURCE_ONTOLOGY, Syntax.RdfXml);
    }

    /**
     * Puts the source ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSourceFormatOntology(Model model) throws Exception {
        readFileFromResource(model, SOURCEFORMAT_ONTOLOGY, Syntax.RdfXml);
    }

    private static void readFileFromResource(Model model, String path, Syntax syntax)
            throws FileNotFoundException, IOException, ModelException {
        InputStream stream = ClassLoader.getSystemResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException("couldn't find resource " + path);
        }
        model.readFrom(stream, syntax);
    }
}
