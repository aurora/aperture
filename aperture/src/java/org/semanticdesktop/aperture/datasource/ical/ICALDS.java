/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.ical;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:55:15 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/ical/icalDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#
 */
public class ICALDS {

    /** Path to the ontology resource */
    public static final String ICALDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/datasource/ical/icalDataSource.ttl";

    /**
     * Puts the ICALDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getICALDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(ICALDS_RESOURCE_PATH, ICALDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + ICALDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for ICALDS */
    public static final URI NS_ICALDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#");
    /**
     * Type: Class <br/>
     * Label: ICAL Calendar Data Source  <br/>
     * Comment: Describes a calendar stored in file in the iCalendar format  <br/>
     */
    public static final URI IcalDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#IcalDataSource");
    /**
     * Type: Property <br/>
     * Label: Root URL  <br/>
     * Comment: URL of the ical file to be crawled.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#IcalDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#rootUrl");
}
