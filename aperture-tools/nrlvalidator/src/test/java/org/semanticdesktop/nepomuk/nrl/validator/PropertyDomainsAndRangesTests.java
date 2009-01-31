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

public class PropertyDomainsAndRangesTests extends ValidatorTest {
    
    @Test
    public void testDomains() throws Exception {
        doTest("/testontology2.n3","http://example.org/ontology2","/testinstance2-1.n3",1);
    }
    
    @Test
    public void testRanges() throws Exception {
        doTest("/testontology2.n3","http://example.org/ontology2","/testinstance2-2.n3",1);
    }
    
    @Test
    public void testDatatypeRanges() throws Exception {
        doTest("/testontology2.n3","http://example.org/ontology2","/testinstance2-3.n3",2);
    }
    
    @Test
    public void testIntegerDatatypeRanges() throws Exception {
        doTest("/testontology2.n3","http://example.org/ontology2","/testinstance2-4.n3",2);
    }
    
    @Test
    public void testNonExistentProperties() throws Exception {
        doTest("/testontology2.n3","http://example.org/ontology2","/testinstance2-5.n3",4);
    }
}
