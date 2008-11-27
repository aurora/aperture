/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NID3;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.semanticdesktop.aperture.vocabulary.TAGGING;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidator;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;

/**
 * A common superclass for all unit tests of aperture.
 */
public class ApertureTestBase extends TestCase {

	public static class TestRDFContainerFactory implements RDFContainerFactory {
        public Map<String,RDFContainer> returnedContainers;
        public TestRDFContainerFactory() {
            this.returnedContainers = new HashMap<String, RDFContainer>();
        }
        public RDFContainer getRDFContainer(URI uri) {
            Model model = RDF2Go.getModelFactory().createModel();
            model.open();
            RDFContainer res = new RDFContainerImpl(model,uri);
            returnedContainers.put(uri.toString(), res);
            return res;
        }
    }

    protected static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";
    
    protected static StandaloneValidator validator;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// CREATING MODELS AND RDFCONTAINERS ////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates an in-memory model
     * @return an in-memory model
     */
	protected Model createModel() {
		try {
            Model model = RDF2Go.getModelFactory().createModel(); 
            model.open();
			return model;
		}
		catch (ModelRuntimeException me) {
			return null;
		}
	}
	
	/**
	 * Creates an RDFContainer describing the given URI
	 * @param uri
	 * @return
	 */
	protected RDFContainer createRDFContainer(String uri) {
        return createRDFContainer(new URIImpl(uri,false));
    }
    
	/**
	 * Creates an RDFContainer describing the given URI
	 * @param uri
	 * @return
	 */
    protected RDFContainer createRDFContainer(URI uri) {
        Model newModel = createModel();
        return new RDFContainerImpl(newModel,uri);
    }
    
