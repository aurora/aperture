/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;
import org.semanticdesktop.aperture.util.FileUtil;

public class TestNativeStoreModelAccessData extends AccessDataTest {

	private static final String TMP_SUBDIR = "TestNativeStoreAccessdata.tmpDir";

	private File tmpDir;

	private File accessDataFile;
	
	private Model model;

	public void setUp() throws IOException, SailException, RepositoryException {
		tmpDir = new File(System.getProperty("java.io.tmpdir"), TMP_SUBDIR)
				.getCanonicalFile();
		FileUtil.deltree(tmpDir);
		tmpDir.mkdir();
		NativeStore store = new NativeStore(tmpDir);
		Repository repo = new SailRepository(store);
		repo.initialize();
		model = new RepositoryModel(repo);
		model.open();
		super.setUp(new ModelAccessData(model));
	}
	
    public void tearDown() {
        // delete the temporary folder
        model.close();
        FileUtil.deltree(tmpDir);
    }
}
