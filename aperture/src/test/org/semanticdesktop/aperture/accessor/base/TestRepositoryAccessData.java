/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.ApertureTestBase;

public class TestRepositoryAccessData extends ApertureTestBase {

	private Repository repository;
	
	private RepositoryAccessData accessData;
	
	public void setUp() throws SailInitializationException {
		MemoryStore memoryStore = new MemoryStore();
		repository = new Repository(memoryStore);
		repository.initialize();
		accessData = new RepositoryAccessData(repository, new URIImpl("urn:test:dummy"));
	}
	
	public void tearDown() {
		repository.shutDown();
		repository = null;
	}
	
	public void testAutoCommitting() throws SailInitializationException, SailUpdateException, IOException {
		repository.setAutoCommit(true);
		AccessDataTest.test(accessData);
	}
	
	public void testNonAutoCommitting() throws SailInitializationException, SailUpdateException, IOException {
		repository.setAutoCommit(false);
		AccessDataTest.test(accessData);
	}
}
