/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:55:35 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/web/webDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#
 */
public class WEBDS {

    /** Path to the ontology resource */
    public static final String WEBDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/datasource/web/webDataSource.ttl";

    /**
     * Puts the WEBDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getWEBDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(WEBDS_RESOURCE_PATH, WEBDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + WEBDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for WEBDS */
    public static final URI NS_WEBDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#");
    /**
     * Type: Class <br/>
     * Label: Website Data Source  <br/>
     * Comment: Describes a website  <br/>
     */
    public static final URI WebDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource");
    /**
     * Type: Property <br/>
     * Label: Root URL  <br/>
     * Comment: URL of the webpage were the crawling should begin  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#rootUrl");
    /**
     * Type: Property <br/>
     * Label: Include Embedded Resources  <br/>
     * Comment: Should the embedded resources (images, sounds, flash animations etc.) be included in the crawl results  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI includeEmbeddedResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#includeEmbeddedResources");
    /**
     * Type: Property <br/>
     * Label: Maximum Depth  <br/>
     * Comment: How many levels of link references should the crawler cover.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#maximumDepth");
    /**
     * Type: Property <br/>
     * Label: Maximum Size  <br/>
     * Comment: Maximum size (in bytes) of resources reported by the crawler  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#long  <br/>
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#maximumSize");
}
