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

/**
 * Tests presented at the Karlsruhe meeting of the Nepomuk Task-Force ontologies at 15.08.2007
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class KarlsruheDemoTests extends ValidatorTest {
    
    @Test
    public void testEx1() throws Exception {
        doTest("ex1.ont.n3","uri:test","ex1.inst.n3",1);
    }
    
    @Test
    public void testEx2() throws Exception {
        doTest("ex2.ont.n3","uri:test","ex2.inst.n3",6);
    }
    
    @Test
    public void testEx3() throws Exception {
        doTest("ex3.ont.n3","uri:test","ex3.inst.n3",1);
    }
    
    @Test
    public void testEx4() throws Exception {
        doTest("ex4.ont.n3","uri:test","ex4.inst.n3",2);
    }
    
    @Test
    public void testEx5() throws Exception {
        doTest("ex5.ont.n3","uri:test","ex5.inst.n3",2);
    }
    
    @Test
    public void testEx6() throws Exception {
        doTest("ex6.ont.n3","uri:test","ex6.inst.n3",2);
    }
}
