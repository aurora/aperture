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
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:54:38 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/websites/bibsonomy/bibsonomyDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#
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
    public static final URI NS_BIBSONOMYDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#");
    /**
     * Type: Class <br/>
     * Label: Bibsonomy Data Source  <br/>
     * Comment: Describes a bibsonomy account  <br/>
     */
    public static final URI BibsonomyDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#BibsonomyDataSource");
    /**
     * Type: Class <br/>
     */
    public static final URI CrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#CrawlType");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#CrawlType <br/>
     */
    public static final URI ItemsAndTagsCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#ItemsAndTagsCrawlType");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#CrawlType <br/>
     */
    public static final URI ItemsOnlyCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#ItemsOnlyCrawlType");
    /**
     * Type: Property <br/>
     * Label: Crawl type  <br/>
     * Comment: Type of crawl  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#BibsonomyDataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#CrawlType  <br/>
     */
    public static final URI crawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#crawlType");
}
