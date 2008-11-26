/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.bibsonomy;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 19 17:40:38 CET 2008
 * input file: src/org/semanticdesktop/aperture/websites/bibsonomy/bibsonomyDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#
 */
public class BIBSONOMYDS {

    /** Path to the ontology resource */
    public static final String BIBSONOMYDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/websites/bibsonomy/bibsonomyDataSource.ttl";

    /**
     * Puts the BIBSONOMYDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getBIBSONOMYDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(BIBSONOMYDS_RESOURCE_PATH, BIBSONOMYDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + BIBSONOMYDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for BIBSONOMYDS */
    public static final URI NS_BIBSONOMYDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#");
    /**
     * Type: Class <br/>
     * Label: Bibsonomy Data Source  <br/>
     * Comment: Describes a bibsonomy account  <br/>
     */
    public static final URI BibsonomyDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#BibsonomyDataSource");
    /**
     * Type: Property <br/>
     * Label: API username  <br/>
     * Comment: The username associated with the API key. Necessary for programmatic access to bibsonomy. It can be different from the name of the account that is to be crawled.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#BibsonomyDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI apiusername = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#apiusername");
    /**
     * Type: Property <br/>
     * Label: API key  <br/>
     * Comment: The API key  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#BibsonomyDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI apikey = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#apikey");
    /**
     * Type: Property <br/>
     * Label: Crawled username  <br/>
     * Comment: The name of the account that is to be crawled  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#BibsonomyDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI crawledusername = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#crawledusername");
}
