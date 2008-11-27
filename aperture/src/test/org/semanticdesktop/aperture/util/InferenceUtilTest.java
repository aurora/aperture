/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.util;

import junit.framework.TestCase;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;


/**
 * a simple test to check the basic functionality of the InferenceUtil
 */
public class InferenceUtilTest extends TestCase {
    
    InferenceUtil inf;
    RDFContainer container;
    
    public static final String RESOURCEURISTRING = "urn:testresource";
    public static final URI RESOURCEURI = new URIImpl(RESOURCEURISTRING);
    
    
    protected void setUp() throws Exception {
        super.setUp();
        inf = InferenceUtil.createForCoreOntologies();
        container = new RDFContainerImpl(RDF2Go.getModelFactory().createModel(), RESOURCEURI);
        container.getModel().open();
       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.getModel().close();
    }
    
    public void testNMOSubject() {
        container.add(NMO.messageSubject, "msg subject");
        assertTrue("container has no NIE:subject", 
            container.getString(NIE.subject) == null);
        inf.extendContent(container);
        assertEquals("container has also NIE:subject", "msg subject",
            container.getString(NIE.subject));
    }
    
    public void testTypeInference() {
        container.add(RDF.type, NMO.Email);
        inf.extendContent(container);
        assertTrue("container has also type ", container.getModel().contains(
            container.getDescribedUri(), RDF.type, NIE.InformationElement));
    }

}

