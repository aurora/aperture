package org.semanticdesktop.aperture.rdf.sesame;

import java.util.Date;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainerFactory;

import junit.framework.TestCase;

public class TestSesameRDFContainer extends TestCase {

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

    Date MYDATE = new Date();

    static ValueFactoryImpl val = new ValueFactoryImpl();

    static {
        PROP_STRING_URI = val.createURI(RDFS.LABEL.toString());
        PROP_RESOURCE_URI = val.createURI(RDFS.SEEALSO.toString());
        PROP_BOOL_URI = val.createURI(PROP_BOOL);
        PROP_DATE_URI = val.createURI(PROP_DATE);
        PROP_INT_URI = val.createURI(PROP_INT);
        TEST_RESOURCE = val.createURI(TEST_RESOURCE_URI);
    }

    private SesameRDFContainer container;

    private Repository repository;

    private Resource subject;

    protected void setUp() throws Exception {
        container = (SesameRDFContainer) new SesameRDFContainerFactory().newInstance(TEST_OBJECT_URI);
        repository = container.getRepository();
        subject = container.getDescribedUri();
    }

    protected void tearDown() throws Exception {
        // container.
    }

    public void testPutBasicTypes() throws Exception {
        container.put(PROP_STRING_URI, "label");
        container.put(PROP_BOOL_URI, true);
        container.put(PROP_DATE_URI, MYDATE);
        container.put(PROP_INT_URI, 23);
        container.put(PROP_RESOURCE_URI, TEST_RESOURCE);
        // check
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI, val.createLiteral("label")));
        assertTrue(repository.hasStatement(subject, PROP_STRING_URI, val.createLiteral("label")));
    }
}
