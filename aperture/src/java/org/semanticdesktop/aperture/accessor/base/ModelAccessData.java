/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModelAccessData provides an AccessData implementation storing its information to and retrieving it from a
 * Model.
 * 
 * <p>
 * This implementation assumes that IDs used to store data are valid URIs and that keys contain only
 * characters that can be used in URIs.
 * 
 * <p>
 * The AccessData.DATE_KEY, AccessData.BYTE_SITE_KEY and AccessData.REDIRECTS_TO_KEY keys are mapped to
 * Aperture DATA predicates. In that case the value must be a long encoded as a String or, in the last case, a
 * URL encoded as a String.
 */
public class ModelAccessData implements AccessData {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String NS_MAD_STRING = "http://aperture.sourceforge.net/2007/07/19/mad#";
    
    public static final URI NS_MAD = new URIImpl(NS_MAD_STRING);

    public static final URI linksTo = new URIImpl(NS_MAD_STRING + "linksTo");
    
    public static final URI aggregates = new URIImpl(NS_MAD_STRING + "aggregates");
    
    public static final URI redirectsTo = new URIImpl(NS_MAD_STRING + "redirectsTo");
    
    public static final URI dateAsNumber = new URIImpl(NS_MAD_STRING + "dateAsNumber");
    
    public static final URI byteSize = new URIImpl(NS_MAD_STRING + "byteSize");
    
    public static final URI timestamp = new URIImpl(NS_MAD_STRING + "timestamp");

    /**
     * Used as a prefix to derive URIs from AccessData key names.
     */
    public static final String URI_PREFIX = "urn:accessdata:";

    /**
     * The Model holding the context information.
     */
    private Model model;
    
    private long timestampLong = -1;

    /**
     * Creates a new ModelAccessData instance.
     * 
     * @param model The Model used to store all access data in and retrieve all data from. This cannot be
     *            null.
     */
    public ModelAccessData(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }

