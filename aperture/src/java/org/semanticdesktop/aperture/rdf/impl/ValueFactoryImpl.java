/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.impl;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.rdf.ValueFactory;

/**
 * An implementation of the ValueFactory interface that relays creation of RDF2Go datatypes to a given RDF2Go
 * Model.
 */
public class ValueFactoryImpl implements ValueFactory {

    private Model model;

    public ValueFactoryImpl(Model model) {
        this.model = model;
    }

    public Literal createLiteral(String label) throws ModelException {
        return model.createPlainLiteral(label);
    }

    public Literal createLiteral(String label, URI datatype) throws ModelException {
        return model.createDatatypeLiteral(label, datatype);
    }

    public Literal createLiteral(boolean value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._boolean);
    }

    public Literal createLiteral(long value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._long);
    }

    public Literal createLiteral(int value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._integer);
    }

    public Literal createLiteral(short value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._short);
    }

    public Literal createLiteral(byte value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._byte);
    }

    public Literal createLiteral(double value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._double);
    }

    public Literal createLiteral(float value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._float);
    }

    public Statement createStatement(Resource subject, URI predicate, Node object) {
        return model.createStatement(subject, predicate, object);
    }

    public URI createURI(String uri) throws ModelException {
        return model.createURI(uri);
    }

    public URI createURI(String namespaceUri, String localName) throws ModelException {
        return createURI(namespaceUri + "#" + localName);
    }

    public BlankNode createBlankNode() {
        return model.createBlankNode();
    }
}
