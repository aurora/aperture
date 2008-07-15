/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.impl;

import java.util.Date;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.MultipleValuesException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;

public class TestRdfContainerImpl extends ApertureTestBase {

    public static final String TEST_OBJECT_URI = "urn://test/dataobject";

    public static final String TEST_RESOURCE_URI = "urn://test/objectresource";

    public static final String PROP_BOOL = "http://example.com/ont/bool";

    public static final String PROP_DATE = "http://example.com/ont/date";

    public static final String PROP_INT = "http://example.com/ont/int";

    public static final URI PROP_STRING_URI;

    public static final URI PROP_RESOURCE_URI;

    public static final URI PROP_BOOL_URI;

    public static final URI PROP_DATE_URI;

    public static final URI PROP_INT_URI;

    public static final URI TEST_RESOURCE;

    private Date MYDATE = new Date();

    static {
        PROP_STRING_URI = RDFS.label;
        PROP_RESOURCE_URI = RDFS.seeAlso;
        PROP_BOOL_URI = URIImpl.createURIWithoutChecking(PROP_BOOL);
        PROP_DATE_URI = URIImpl.createURIWithoutChecking(PROP_DATE);
        PROP_INT_URI = URIImpl.createURIWithoutChecking(PROP_INT);
        TEST_RESOURCE = URIImpl.createURIWithoutChecking(TEST_RESOURCE_URI);
    }

    public void testPutBasicTypes() throws Exception {
        RDFContainer container = createRDFContainer(TEST_OBJECT_URI);
        Model model = (Model) container.getModel();
        ValueFactory val = new ValueFactoryImpl(model);
        Resource subject = container.getDescribedUri();

        container.put(PROP_STRING_URI, "label");
        container.put(PROP_BOOL_URI, true);
        container.put(PROP_DATE_URI, MYDATE);
        container.put(PROP_INT_URI, 23);
        container.put(PROP_RESOURCE_URI, TEST_RESOURCE);
        // check
        assertTrue(model.contains(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(model.contains(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(model.contains(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(model.contains(subject, PROP_STRING_URI, val.createLiteral("label")));
        container.dispose();
    }

    public void testPutSemantics() throws ModelException {
        URI subject = URIImpl.createURIWithoutChecking("urn:test:dummy");
        RDFContainer container = createRDFContainer(subject);
        ValueFactory valFac = new ValueFactoryImpl((Model) container.getModel());

        // check whether the current value is propertly overwritten
        assertEquals(null, container.getString(PROP_STRING_URI));

        container.put(PROP_STRING_URI, valFac.createLiteral("label"));
        assertEquals("label", container.getString(PROP_STRING_URI));

        container.put(PROP_STRING_URI, valFac.createLiteral("label2"));
        assertEquals("label2", container.getString(PROP_STRING_URI));

        // check that a MultipleValuesException is thrown when "add" is used instead of "put"
        container.add(PROP_STRING_URI, valFac.createLiteral("label3"));

        // check that you get null back for a missing boolean property
        assertNull(container.getBoolean(PROP_BOOL_URI));

        try {
            container.getString(PROP_STRING_URI);
            fail();
        }
        catch (MultipleValuesException e) {
            // this is the required behaviour
        }

        // check that the put now also throws a MultipleValuesException
        try {
            container.put(PROP_STRING_URI, valFac.createLiteral("label4"));
            fail();
        }
        catch (MultipleValuesException e) {
            // this is the required behaviour
        }
        container.dispose();
    }
}
