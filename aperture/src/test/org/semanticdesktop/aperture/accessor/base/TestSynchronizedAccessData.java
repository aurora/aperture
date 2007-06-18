/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.FileUtil;

public class TestSynchronizedAccessData extends TestCase {

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

    public void testInputOutput() throws IOException {
    	FileAccessData accessData = new FileAccessData(null);
        SynchronizedAccessData synchronizedAccessData = new SynchronizedAccessData(accessData);
    	AccessDataTest.test(synchronizedAccessData);
    }
    
	public void testFillStoreAndLoad() throws IOException {
		// new object
		FileAccessData accessData = new FileAccessData(accessDataFile);
        SynchronizedAccessData synchronizedAccessData = new SynchronizedAccessData(accessData);
		synchronizedAccessData.initialize();
        synchronizedAccessData.put("urn:test", AccessData.DATE_KEY, "12");
        synchronizedAccessData.store();

		// load
		accessData = new FileAccessData(accessDataFile);
        synchronizedAccessData = new SynchronizedAccessData(accessData);
        synchronizedAccessData.initialize();
		String value = synchronizedAccessData.get("urn:test", AccessData.DATE_KEY);
		assertEquals("12", value);
	}
}
