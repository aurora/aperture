/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:55:45 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/outlook/outlookDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#
 */
public class OUTLOOKDS {

    /** Path to the ontology resource */
    public static final String OUTLOOKDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/outlook/outlookDataSource.ttl";

    /**
     * Puts the OUTLOOKDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getOUTLOOKDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(OUTLOOKDS_RESOURCE_PATH, OUTLOOKDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + OUTLOOKDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for OUTLOOKDS */
    public static final URI NS_OUTLOOKDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#");
    /**
     * Type: Class <br/>
     * Label:  Outlook Data Source  <br/>
     * Comment: Configures a data source for extracting mails, contacts and calendar information from a running  Outlook instance  <br/>
     */
    public static final URI OutlookDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#OutlookDataSource");
    /**
     * Type: Property <br/>
     * Label: Root URL  <br/>
     * Comment: URL used as a prefix for URIs of all outlook resources. It should begin with outlook://  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#OutlookDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#rootUrl");
}
