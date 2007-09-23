/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains utility methods that facilitate the generation of various values from an RDF2Go model.
 * The interface is deliberately similar to that of a ValueFactory, but this class can be used in situations
 * when the code has to work directly with a model, without it being wrapped in an RDFContainer.
 */
public class ModelUtil {

    private static Logger log = LoggerFactory.getLogger(ModelUtil.class);
    
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
    
    /**
     * Returns a resource that has a given property with the given value. This
     * method assumes that the inverse of this property is obligatory and
     * functional. That is exactly one such subject must exist. In other cases
     * (no subject or more than one subject) an exception is thrown.
     * @param model
     * @param predicate
     * @param object
     * @return
     */
    public static Resource getSingleSubjectWithProperty(
        Model model,
        URI predicate,
        Node object) {
        
        if (model == null || predicate == null || object == null) {
            throw new NullPointerException("All parameters of the " +
                    "getSingleSubjectWithProperty method must be non-null");
        }
        
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model
                .findStatements(Variable.ANY, predicate, object);
            if (!iterator.hasNext()) {
                throw new ModelException(
                    "There are no subjects for property: " + predicate + 
                    " object: " + object);
            }
            Statement statement = iterator.next();
            if (iterator.hasNext()) {
                throw new ModelException("There are multiple subjects for "
                    + "the given property");
            }
            return statement.getSubject().asResource();
        } catch (ModelException me) {
            log.warn("Couldn't get the subject for the given "
                + "property", me);
        } finally {
            closeClosable(iterator);
        }
        return null;
    }
    
    /**
     * Returns all resources that have a given property with the given value.
     * @param model
     * @param predicate
     * @param object
     * @return
     */
    public static Collection<Resource> getAllSubjectsWithProperty(
        Model model,
        URI predicate,
        Node object) {
        
        if (model == null || predicate == null || object == null) {
            throw new NullPointerException("All parameters of the " +
                    "getSingleSubjectWithProperty method must be non-null");
        }
        
        ClosableIterator<? extends Statement> iterator = null;
        List<Resource> result = new LinkedList<Resource>();
        try {
            iterator = model
                .findStatements(Variable.ANY, predicate, object);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                result.add(statement.getSubject());
            }
            return result;
        } finally {
            closeClosable(iterator);
        }
    }
    
    /**
     * @param model The model to work with.
     * @param subject The subject.
     * @param predicate The property we would like to find.
     * @return the value of a given property applied to the given subject. If 
     *         there is more than value, one of the values is returned, if there
     *         is none, null is returned.
     */
    public static Node getPropertyValue(
        Model model,
        Resource subject,
        URI predicate) {
        
        if (model == null || subject == null || predicate == null) {
            throw new NullPointerException("All arguments of the " +
                    "getPropertyValue method must be non-null");
        }
        
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model
                .findStatements(subject, predicate, Variable.ANY);
            if (!iterator.hasNext()) {
                return null;
            }
            Statement statement = iterator.next();
            return statement.getObject();
        } catch (ModelRuntimeException me) {
            log.warn("Couldn't get the subject for the given "
                + "property", me);
        } finally {
            closeClosable(iterator);
        }
        return null;
    }
    
    /**
     * Returns all values of a given property for the given resource.
     * @param model The model in which to look for values.
     * @param subject The resource.
     * @param predicate The property.
     * @return A list of values for of the given property for the given resource.
     */
    public static List<Node> getAllPropertyValues(
        Model model,
        Resource subject,
        URI predicate) {
        List<Node> resultList = new LinkedList<Node>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject,predicate,Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                resultList.add(statement.getObject());
            }
        } catch (ModelRuntimeException me) {
            log.warn("Couldn't get all property values",me);
        } finally {
            closeClosable(iterator);
        }
        return resultList;
    }
    
    /**
     * Removes all values of the given property for the given resource.
     * @param model the model in which to look for values
     * @param subject the resource
     * @param predicate the property
     */
    public static void removeAllPropertyValues(Model model, Resource subject, URI predicate) {
        ClosableIterator<? extends Statement> iterator = null;
        List<Statement> statementsToRemove = new LinkedList<Statement>();
        try {
            iterator = model.findStatements(subject,predicate,Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                statementsToRemove.add(statement);
            }
            iterator.close();
            for (Statement statement : statementsToRemove) {
                model.removeStatement(statement);
            }
        } catch (ModelRuntimeException me) {
            log.warn("Couldn't remove all property values",me);
        } finally {
            closeClosable(iterator);
        }
    }
    
    private static void closeClosable(ClosableIterator iterator) {
        if (iterator != null) {
            iterator.close();
        }
    }
    
    /**
     * Converts a node to an instance of the given class. 
     * @param node the node
     * @param clazz class to which the node should be converted, currently only primitive types are supported
     *    and following RDF2Go classes: URI, Node and Literal
     * @return the converted object or null if the conversion doesn't work
     */
    public static Object convertNode(Node node, Class<?> clazz) {
        try {
            if (clazz.equals(String.class)) {
                return node.toString();
            } else if (clazz.equals(Node.class)) {
                return node;
            } else if (clazz.equals(Resource.class)) {
                return node;
            } else if (clazz.equals(URI.class)) {
                return node;
            } else if (clazz.equals(Literal.class)) {
                return node;
            } else if (clazz.equals(Integer.class)) {
                return new Integer(node.toString());
            } else if (clazz.equals(Long.class)) {
                return new Long(node.toString());
            } else if (clazz.equals(Boolean.class)) {
                String string = node.toString();
                if (string.equals("1")) {
                    return Boolean.TRUE;
                } else if (string.equals("0")) {
                    return Boolean.FALSE;
                }
                return new Boolean(node.toString());
            } else if (clazz.equals(Byte.class)) {
                return new Byte(node.toString());
            } else if (clazz.equals(Short.class)) {
                return new Short(node.toString());
            } else if (clazz.equals(Float.class)) {
                return new Float(node.toString());
            } else if (clazz.equals(Double.class)) {
                return new Double(node.toString());
            } else {
                log.warn("Unknown class to convert node: " + node + ", clazz: " + clazz.getName());
                return null;
            }
        } catch (Exception e) {
            log.debug("Conversion failed. node: " + node + ", clazz: " + clazz.getName());
            return null;
        }
    }
}
