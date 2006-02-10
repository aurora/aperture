/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.FileUtil;

public class TestAccessDataBase extends TestCase {

	private static final String TMP_SUBDIR = "TestFileAccessor.tmpDir";

	private File tmpDir;

	private File accessDataFile;

	public void setUp() throws IOException {
		tmpDir = new File(System.getProperty("java.io.tmpdir"), TMP_SUBDIR)
				.getCanonicalFile();
		FileUtil.deltree(tmpDir);
		tmpDir.mkdir();
		accessDataFile = File.createTempFile("file-", ".txt", tmpDir);
		accessDataFile.delete();
	}
	
    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }

	public void testFillStoreAndLoad() throws IOException {
		// new object
		FileAccessData accessDataBase = new FileAccessData(accessDataFile);
		accessDataBase.initialize();
		accessDataBase.put("urn:test", AccessData.DATE_KEY, "12");
		accessDataBase.store();

		// load
		accessDataBase = new FileAccessData(accessDataFile);
		accessDataBase.initialize();
		String value = accessDataBase.get("urn:test", AccessData.DATE_KEY);
		assertEquals("12", value);
	}

}
