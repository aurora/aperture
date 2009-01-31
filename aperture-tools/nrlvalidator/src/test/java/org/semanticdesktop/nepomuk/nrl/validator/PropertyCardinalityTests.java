package org.semanticdesktop.nepomuk.nrl.validator;

import org.junit.Test;

public class PropertyCardinalityTests extends ValidatorTest {
    
    @Test
    public void testMinimumCardinality() throws Exception {
        doTest("/testOntologyCard1.n3", "http://example.org/ontology-card", 
            "/testInstance-MinCardinality.n3", 1);
    }
    
    @Test
    public void testMaximumCardinality() throws Exception {
        doTest("/testOntologyCard1.n3", "http://example.org/ontology-card", 
            "/testInstance-MaxCardinality.n3", 1);
    }
    
    @Test
    public void testMinMaxCardinality() throws Exception {
        doTest("/testOntologyCard1.n3", "http://example.org/ontology-card", 
            "/testInstance-MinMaxCardinality.n3", 2);
    }
    
    @Test
    public void testCardinality() throws Exception {
        doTest("/testOntologyCard1.n3", "http://example.org/ontology-card", 
            "/testInstance-Cardinality.n3", 1);
    }
    
    @Test
    public void testDatatypeCardinality() throws Exception {
        doTest("/testOntologyCard2.n3", "http://example.org/ontology-card", 
            "/testInstance-DatatypeCardinality.n3", 4);
    }
    
}
