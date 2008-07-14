/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;

/**
 * Tests the XMLSafeModel internal returned by the {@link XmlSafetyUtils#wrapXmlSafeModel(Model)}
 */
public class XmlSafeModelTests extends ApertureTestBase {
    
    private Model model;
    
    public void setUp() {
        Model newModel = RDF2Go.getModelFactory().createModel();
        newModel.open();
        this.model = XmlSafetyUtils.wrapXmlSafeModel(newModel);
    }
    
    /**
     * Checks if the model created by a test is actually XML-friendly, i.e. deserializes it
     * into RDF/XML and tries to read it again.
     */
    public void tearDown() throws Exception {
        // every model is supposed to produce valid RDF/XML
        
        testXmlSafety(this.model);
        
        this.model.close();
        this.model = null;
    }

    /**
     * Tests the addition of a single statement
     * @throws Exception
     */
    public void testAddStatement() throws Exception {
        Statement s1 = new StatementImpl(
            null,
            new URIImpl("urn:test1"),
            new URIImpl("urn:prop"),
            new PlainLiteralImpl("A correct String"));
        Statement s2 = new StatementImpl(
            null,
            new URIImpl("urn:test2"),
            new URIImpl("urn:prop"),
            new PlainLiteralImpl("A \u0002faulty \u0019\u0003String"));
        model.addStatement(s1);
        model.addStatement(s2);
        assertSingleValueProperty(model, new URIImpl("urn:test1"), new URIImpl("urn:prop"),"A correct String");
        assertSingleValueProperty(model, new URIImpl("urn:test2"), new URIImpl("urn:prop"), "A  faulty   String");
        assertEquals(2,model.size());
    }
    
    /**
     * Tries to add four statements, two with invalid objects, two with correct objects
     * @throws Exception
     */
    public void testAddAllStatements() throws Exception {
        Statement s1 = new StatementImpl(
            null,
            new URIImpl("urn:test1"),
            new URIImpl("urn:prop"),
            new PlainLiteralImpl("A correct String"));
        Statement s2 = new StatementImpl(
            null,
            new URIImpl("urn:test2"),
            new URIImpl("urn:prop"),
            new PlainLiteralImpl("A \u0002faulty \u0019\u0003String"));
        Statement s3 = new StatementImpl(
            null,
            new URIImpl("urn:test3"),
            new URIImpl("urn:prop"),
            new DatatypeLiteralImpl("A \u0005wrong dtstring",XSD._string));
        Statement s4 = new StatementImpl(
            null,
            new URIImpl("urn:test4"),
            new URIImpl("urn:prop"),
            model.createBlankNode());
        List<Statement> list = new LinkedList<Statement>();
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        
        model.addAll(list.iterator());
        
        assertSingleValueProperty(model, new URIImpl("urn:test1"), new URIImpl("urn:prop"), "A correct String");
        assertSingleValueProperty(model, new URIImpl("urn:test2"), new URIImpl("urn:prop"), "A  faulty   String");
        assertSingleValueProperty(model, new URIImpl("urn:test3"), new URIImpl("urn:prop"), "A  wrong dtstring");
        
        ClosableIterator<Statement> it = model.findStatements(new URIImpl("urn:test4"),new URIImpl("urn:prop"), Variable.ANY);
        Statement stmt = it.next();
        assertFalse(it.hasNext());
        assertEquals(new URIImpl("urn:test4"), stmt.getSubject());
        assertEquals(new URIImpl("urn:prop"), stmt.getPredicate());
        assertTrue(stmt.getObject() instanceof BlankNode);
        assertEquals(4, model.size());
    }
}