    protected void closeIterator(ClosableIterator<? extends Object> iterator) {
        if (iterator != null) {
            iterator.close();
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// VALIDATION //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Validates the metadata of the given DataObject and prints the report on the standard output.
     * 
     * @param object
     */
    public void validate(DataObject object) {
        validate(object.getMetadata(), true);
    }
    
    /**
	 * Validates the content of the given container and prints the report to the standard output.
	 * @param container
	 */
    public void validate(RDFContainer container) {
        validate(container,true);
    }
    
    /**
     * Validates the content and lets the user decide whether to print the report to the standard output.
     * @param container the container
     * @param print true if the report is to be printed, false otherwise
     */
    public void validate(RDFContainer container, boolean print) {
        validate(container.getModel(), print, null, (ModelTester[])null);
    }

    /**
     * Validates the given model and prints the report to the standard output.
     * @param model
     */
    public void validate(Model model) {
        validate(model,true, null,(ModelTester[])null);
    }
    
    /**
     * Validates the given model and lets the use decide whether to print the report to the standard output.
     * @param model
     * @param print
     */
    public void validate(Model model, boolean print) {
        validate(model,print,null,(ModelTester[])null);
    }
    
    /**
     * Validates the given model and performs additional test with the given additional model testers.
     * 
     * @param model the model to validate
     * @param print true if the report is to be printed on the standard output, false otherwise
     * @param dataSourceUri the uri of the datasource, the validator will temporarily insert an dataSourceUri
     *            rdf:type nie:DataSource triple to satiate the validator. This case is quite common and we
     *            would not like it to be treated as an error.
     * @param additionalTesters testers that will be used in addition to the default NRLClosedWorldModelTester
     */
    public void validate(Model model, boolean print, URI dataSourceUri, ModelTester ... additionalTesters) {
        if (validationTurnedOff()) {
            return;
        }
        
        
        boolean removeFlag = false;
        Statement statement = null;
        if (dataSourceUri != null) {
            statement = model.createStatement(dataSourceUri, RDF.type, NIE.DataSource);
            if (model.contains(statement)) {
                removeFlag = false;
            } else {
                model.addStatement(statement);
                removeFlag = true;
            }            
        }
        
        if (additionalTesters != null && additionalTesters.length > 0 && additionalTesters[0] != null) {
            ModelTester [] testers = new ModelTester[additionalTesters.length + 1];
            testers[0] = new NRLClosedWorldModelTester();
            for (int i = 0; i < additionalTesters.length; i++) {
                testers[i+1] = additionalTesters[i];
            }
            validateWithTesters(model,print,testers);
        } else {
            validateWithTesters(model,print,new NRLClosedWorldModelTester());
        }
        
        if (removeFlag) {
            model.removeStatement(statement);
        }
    }

    private boolean validationTurnedOff() {
        String prop = System.getProperty("aperture.validation.skip");
        return Boolean.valueOf(prop);
    }

    private void validateWithTesters(Model model, boolean print, ModelTester... testers) {
        try {
            if (validator == null) {
                initializeValidator();
            }
            validator.setModelTesters(testers);
            ValidationReport report = validator.validate(model);
            if ( !report.isValid() || (report.getMessages().size() > 0 && print)) {
                printValidationReport(report);
            }
            if (!report.isValid()) {
                fail();
            } 
            
        } catch (StandaloneValidatorException sve) {
            sve.printStackTrace();
            if (sve.getCause() != null) {
                sve.getCause().printStackTrace();
            }
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    protected void initializeValidator() throws Exception {
        validator = new StandaloneValidatorImpl();        
        Model tempModel  = RDF2Go.getModelFactory().createModel();
        tempModel.open();
        
        NIE.getNIEOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NIE.NS_NIE));
        tempModel.removeAll();
        
        NCO.getNCOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NCO.NS_NCO));
        tempModel.removeAll();
        
        NFO.getNFOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NFO.NS_NFO));
        tempModel.removeAll();
        
        NMO.getNMOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NMO.NS_NMO));
        tempModel.removeAll();
        
        NCAL.getNCALOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NCAL.NS_NCAL));
        tempModel.removeAll();
        
        NEXIF.getNEXIFOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NEXIF.NS_NEXIF));
        tempModel.removeAll();
        
        NID3.getNID3Ontology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NID3.NS_NID3));
        tempModel.removeAll();
        
        TAGGING.getTAGGINGOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(TAGGING.NS_TAGGING));
        tempModel.removeAll();
        
        GEO.getGEOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(GEO.NS_GEO));
        tempModel.removeAll();
        
        tempModel.close();
    }
    
    private String getOntUriFromNs(URI uri) {
        return uri.toString().substring(0,uri.toString().length() - 1);
    }

    private void printValidationReport(ValidationReport report) {
        System.out.println("Validation report");
        List<ValidationMessage> messages = report.getMessages();
        int i = 1;
        for (ValidationMessage msg : messages) {
            System.out.print  ("" + i + ": ");
            System.out.println(msg.getMessageType().toString() + " ");
            System.out.println("   " + msg.getMessageTitle() + " ");
            System.out.println("   " + msg.getMessage() + " ");
            for (Statement stmt : msg.getStatements()) {
                try {
                    System.out.println("   {" + stmt.getSubject().toSPARQL() + ",");
                    System.out.println("    " + stmt.getPredicate().toSPARQL() + ",");
                    System.out.println("    " + stmt.getObject().toSPARQL() + "}");
                } catch (Exception x) {
                    // YES, blank nodes do not support toSparql...
                    System.out.println("   {" + stmt + "}");
                }
            }
            i++;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// ASSERTIONS ////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a the object of the triple with the given subject and predicate. Asserts that there is only one
     * such triple and that the object of that triple is a Resource.
     * 
     * @param model the model to check in
     * @param subject the subject of the the triple to be found
     * @param predicate the predicate of the triple to be found
     * @return the object of the triple to be found, assumes that there is one and only one such triple and
     *         that it is a resource
     */
    protected Resource findSingleObjectResource(Model model, Resource subject, URI predicate) {
        Set<Resource> set = findObjectResourceSet(model, subject, predicate);
        assertEquals(1,set.size());
        return set.iterator().next();
    }

    /**
     * Returns a list of objects of all triples with the given subject and predicate. Asserts that all those
     * objects are Resources.
     * 
     * @param model the model to check in
     * @param subject the subject of the triple to be found
     * @param predicate the predicate of the triple to be found
     * @return a list of objects of all triples with the given subject and predicate, converted to Resource
     *         instances
     */
    protected Set<Resource> findObjectResourceSet(Model model, Resource subject, URI predicate) {
        HashSet<Resource> result = new HashSet<Resource>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Node value = statement.getObject();
                assertTrue(value instanceof Resource);
                result.add(value.asResource());
            }
            return result;
        } finally {
            closeIterator(iterator);
        }
    }
    
    /**
     * Returns a the object of the triple with the given subject and predicate. Asserts that there is only
     * one such triple.
     * @param model the model to check in
     * @param subject the subject of the the triple to be found
     * @param predicate the predicate of the triple to be found
     * @return the object of the triple to be found, assumes that there is one and only one such triple
     */
    protected Node findSingleObjectNode(Model model, Resource subject, URI predicate) {
        Set<Node> set = findObjectNodeSet(model, subject, predicate);
        assertEquals(1,set.size());
        return set.iterator().next();
    }
    
    /**
     * Returns a list of objects of all triples with the given subject and predicate. Asserts that all
     * those objects are Resources.
     * @param model the model to check in
     * @param subject the subject of the triple to be found
     * @param predicate the predicate of the triple to be found
     * @return a list of objects of all triples with the given subject and predicate 
     */
    protected Set<Node> findObjectNodeSet(Model model, Resource subject, URI predicate) {
        HashSet<Node> result = new HashSet<Node>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Node value = statement.getObject();
                result.add(value);
            }
            return result;
        } finally {
            closeIterator(iterator);
        }
    }
    
    /**
     * Returns a the subject of the triple with the given predicate and object. Asserts that there is only
     * one such triple.
     * @param model the model to check in
     * @param predicate the predicate of the triple to be found
     * @param object the object of the the triple to be found
     * @return the object of the triple to be found, assumes that there is one and only one such triple
     */
    protected Resource findSingleSubjectResource(Model model, URI predicate, Resource object) {
        Set<Resource> set = findSubjectResourceSet(model, predicate, object);
        assertEquals(1,set.size());
        return set.iterator().next();
    }
    
    /**
     * Returns a list of subjects of all triples with the given subject and predicate
     * @param model the model to check in
     * @param predicate the predicate of the triple to be found
     * @param object the object predicate
     * @return a list of subjects of all triples with the given subject and predicate 
     * @throws ModelException if something goes wrong
     */
    protected Set<Resource> findSubjectResourceSet(Model model, URI predicate, Resource object) {
        HashSet<Resource> result = new HashSet<Resource>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, predicate, object);
            while (iterator.hasNext()) {
                Statement statement = iterator.next();
                Resource subject = statement.getSubject();
                result.add(subject);
            }
            return result;
        } finally {
            closeIterator(iterator);
        }
    }

    /**
     * Asserts that the given container contains the given property, and that one of the values of that
     * property is a literal, whose label contains the given substring.
     * 
     * @param property the property to look for
     * @param substring the substring to look for
     * @param container the container to look in
     * @throws ModelException if something goes wrong
     */
    public void checkStatement(URI property, String substring, RDFContainer container) 
            throws ModelException {
        // setup some info
        Model model = container.getModel();
        boolean encounteredSubstring = false;

        // loop over all statements that have the specified property uri as predicate
        ClosableIterator<? extends Statement> statements = model.findStatements(
            container.getDescribedUri(), property, Variable.ANY);
        try {
            while (statements.hasNext()) {
                // check the property type
                Statement statement = statements.next();
                assertTrue(statement.getPredicate().equals(property));

                // see if it has a Literal containing the specified substring
                Node object = statement.getObject();
                if (object instanceof Literal) {
                    String value = ((Literal) object).getValue();
                    if (value.indexOf(substring) >= 0) {
                        encounteredSubstring = true;
                        break;
                    }
                }
            }
        }
        finally {
            statements.close();
        }

        // see if any of the found properties contains the specified substring
        if (!encounteredSubstring)
            fail("Expected substring '"+substring+
                "' in property "+property+" not found");
        assertTrue(encounteredSubstring);
    }

    /**
     * Asserts that the given container contains the given property AND that there is exactly
     * one value for this property AND that the value of this property is a Resource with an
     * rdf:type of nco:Contact, which has a nco:fullname property whose value is equal to the
     * given string.
     * @param property
     * @param fullname
     * @param container
     */
    public void checkSimpleContact(URI property, String fullname, RDFContainer container) {
        Node node = container.getNode(property);
        assertTrue(node instanceof Resource);
        Resource resource = (Resource)node;
        Model model = container.getModel();
        assertTrue(model.contains(resource, RDF.type, NCO.Contact));
        assertTrue(model.contains(resource, NCO.fullname, fullname));
    }
    
    /**
     * Asserts that the given container contains the given property AND that one of the values of this
     * property is a Resource with an rdf:type of nco:Contact, which has a nco:fullname property whose value
     * is equal to the given string. There may be more values of the given property in this container.
     * 
     * @param property
     * @param fullname
     * @param container
     */
    public void checkMultipleSimpleContacts(URI property, String fullname, RDFContainer container) {
        Model model = container.getModel();
        QueryResultTable table = model.sparqlSelect(
                "PREFIX nco: <" + NCO.NS_NCO + "> " +
                "SELECT ?contact " +
                "WHERE" +
                "  {" + container.getDescribedUri().toSPARQL() + " " + property.toSPARQL() + " ?contact ." +
                "    ?contact nco:fullname ?name . " +
                "    FILTER (regex(?name,\"" + fullname + "\"))" +
                "  }");
        ClosableIterator<QueryRow> iterator = null;
        try {
            iterator = table.iterator();
            assertTrue(iterator.hasNext());;
        } finally {
            iterator.close();
        }
    }

    /**
     * Asserts that the given container contains the given property, and that one of the values
     * of that property (there may be more) is an URI equal to the given value.
     * @param property
     * @param value
     * @param container
     * @throws ModelException
     */
    public void checkStatement(URI property, URI value, RDFContainer container) 
            throws ModelException {
        URI subject = container.getDescribedUri(); 
        checkStatement(subject, property, value, container);
    }

    /**
     * Asserts that the given container contains the given property, and that one of the values
     * of that property (there may be more) is a Node equal to the given node.
     * @param subject
     * @param property
     * @param value
     * @param container
     * @throws ModelException
     */
    public void checkStatement(URI subject, URI property, Node value, RDFContainer container) 
            throws ModelException {
        checkStatement(subject, property, value, container.getModel());
    }

    /**
     * Asserts that the given model contains a statement with the given subject, property and value.
     * @param subject
     * @param property
     * @param value
     * @param model
     * @throws ModelException
     */
    public void checkStatement(URI subject, URI property, Node value, Model model) throws ModelException {
        boolean encounteredValue = false;

        // loop over all statements that have the specified property uri as predicate
        ClosableIterator<? extends Statement> statements = model.findStatements(subject,property,Variable.ANY);
        try {
            while (statements.hasNext()) {
                // check the property type
                Statement statement = (Statement) statements.next();
                assertTrue(statement.getPredicate().equals(property));

                // see if it has a Literal containing the specified substring
                Node object = statement.getObject();
                if (object.equals(value)) {
                    encounteredValue = true;
                    break;
                }
            }
        }
        finally {
            statements.close();
        }

        // see if any of the found properties contains the specified substring
        assertTrue(encounteredValue);
    }
    
    protected void assertSingleValueProperty(Model model, Resource subject, URI predicate, String objectLabel) {
        assertSingleValueProperty(model, subject, predicate, model.createPlainLiteral(objectLabel));
    }

    /**
     * Asserts that a given triple in the model exists AND that it is the only one with the given subject and
     * predicate.
     * 
     * @param model
     * @param subject
     * @param predicate
     * @param object
     */
    protected void assertSingleValueProperty(Model model, Resource subject, URI predicate, Node object) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
            assertTrue(iterator.hasNext());
            Statement statement = iterator.next();
            assertFalse(iterator.hasNext());
            if (object instanceof Literal) {
                assertEquals(((Literal)object).getValue(), ((Literal)statement.getObject()).getValue());
            } else {
                assertEquals(statement.getObject().toString(), object.toString());
            }
            iterator.close();
        }
        finally {
            closeIterator(iterator);
        }
    }

    /**
     * Asserts that a given triple in the model exists AND that it is the only one with the given subject and
     * predicate AND that the object is a literal with a given XSD datatype.
     * 
     * @param model
     * @param subject
     * @param predicate
     * @param objectLabel
     * @param xsdDatatype
     */
    protected void assertSingleValueProperty(Model model, Resource subject, URI predicate,
            String objectLabel, URI xsdDatatype) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
            assertTrue(iterator.hasNext()); // statement exists
            Statement statement = iterator.next();
            assertFalse(iterator.hasNext()); // it is the only one
            iterator.close();
            Node object = statement.getObject();
            assertTrue(object instanceof DatatypeLiteral); // the object is a literal
            DatatypeLiteral literal = (DatatypeLiteral) object;
            assertEquals(literal.getValue(), objectLabel); // it's label is as given
            assertEquals(literal.getDatatype(), xsdDatatype); // and datatype as well
        }
        finally {
            closeIterator(iterator);
        }
    }

    protected void assertSingleValueURIProperty(Model model, Resource parentNode, URI predicate, String label)
            throws ModelException {
        Resource attachedUri = findSingleObjectResource(model, parentNode, predicate);
        assertTrue(attachedUri instanceof URI);
        assertEquals(attachedUri.toString(), label);
    }

    /**
     * Asserts that the given triple exists in the given model. It doesn't need to be the only one with the
     * given subject and predicate.
     * 
     * @param model
     * @param subject
     * @param predicate
     * @param value
     */
    protected void assertMultiValueProperty(Model model, Resource subject, URI predicate, String valueLabel)
            throws ModelException {
        assertMultiValueProperty(model, subject, predicate, model.createPlainLiteral(valueLabel));
    }

    /**
     * Asserts that the given triple exists in the given model. It doesn't need to be the only one with the
     * given subject and predicate.
     * 
     * @param model
     * @param subject
     * @param predicate
     * @param value
     */
    protected void assertMultiValueProperty(Model model, Resource subject, URI predicate, Node value)
            throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, value);
            assertTrue(iterator.hasNext());
            iterator.close();
        }
        finally {
            closeIterator(iterator);
        }
    }

    protected void assertSparqlQuery(Model model, String query) {
        ClosableIterator<QueryRow> queryIterator = null;
        QueryResultTable table = model.sparqlSelect(query);
        try {
            queryIterator = table.iterator();
            assertTrue(queryIterator.hasNext());
            queryIterator.close();
        }
        finally {
            closeIterator(queryIterator);
        }
    }
    
    protected void assertNoResultSparqlQuery(Model model, String query) {
        ClosableIterator<QueryRow> queryIterator = null;
        QueryResultTable table = model.sparqlSelect(query);
        try {
            queryIterator = table.iterator();
            assertFalse(queryIterator.hasNext());
            queryIterator.close();
        }
        finally {
            closeIterator(queryIterator);
        }
    }
    
    protected void assertMimeType(String desiredMimeType, URI uri, InputStream stream) throws Exception {
        MimeTypeIdentifier mimeTypeIdentifier = new MagicMimeTypeIdentifier();
        int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
        stream.mark(minimumArrayLength + 10); // add some for safety
        byte[] bytes = IOUtil.readBytes(stream, minimumArrayLength);
        String mimeType = mimeTypeIdentifier.identify(bytes, null, uri);
        assertEquals(mimeType, desiredMimeType);
        stream.reset();
    }
    
    /**
     * Sleep for the given number of miliseconds. This method is provided for convenience. It is immune to
     * InterruptedExceptions. If you need to be able to interrupt the sleep with the {@link Thread#interrupt()}
     * method, use {@link #interruptibleSleep(long)}
     * @param timeout The amount of miliseconds to wait.
     */
    protected void safelySleep(long timeout) {
        long begin = System.currentTimeMillis();
        long end = begin;
        while (end - begin < timeout) {
            try {
                Thread.sleep(timeout - (end - begin));
            } catch (InterruptedException ie) {
                // that shouldn't be much of a problem
            }
            end = System.currentTimeMillis();
        }
    }
    
    /**
     * Sleep for the given number of miliseconds. This method is provided for convenience. The sleep
     * may be interrupted by the {@link Thread#interrupt()}. Compare with {@link #safelySleep(long)}
     * @param timeout The amount of miliseconds to wait.
     * @throws InterruptedException if the sleep is interrupted.
     */
    protected void interruptibleSleep(long timeout) throws InterruptedException {
        long begin = System.currentTimeMillis();
        long end = begin;
        while (end - begin < timeout) {
            Thread.sleep(timeout - (end - begin));
            end = System.currentTimeMillis();
        }
    }
    
    /**
     * Join the given thread. Wait at most the given number of milliseconds. This method is provided for
     * convenience.
     * 
     * @param thread the Thread that is supposed to die
     * @param timeout The amount of miliseconds to wait for the thread to die.
     * @throws RuntimeException if the thread hasn't died within the given time
     */
    protected void safelyJoin(Thread thread, long timeout) {
        long begin = System.currentTimeMillis();
        long end = begin;
        while (thread.isAlive() && end - begin < timeout) {
            try {
                thread.join(timeout - (end - begin));
            } catch (InterruptedException ie) {
                // that shouldn't be much of a problem
            }
            end = System.currentTimeMillis();
        }
        if (thread.isAlive()) {
            throw new RuntimeException("Thread hasn't died properly");
        }
    }

    /**
     * Tests if the given model serializes properly to Xml
     * @param modelToCheck
     * @throws IOException
     */
    protected void testXmlSafety(Model modelToCheck) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        modelToCheck.writeTo(baos,Syntax.RdfXml);
        byte [] byteArray = baos.toByteArray();
        
        Model newModel = RDF2Go.getModelFactory().createModel();
        newModel.open();
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        // this should proceed without any exceptions
        newModel.readFrom(bais,Syntax.RdfXml);
        newModel.close();
    }

    /**
     * Asserts that the incremental crawling results gathered by the given {@link TestIncrementalCrawlerHandler}
     * are correct.
     * @param handler the handler to check
     * @param newObjects the desired number of new objects
     * @param changedObjects the desired number of changed objects
     * @param unchangedObjects the desired number of unchanged objects
     * @param deletedObjects the desired number of deleted objects
     */
    public void assertNewModUnmodDel(TestIncrementalCrawlerHandler handler, int newObjects,
            int changedObjects, int unchangedObjects, int deletedObjects) {
        assertEquals(handler.getNewObjects().size(), newObjects);
        assertEquals(handler.getChangedObjects().size(), changedObjects);
        assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
        assertEquals(handler.getDeletedObjects().size(), deletedObjects);
    }
}
