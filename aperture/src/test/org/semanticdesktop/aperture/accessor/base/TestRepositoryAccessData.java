/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryImpl;
import org.openrdf.sail.SailException;
import org.openrdf.sail.SailInitializationException;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticdesktop.aperture.ApertureTestBase;

public class TestRepositoryAccessData extends ApertureTestBase {

	private Repository repository;
	
	private RepositoryAccessData accessData;
	
	public void setUp() throws SailInitializationException, SailException {
		MemoryStore memoryStore = new MemoryStore();
		repository = new RepositoryImpl(memoryStore);
		repository.initialize();
		accessData = new RepositoryAccessData(repository, new URIImpl("urn:test:dummy"));
	}
	
	public void tearDown() {
		accessData.shutDown();
		repository = null;
	}
	
	public void testAutoCommitting() throws SailInitializationException, SailException, IOException {
		accessData.setAutoCommit(true);
		AccessDataTest.test(accessData);
	}
	
	public void testNonAutoCommitting() throws SailInitializationException, SailException, IOException {
		accessData.setAutoCommit(false);
		AccessDataTest.test(accessData);
	}
}
