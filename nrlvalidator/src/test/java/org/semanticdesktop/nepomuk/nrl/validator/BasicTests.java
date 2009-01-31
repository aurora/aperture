package org.semanticdesktop.nepomuk.nrl.validator;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.ontoware.rdf2go.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testres.TESTRESOURCES;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BasicTests extends ValidatorTest {
    
    @Test
    public void testIsDefinedBy() throws Exception {
        doTest("testontology1.n3","http://example.org/ontology1","testinstance1isdefinedby.n3",1);
    }
    
    @Test
    public void testBlankNodeSubject() throws Exception {
        doTest("testontology1.n3","http://example.org/ontology1","testinstance1blanksubject.n3",1);
    }
    
    @Test
    public void testBlankNodeObject() throws Exception {
        doTest("testontology1.n3","http://example.org/ontology1","testInstanceBlankObjects.n3",2);
    }
    
    @Test
    public void testContainers() throws Exception {
        doTest("testontology1.n3","http://example.org/ontology1","testInstanceContainers.n3",5);
    }
}
