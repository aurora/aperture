package org.semanticdesktop.nepomuk.nrl.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.semanticdesktop.nepomuk.nrl.validator.exception.ModelTesterException;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testres.TESTRESOURCES;
import org.semanticdesktop.nepomuk.openrdf.InfSail;
import org.semanticdesktop.nepomuk.openrdf.UnionMemoryStore;
import org.semanticdesktop.nepomuk.openrdf.UnionNativeStore;

public class StandaloneValidatorTests {
    
    @Test public void testExternalSailValidator() throws Exception {
        UnionMemoryStore nativeStore = new UnionMemoryStore();
        InfSail infSail = new InfSail(nativeStore);
        infSail.initialize();
        StandaloneValidator validator = new StandaloneValidatorImpl(infSail);
        addRemoveOntologyTest(validator);
    }
    
    @Test public void testInternalSailValidator() throws Exception{
        StandaloneValidator validator = new StandaloneValidatorImpl();
        addRemoveOntologyTest(validator);
    }

    @Test public void testModelExternalSailValidator() throws Exception {
        UnionMemoryStore nativeStore = new UnionMemoryStore();
        InfSail infSail = new InfSail(nativeStore);
        infSail.initialize();
        StandaloneValidator validator = new StandaloneValidatorImpl(infSail);
        addRemoveModelTest(validator);
    }
    
    @Test public void testModelInternalSailValidator() throws Exception{
        StandaloneValidator validator = new StandaloneValidatorImpl();
        addRemoveModelTest(validator);
    }
    
    public void addRemoveOntologyTest(StandaloneValidator validator) throws StandaloneValidatorException {
        List<String> urisList = validator.listOntologyUris();
        assertEquals(urisList.size(),0);
        
        InputStream stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RDF_RDF_FILE);
        assertNotNull(stream);
        validator.addOntology(stream, RDFFormat.RDFXML.getDefaultMIMEType(),TESTRESOURCES.RDF_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(1, urisList.size());
        assertEquals(TESTRESOURCES.RDF_GRAPH_URI, urisList.get(0));
        
        stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.OWL_RDF_FILE);
        assertNotNull(stream);
        validator.addOntology(stream, RDFFormat.RDFXML.getDefaultMIMEType(), TESTRESOURCES.OWL_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(2,urisList.size());
        
        validator.removeOntology(TESTRESOURCES.RDF_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(1,urisList.size());
        
        validator.removeOntology(TESTRESOURCES.OWL_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(0,urisList.size());
    }
    
    public void addRemoveModelTest(StandaloneValidator validator) throws Exception {
        List<String> urisList = validator.listOntologyUris();
        assertEquals(urisList.size(),0);
        
        
        InputStream stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RDF_RDF_FILE);
        assertNotNull(stream);
        Model tempModel = RDF2Go.getModelFactory().createModel();
        tempModel.open();
        tempModel.readFrom(stream,Syntax.RdfXml);
        validator.addOntology(tempModel,TESTRESOURCES.RDF_GRAPH_URI);
        tempModel.close();
        urisList = validator.listOntologyUris();
        assertEquals(1, urisList.size());
        assertEquals(TESTRESOURCES.RDF_GRAPH_URI, urisList.get(0));
        
        stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.OWL_RDF_FILE);
        assertNotNull(stream);
        tempModel.open();
        tempModel.removeAll();
        tempModel.readFrom(stream,Syntax.RdfXml);
        validator.addOntology(tempModel, TESTRESOURCES.OWL_GRAPH_URI);
        tempModel.close();
        urisList = validator.listOntologyUris();
        assertEquals(2,urisList.size());
        
        validator.removeOntology(TESTRESOURCES.RDF_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(1,urisList.size());
        
        validator.removeOntology(TESTRESOURCES.OWL_GRAPH_URI);
        urisList = validator.listOntologyUris();
        assertEquals(0,urisList.size());
    }
    
    @Test public void basicSubClassOfInferenceTest() throws Exception {
        ModelTester tester = getBasicInferenceModelTester();
        StandaloneValidator validator = new StandaloneValidatorImpl();
        validator.setModelTesters(tester);
        
        InputStream stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RES_PACKAGE_NAME + "/testontology1.n3");
        assertNotNull(stream);
        validator.addOntology(stream, RDFFormat.TURTLE.getDefaultMIMEType(), "http://example.org/ontology1");
        List<String >urisList = validator.listOntologyUris();
        assertEquals(1,urisList.size());
        assertEquals("http://example.org/ontology1",urisList.get(0));
        
        stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RES_PACKAGE_NAME + "/testinstance1.n3");
        validator.validate(stream, RDFFormat.TURTLE.getDefaultMIMEType());        
    }
    
    private ModelTester getBasicInferenceModelTester() {
        return new ModelTester() {
            public ValidationReport performTests(Model unionModel, Model dataModel) throws ModelTesterException {
                URI instance1 = unionModel.createURI("http://example.org/inst1#Instance1");
                URI instance2 = unionModel.createURI("http://example.org/inst1#Instance2");
                
                URI class1 = unionModel.createURI("http://example.org/ontology1#Class1");
                URI class2 = unionModel.createURI("http://example.org/ontology1#Class2");
                
                
                assertTrue(dataModel.contains(instance1,org.ontoware.rdf2go.vocabulary.RDF.type,class1));
                assertTrue(dataModel.contains(instance2,org.ontoware.rdf2go.vocabulary.RDF.type,class2));
                
                assertEquals(2L,dataModel.size());
                
                assertTrue(unionModel.contains(class1, org.ontoware.rdf2go.vocabulary.RDF.type, RDFS.Class));
                assertTrue(unionModel.contains(class2, org.ontoware.rdf2go.vocabulary.RDF.type, RDFS.Class));
                assertTrue(unionModel.contains(class2, org.ontoware.rdf2go.vocabulary.RDFS.subClassOf, class1));
                assertTrue(unionModel.contains(instance1,org.ontoware.rdf2go.vocabulary.RDF.type,class1));
                assertTrue(unionModel.contains(instance2,org.ontoware.rdf2go.vocabulary.RDF.type,class2));
                
                // this is supposed to be inferred...
                assertTrue(unionModel.contains(instance2,org.ontoware.rdf2go.vocabulary.RDF.type,class1));
                
                //try {
                //    unionModel.writeTo(System.out, Syntax.Ntriples);
                //} catch (IOException ioe) {
                //    throw new RuntimeException(ioe);
               // }
                
                return null;
            }

            public void performTests(
                Model unionModel,
                Model dataModel,
                ValidationReport report) throws ModelTesterException {
            }            
        };
    }

    private void printSail(Sail sail) {
        RepositoryConnection connection = null;
        Repository repository = new SailRepository(sail);
        try {
            connection = repository.getConnection();
            RDFWriterFactory rdfWriterFactory = RDFWriterRegistry.getInstance().get(RDFFormat.TRIG);
            RDFWriter writer = rdfWriterFactory.getWriter(System.out);
            connection.export(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(RepositoryConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
