/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.List;
import java.util.Vector;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;

/**
 * This class contains utility methods that facilitate the generation of various values from an RDF2Go model.
 * The interface is deliberately similar to that of a ValueFactory, but this class can be used in situations
 * when the code has to work directly with a model, without it being wrapped in an RDFContainer.
 */
public class ModelUtil {

    public static Literal createLiteral(Model model, String label) throws ModelException {
        return model.createPlainLiteral(label);
    }

    public static Literal createLiteral(Model model, String label, URI datatype) throws ModelException {
        return model.createDatatypeLiteral(label, datatype);
    }

    public static Literal createLiteral(Model model, boolean value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._boolean);
    }

    public static Literal createLiteral(Model model, long value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._long);
    }

    public static Literal createLiteral(Model model, int value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._integer);
    }

    public static Literal createLiteral(Model model, short value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._short);
    }

    public static Literal createLiteral(Model model, byte value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._byte);
    }

    public static Literal createLiteral(Model model, double value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._double);
    }

    public static Literal createLiteral(Model model, float value) throws ModelException {
        return model.createDatatypeLiteral(String.valueOf(value), XSD._float);
    }

    public static Statement createStatement(Model model, Resource subject, URI predicate, Node object) {
        return model.createStatement(subject, predicate, object);
    }

    public static URI createURI(Model model, String uri) throws ModelException {
        return model.createURI(uri);
    }

    public static URI createURI(Model model, String namespaceUri, String localName) throws ModelException {
        return createURI(model, namespaceUri + "#" + localName);
    }

    public static BlankNode createBlankNode(Model model) {
        return model.createBlankNode();
    }

    /**
     * Returns the Concise Bounded Description of a RDF Resource in a given Model.
     * 
     * @param node The starting node.
     * @param model The model holding the RDF Graph.
     * @param backwards Indicates whether to traverse nodes backwards (null,null,RESOURCE).
     * @return A List of Statements.
     * @throws ModelException Whenever access to the Model throws a ModelException.
     * 
     * @see <a href="http://www.w3.org/Submission/CBD/">W3C CBD Spec</a>
     */
    public static List<Statement> getCBD(Resource node, Model model, boolean backwards) throws ModelException {
        List<Statement> res;
        res = new Vector<Statement>();
        ClosableIterator<? extends Statement> i = null;
        try {
            i = model.findStatements(node, Variable.ANY, Variable.ANY);
            while (i.hasNext()) {
                Statement s = i.next();
                res.add(s);
                if (s.getObject() instanceof BlankNode)
                    res.addAll(getCBD((Resource) s.getObject(), model, backwards));
            }
            i.close();

            if (backwards) {
                i = model.findStatements(Variable.ANY, Variable.ANY, node);
                while (i.hasNext()) {
                    Statement s = i.next();
                    res.add(s);
                    if (s.getSubject() instanceof BlankNode)
                        res.addAll(getCBD((Resource) s.getObject(), model, backwards));
                }
                i.close();
            }
        }
        finally {
            if (i != null) {
                i.close();
            }
        }

        return res;
    }

    public static boolean hasStatement(Model model, Resource subject, URI predicate, Node object) throws ModelException {
        boolean result = false;
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, object);
            result = iterator.hasNext();
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
        return result;
    }
}