        this.model = model;
        this.timestampLong = -1;
    }
    
    private void checkInitialization() {
        if (timestampLong < 0) {
            throw new IllegalStateException("AccessData not initialized, call initialize() first");
        }
    }

    public void clear() throws IOException {
        this.timestampLong = -1;
        try {
            model.removeAll();
        }
        catch (ModelRuntimeException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    public void touch(String id) {
        URI idURI = model.createURI(id);
        touch(idURI);
    }
    
    public boolean isTouched(String id) {
        URI idURI = model.createURI(id);
        ClosableIterator<? extends Statement> iter = null;
        try {
            iter = model.findStatements(idURI,timestamp,Variable.ANY);
            if (!iter.hasNext()) {
                return false;
            }
            Statement statement = iter.next();
            boolean result = checkTouched(statement.getObject());
            iter.close();
            return result;
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }
    
    private void touch(URI id) {
        model.removeStatement(id,timestamp,(Node)null);
        try {
            // this is crappy, the ModelUtil class shouldn't throw ModelExceptions anymore
            model.addStatement(id,timestamp,ModelUtil.createLiteral(model, timestampLong));
        }
        catch (ModelException e) {
            throw new ModelRuntimeException(e);
        }
    }

    public String get(String id, String key) {
        checkInitialization();
        commit();

        ClosableIterator<? extends Statement> iterator = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);
            URI keyURI = toURI(key);

            // only returns a value when there is exactly one matching statement
            iterator = model.findStatements(idURI, keyURI, Variable.ANY);
            if (iterator.hasNext()) {
                Statement statement = iterator.next();
                if (!iterator.hasNext()) {
                    Node value = statement.getObject();
                    if (value instanceof Literal) {
                        return ((Literal) value).getValue();
                    }
                    else if (value instanceof URI) {
                        return value.toString();
                    }
                }
            }
            return null;
        }
        catch (ModelException me) {
            logger.error("Could not get value for id: '" + id + "' key: '" + key + "'", me);
            return null;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    public Set<String> getReferredIDs(String id) {
        checkInitialization();
        commit();

        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);
            iterator = model.findStatements(idURI, linksTo, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Node value = statement.getObject();
                if (value instanceof URI) {
                    if (result == null) {
                        result = new HashSet<String>();
                    }
                    result.add(((URI) value).toString());
                }
            }
        }
        catch (ModelException me) {
            logger.error("Could not get referred id's", me);
            return null;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return result;
    }

    /**
     * Warning: expensive operation, as this implementation queries for all unique subjects used in this
     * ModelAccessData's context.
     */
    public int getSize() {
        checkInitialization();
        return getStoredIDs().size();
    }

    public Set<String> getStoredIDs() {
        checkInitialization();
        commit();

        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = new HashSet<String>();

        try {
            iterator = model.findStatements(Variable.ANY, Variable.ANY,Variable.ANY);
            while (iterator.hasNext()) {
                result.add(iterator.next().getSubject().toString());
            }
        }
        catch (ModelRuntimeException me) {
            logger.warn("Couldn't get stored IDs", me);
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return result;
    }

    public void initialize() throws IOException {
        this.timestampLong = System.currentTimeMillis();
        safelySleep(2); // this is to ensure that two consecutive crawler will have diferent timestamps
        // it is ugly, but without it the mechanism could get unreliable (e.g. in unit tests)
        // using uuid's would be inefficient in SPARQL, since we'd have to use the FILTER regex constructs
    }

    private void safelySleep(long ms) {
        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while (now < start + ms) {
            try {
                Thread.sleep(start + ms - now);
            }
            catch (InterruptedException e) {
                // do nothing
            }
            now = System.currentTimeMillis();
        }
    }
    
    public boolean isKnownId(String id) {
        checkInitialization();
        commit();

        ClosableIterator<? extends Statement> iterator = null;

        try {
            URI idURI = ModelUtil.createURI(model, id);
            iterator = model.findStatements(idURI, Variable.ANY,Variable.ANY);
            return iterator.hasNext();
        }
        catch (ModelException me) {
            logger.error("Could not determine if an ID is known: " + id, me);
            return false;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    public void put(String id, String key, String value) {
        checkInitialization();
        try {
            // remove any previous statements with different values
            URI subject = ModelUtil.createURI(model, id);
            URI predicate = toURI(key);
            remove(subject, predicate);

            // add the new statement
            if (predicate == redirectsTo) {
                add(ModelUtil.createStatement(model, subject, predicate, ModelUtil.createURI(model, value)));
            }
            else {
                URI dataType = (predicate == dateAsNumber || predicate == byteSize) ? XSD._long
                        : XSD._string;
                Literal object = ModelUtil.createLiteral(model, value, dataType);
                add(ModelUtil.createStatement(model, subject, predicate, object));
            }
            add(model.createStatement(subject, timestamp, ModelUtil.createLiteral(model, timestampLong)));
        }
        catch (ModelException e) {
            logger.error("Could not store info for ID " + id, e);
        }
    }

    public void putReferredID(String id, String referredID) {
        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, referredID);
            add(ModelUtil.createStatement(model, subject, linksTo, object));
        }
        catch (ModelException e) {
            logger.error("Could not store referred ID for ID " + id, e);
        }
    }

    public void remove(String id, String key) {
        try {
            URI idURI = ModelUtil.createURI(model, id);
            remove(idURI, toURI(key));
        }
        catch (ModelException e) {
            logger.error("Could not remove value for ID " + id, e);
        }
    }

    public void remove(String id) {
        try {
            remove(ModelUtil.createURI(model, id), null);
        }
        catch (ModelException e) {
            logger.error("Could not remove info about ID " + id, e);
        }
    }

    public void removeReferredID(String id, String referredID) {
        commit();

        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, referredID);
            Statement statement = ModelUtil.createStatement(model, subject, linksTo, object);

            model.removeStatement(statement);
        }
        catch (ModelException e) {
            logger.error("Could not remove referred ID for ID " + id, e);
        }
    }

    public void removeReferredIDs(String id) {
        try {
            URI idURI = ModelUtil.createURI(model, id);
            remove(idURI, linksTo);
        }
        catch (ModelException e) {
            logger.error("Could not remove referred IDs for ID " + id, e);
        }
    }

    public void store() throws IOException {
        this.timestampLong = -1;
        commit();
    }

    private void commit() {}

    private URI toURI(String key) throws ModelException {
        if (key == AccessData.DATE_KEY) {
            return dateAsNumber;
        }
        else if (key == AccessData.BYTE_SIZE_KEY) {
            return byteSize;
        }
        else if (key == AccessData.REDIRECTS_TO_KEY) {
            return redirectsTo;
        }
        else {
            return ModelUtil.createURI(model, URI_PREFIX + key);
        }
    }

    private void add(Statement statement) {
        try {
            model.addStatement(statement);
        }
        catch (ModelRuntimeException e) {
            logger.error("Exception while adding statement", e);
        }
    }

    private void remove(URI subject, URI predicate) {
        commit();
        ClosableIterator<? extends Statement> iter = null;
        try {
            // remove the subtree
            
            if (predicate == null) {
                // this means that we want to remove everything we know
                iter = model.findStatements(subject, aggregates, Variable.ANY);
                while (iter.hasNext()) {
                    URI child = iter.next().getObject().asURI();
                    remove(child,null);
                }
            }
            
            // remove the current node
            model.removeStatement(subject, predicate, (Node)null);
            // remove the link from the parent
            model.removeStatement(null, aggregates, subject);
            // note that the incoming referredID links are left alone
        }
        catch (ModelRuntimeException e) {
            logger.error("Exception while removing statement", e);
        }
        finally {
            if (iter != null) {
                iter .close();
            }
        }
    }

    public Set getAggregatedIDs(String id) {
        commit();

        ClosableIterator<? extends Statement> iterator = null;
        HashSet<String> result = new HashSet<String>();

        try {
            URI idURI = ModelUtil.createURI(model, id);
            iterator = model.findStatements(idURI, aggregates, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Node value = statement.getObject();
                if (value instanceof URI) {
                    
                    result.add(((URI) value).toString());
                }
            }
        }
        catch (ModelException me) {
            logger.error("Could not get referred id's", me);
            return null;
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }

        return result;
    }

    public void putAggregatedID(String id, String aggregatedID) {
        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, aggregatedID);
            // if this aggregateID already had a parent, the previous link must be
            // removed, otherwise cycles in the aggregation graph may occur
            model.removeStatement(null, aggregates, object);
            
            add(ModelUtil.createStatement(model, subject, aggregates, object));
        }
        catch (ModelException e) {
            logger.error("Could not store referred ID for ID " + id, e);
        }
    }

    public void removeAggregatedID(String id, String aggregatedID) {
        commit();
        try {
            URI subject = ModelUtil.createURI(model, id);
            URI object = ModelUtil.createURI(model, aggregatedID);
            Statement statement = ModelUtil.createStatement(model, subject, aggregates, object);
            model.removeStatement(statement);
        }
        catch (ModelException e) {
            logger.error("Could not remove referred ID for ID " + id, e);
        }
    }
    
    // this tries to remove untouched IDs gracefully, without creating a gigantic set
    public void removeUntouchedIDs() {
        ClosableIterator<? extends Statement> iter = null;
        try {
            iter = model.findStatements(Variable.ANY, timestamp, Variable.ANY);
            // we can only delete statements behind the iterator
            Resource previousResource = null;
            while (iter.hasNext()) {
                if (previousResource != null) {
                    model.removeStatements(previousResource,Variable.ANY,Variable.ANY);
                    // in conformance to the remove() method the incoming referredID links are left alone
                    model.removeStatements(Variable.ANY,aggregates,previousResource);
                }
                
                Statement statement = iter.next();
                Node subject = statement.getObject();
                if (!checkTouched(subject)) {
                    previousResource = statement.getSubject();                    
                }
            }
            // don't forget the last one...
            if (previousResource != null) {
                model.removeStatements(previousResource,Variable.ANY,Variable.ANY);
                // in conformance to the remove() method the incoming referredID links are left alone
                model.removeStatements(Variable.ANY,aggregates,previousResource);
            }
        } catch (ModelRuntimeException e) {
            throw e;
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }
    
    public ClosableIterator getUntouchedIDsIterator() {
        return new UntouchedIterator(model.findStatements(Variable.ANY,timestamp,Variable.ANY));
    }
    
    private class UntouchedIterator implements ClosableIterator {

        private ClosableIterator<? extends Statement> wrappedIterator;
        private String nextValue;
        
        public UntouchedIterator(ClosableIterator<? extends Statement> it) {
            this.wrappedIterator = it;
        }
        
        public boolean hasNext() {
            getNextUntouched();
            return nextValue != null;
        }

        public Object next() {
            getNextUntouched();
            String result = nextValue;
            nextValue = null;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }        
        
        public void close() {
            wrappedIterator.close();
        }
        
        private void getNextUntouched() {
            if (nextValue == null) {
                while (wrappedIterator.hasNext()) {
                    Statement statement = wrappedIterator.next(); 
                    Node node = statement.getObject();
                    boolean touched = checkTouched(node);
                    if (!touched) {
                        nextValue = statement.getSubject().toString();
                        break;
                    }
                }
                // automatically close the iterator, this may save some poor soul
                if (!wrappedIterator.hasNext()) {
                    wrappedIterator.close();
                }
            }
        }
    }
    
    private boolean checkTouched(Node node) {
        boolean touched = false;
        if (node instanceof Literal) {
            Literal lit = (Literal)node;
            String value = lit.getValue();
            try {
                long longValue = Long.valueOf(value);
                if (longValue == timestampLong) {
                    touched = true;
                }
            } catch (NumberFormatException nfe) {
                // this is obviously an untouched resource
                touched = false;
            }
        } else {
            touched = false;
        }
        return touched;
    }


    public void touchRecursively(String id) {
        URI idURI = model.createURI(id);
        touchURIRecursively(idURI);
    }
    
    private void touchURIRecursively(URI uri) {
        touch(uri);
        ClosableIterator<? extends Statement> iter = null;
        try {
            iter = model.findStatements(uri,aggregates,Variable.ANY);
            while (iter.hasNext()) {
                Statement statement = iter.next();
                touchURIRecursively(statement.getObject().asURI());
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }
    
    public ClosableIterator getAggregatedIDsClosure(String id) {
        URI idURI = model.createURI(id);
        return new AggregatedClosureIterator(model.findStatements(idURI,aggregates,Variable.ANY),id);
    }
    
    private class AggregatedClosureIterator implements ClosableIterator {

        private List<ClosableIterator<? extends Statement>> iteratorStack;
        private String nextValue;
        
        public AggregatedClosureIterator(ClosableIterator<? extends Statement> firstIterator, String firstValue) {
            this.nextValue = firstValue;
            this.iteratorStack = new LinkedList<ClosableIterator<? extends Statement>>();
            this.iteratorStack.add(0,firstIterator);
        }
        
        public boolean hasNext() {
            getNextValue();
            return nextValue != null;
        }

        public Object next() {
            getNextValue();
            if (nextValue == null) {
                throw new NoSuchElementException();
            }
            String result = nextValue;
            nextValue = null;
            return result;
            
        }

        private void getNextValue() {
            if ( nextValue != null) {
                return;
            }
            
            while (iteratorStack.size() > 0) {
                ClosableIterator<? extends Statement> iter = iteratorStack.get(0);
                if (iter.hasNext()) {
                    Statement statement = iter.next();
                    nextValue = statement.getSubject().toString();
                    this.iteratorStack.add(0,model.findStatements(statement.getObject().asURI(),aggregates,Variable.ANY));
                    break;
                } else {
                    iter.close();
                    iteratorStack.remove(0);
                }
            }                        
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void close() {
            for (ClosableIterator<? extends Statement> iter : iteratorStack) {
                iter.close();
            }
        }
    }
}
