/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.delicious;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:54:55 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/websites/delicious/deliciousDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/deliciousds#
 */
public class DELICIOUSDS {

    /** Path to the ontology resource */
    public static final String DELICIOUSDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/websites/delicious/deliciousDataSource.ttl";

    /**
     * Puts the DELICIOUSDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getDELICIOUSDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(DELICIOUSDS_RESOURCE_PATH, DELICIOUSDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + DELICIOUSDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for DELICIOUSDS */
    public static final URI NS_DELICIOUSDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/deliciousds#");
    /**
     * Type: Class <br/>
     * Label: del.icio.us Data Source  <br/>
     * Comment: Describes a del.icio.us account  <br/>
     */
    public static final URI DeliciousDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/deliciousds#DeliciousDataSource");
}
