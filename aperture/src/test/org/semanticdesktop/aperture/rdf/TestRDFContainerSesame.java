/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.rdf;

import java.net.URI;
import java.util.Date;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

import junit.framework.TestCase;

public class TestRDFContainerSesame extends TestCase {

    public final static String TEST_OBJECT_URI = "urn:test:dataobject";
    public final static String TEST_RESOURCE_URI = "urn:test:objectresource";
    public final static String PROP_BOOL = "http://example.com/ont/bool";
    public final static String PROP_DATE = "http://example.com/ont/date";
    public final static String PROP_INT = "http://example.com/ont/int";
    public static URI PROP_STRING_URI;
    public static URI PROP_RESOURCE_URI;
    public static URI PROP_BOOL_URI;
    public static URI PROP_DATE_URI;
    public static URI PROP_INT_URI;
    public static URI TEST_RESOURCE;
    public static org.openrdf.model.URI PROP_STRING_URI_S;
    public static org.openrdf.model.URI PROP_RESOURCE_URI_S;
    public static org.openrdf.model.URI PROP_BOOL_URI_S;
    public static org.openrdf.model.URI PROP_DATE_URI_S;
    public static org.openrdf.model.URI PROP_INT_URI_S;
    Date MYDATE = new Date();
    static ValueFactoryImpl val = new ValueFactoryImpl();

    
    static {
        try {
            PROP_STRING_URI = new URI(RDFS.LABEL.toString());
            PROP_RESOURCE_URI = new URI(RDFS.SEEALSO.toString());
            PROP_BOOL_URI = new URI(PROP_BOOL);
            PROP_DATE_URI = new URI(PROP_DATE);
            PROP_INT_URI = new URI(PROP_INT);
            TEST_RESOURCE = new URI(TEST_RESOURCE_URI);
            /**
             * the sesame URI objects
             */
            
            PROP_STRING_URI_S = val.createURI(RDFS.LABEL.toString());
            PROP_RESOURCE_URI_S = val.createURI(RDFS.SEEALSO.toString());
            PROP_BOOL_URI_S = val.createURI(PROP_BOOL);
            PROP_DATE_URI_S = val.createURI(PROP_DATE);
            PROP_INT_URI_S = val.createURI(PROP_INT);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
    
    private RDFContainerSesame container; 
    private Repository repository;
    private Resource subject;
    
    protected void setUp() throws Exception
    {
        container =  new RDFContainerSesame(TEST_OBJECT_URI);
        repository = container.getRepository();
        subject = container.getResource();
    }

    protected void tearDown() throws Exception
    {
        //container.
    }
    
    /** 
     * something to look in system.out
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        TestRDFContainerSesame test = new TestRDFContainerSesame();
        test.setUp();
        test.testPutBasicTypes();
        test.repository.extractStatements();
    }
    
    public void testPutBasicTypes() throws Exception {
        container.put(PROP_STRING_URI, "label");
        container.put(PROP_BOOL_URI, true);
        container.put(PROP_DATE_URI, MYDATE);
        container.put(PROP_INT_URI, 23);
        container.put(PROP_RESOURCE_URI, TEST_RESOURCE);
        // check
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI_S, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI_S, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI_S, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI_S, val.createLiteral("label")));
    }

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 14:57:02  leo_sauermann
 * added testcase for the RDFContainerSesame
 *
 */