/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.mbox;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:56:12 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/mbox/mboxDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#
 */
public class MBOXDS {

    /** Path to the ontology resource */
    public static final String MBOXDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/datasource/mbox/mboxDataSource.ttl";

    /**
     * Puts the MBOXDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getMBOXDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(MBOXDS_RESOURCE_PATH, MBOXDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + MBOXDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for MBOXDS */
    public static final URI NS_MBOXDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#");
    /**
     * Type: Class <br/>
     * Label: MBOX File Data Source  <br/>
     * Comment: Describes a mailbox stored in an mbox-format file  <br/>
     */
    public static final URI MboxDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#MboxDataSource");
    /**
     * Type: Property <br/>
     * Label: MBOX Path  <br/>
     * Comment: The path to the mbox file  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#MboxDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI mboxPath = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#mboxPath");
    /**
     * Type: Property <br/>
     * Label: Maximum Depth  <br/>
     * Comment: Maximum depth of the crawl  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#MboxDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#maximumDepth");
    /**
     * Type: Property <br/>
     * Label: Maximum Size  <br/>
     * Comment: Maximum size (in bytes) of the attachments that are to be reported by the crawler  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#MboxDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#long  <br/>
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#maximumSize");
}
