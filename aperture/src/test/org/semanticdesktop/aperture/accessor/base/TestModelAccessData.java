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
import org.semanticdesktop.aperture.ApertureTestBase;

public class TestModelAccessData extends AccessDataTest {

	private Model model;
	
	private ModelAccessData accessData;
	
	public void setUp() throws ModelException, IOException {
        model = createModel();
		super.setUp(new ModelAccessData(model));
	}
	
	public void tearDown() {
		model.close();
		model = null;
	}
}
