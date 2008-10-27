/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.email;

import java.io.IOException;

import junit.framework.TestCase;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;


public class EmailOpenerTest extends TestCase {
    
    public void testWrongURI() throws IOException{
        EmailOpener eo = new EmailOpener();
        URI uri = new URIImpl("msgids://AC033689-6C41-4AD5-96AD-35ABB880E5CC@gmail.com");
        try{
            eo.open(uri );
            fail("Exception not thrown");
        }catch(Exception e){
        }
    }
    public void testOpen() throws IOException{
        EmailOpener eo = new EmailOpener();
        URI uri = new URIImpl("msgid://AC033689-6C41-4AD5-96AD-35ABB880E5CC@gmail.com");
        eo.open(uri );
    }
}

