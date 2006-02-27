/*
 * Copyright (c) 2005 - 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

import java.util.Date;

import junit.framework.TestCase;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.rdf.MultipleValuesException;

public class TestSesameRDFContainer extends TestCase {

	public static final String TEST_OBJECT_URI = "urn:test:dataobject";

	public static final String TEST_RESOURCE_URI = "urn:test:objectresource";

	public static final String PROP_BOOL = "http://example.com/ont/bool";

	public static final String PROP_DATE = "http://example.com/ont/date";

	public static final String PROP_INT = "http://example.com/ont/int";

	public static final URI PROP_STRING_URI;

	public static final URI PROP_RESOURCE_URI;

	public static final URI PROP_BOOL_URI;

	public static final URI PROP_DATE_URI;

	public static final URI PROP_INT_URI;

	public static final URI TEST_RESOURCE;

	private Date MYDATE = new Date();

	static ValueFactoryImpl val = new ValueFactoryImpl();

	static {
		PROP_STRING_URI = val.createURI(RDFS.LABEL.toString());
		PROP_RESOURCE_URI = val.createURI(RDFS.SEEALSO.toString());
		PROP_BOOL_URI = val.createURI(PROP_BOOL);
		PROP_DATE_URI = val.createURI(PROP_DATE);
		PROP_INT_URI = val.createURI(PROP_INT);
		TEST_RESOURCE = val.createURI(TEST_RESOURCE_URI);
	}

	public void testPutBasicTypes() throws Exception {
		SesameRDFContainer container = (SesameRDFContainer) new SesameRDFContainerFactory()
				.newInstance(TEST_OBJECT_URI);
		Repository repository = container.getRepository();
		Resource subject = container.getDescribedUri();

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

	// The following tests test whether SesameRDFContainer properly applies its map semantics, i.e. whether
	// the put really overwrites previous values, whether the get immediately returns results, regardless of
	// whether the Repository is auto-committing, etc.

	public void testAutoCommittingRepository() throws Exception {
		testMapSemantics(true);
	}

	public void testNonAutoCommittingRepository() throws Exception {
		testMapSemantics(false);
	}

	private void testMapSemantics(boolean autoCommitting) throws Exception {
		MemoryStore memoryStore = new MemoryStore();
		Repository repository = new Repository(memoryStore);
		repository.initialize();
		repository.setAutoCommit(autoCommitting);
		testPutSemantics(repository);
	}

	private void testPutSemantics(Repository repository) {
		URI subject = new URIImpl("urn:test:dummy");
		SesameRDFContainer container = new SesameRDFContainer(repository, subject);
		ValueFactory valFac = repository.getSail().getValueFactory();
		
		// check whether the current value is propertly overwritten
		assertEquals(null, container.getString(PROP_STRING_URI));
		
		container.put(PROP_STRING_URI, valFac.createLiteral("label"));
		assertEquals("label", container.getString(PROP_STRING_URI));
		
		container.put(PROP_STRING_URI, valFac.createLiteral("label2"));
		assertEquals("label2", container.getString(PROP_STRING_URI));
		
		// check that a MultipleValuesException is thrown when "add" is used instead of "put"
		container.add(PROP_STRING_URI, valFac.createLiteral("label3"));
		
		try {
			container.getString(PROP_STRING_URI);
			fail();
		}
		catch (MultipleValuesException e) {
			// this is the required behaviour
		}
		
		// check that the put now also throws a MultipleValuesException
		try {
			container.put(PROP_STRING_URI, valFac.createLiteral("label4"));
			fail();
		}
		catch (MultipleValuesException e) {
			// this is the required behaviour
		}
	}
}
