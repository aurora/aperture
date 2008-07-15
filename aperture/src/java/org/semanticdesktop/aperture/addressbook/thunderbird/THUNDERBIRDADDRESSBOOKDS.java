/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:56:02 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/addressbook/thunderbird/ThunderbirdAddressbookDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#
 */
public class THUNDERBIRDADDRESSBOOKDS {

    /** Path to the ontology resource */
    public static final String THUNDERBIRDADDRESSBOOKDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/addressbook/thunderbird/ThunderbirdAddressbookDataSource.ttl";

    /**
     * Puts the THUNDERBIRDADDRESSBOOKDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getTHUNDERBIRDADDRESSBOOKDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(THUNDERBIRDADDRESSBOOKDS_RESOURCE_PATH, THUNDERBIRDADDRESSBOOKDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + THUNDERBIRDADDRESSBOOKDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for THUNDERBIRDADDRESSBOOKDS */
    public static final URI NS_THUNDERBIRDADDRESSBOOKDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#");
    /**
     * Type: Class <br/>
     * Label: Thunderbird Addresbook Data Source  <br/>
     * Comment: Describes an addresbook maintained by Mozilla Thunderbird  <br/>
     */
    public static final URI ThunderbirdAddressbookDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#ThunderbirdAddressbookDataSource");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#Format <br/>
     * Label: Addresbook Path  <br/>
     * Comment: Path to the file where the addresbook is stored.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#ThunderbirdAddressbookDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI thunderbirdAddressbookPath = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#thunderbirdAddressbookPath");
}
