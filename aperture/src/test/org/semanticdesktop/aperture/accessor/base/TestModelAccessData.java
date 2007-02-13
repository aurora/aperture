/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.semanticdesktop.aperture.ApertureTestBase;

public class TestModelAccessData extends ApertureTestBase {

	private Model model;
	
	private ModelAccessData accessData;
	
	public void setUp() throws ModelException {
		URI contextURI = URIImpl.createURIWithoutChecking("urn:test:dummy");
		model = new RepositoryModel(contextURI,false);
		accessData = new ModelAccessData(model);
	}
	
	public void tearDown() {
		model.close();
		model = null;
	}
	
	public void testAccessData() throws IOException {
		AccessDataTest.test(accessData);
	}
}
