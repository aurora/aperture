/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.iphoto;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:54:48 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/websites/iphoto/iphotoDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#
 */
public class IPHOTODS {

    /** Path to the ontology resource */
    public static final String IPHOTODS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/websites/iphoto/iphotoDataSource.ttl";

    /**
     * Puts the IPHOTODS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getIPHOTODSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(IPHOTODS_RESOURCE_PATH, IPHOTODS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + IPHOTODS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for IPHOTODS */
    public static final URI NS_IPHOTODS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#");
    /**
     * Type: Class <br/>
     * Label: IPhoto Keyword Data Source  <br/>
     * Comment: Describes the IPhoto application.  <br/>
     */
    public static final URI IPhotoKeywordDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#IPhotoKeywordDataSource");
}
