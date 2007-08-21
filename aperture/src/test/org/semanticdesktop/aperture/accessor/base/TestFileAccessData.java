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

public class TestFileAccessData extends TestCase {

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
    	AccessDataTest.test(accessData);
    }
    
	public void testFillStoreAndLoad() throws IOException {
		// new object
		FileAccessData accessData = new FileAccessData(accessDataFile);
		accessData.initialize();
		accessData.put("urn:test", AccessData.DATE_KEY, "12");
		accessData.store();

		// load
		accessData = new FileAccessData(accessDataFile);
		accessData.initialize();
		String value = accessData.get("urn:test", AccessData.DATE_KEY);
		assertEquals("12", value);
	}
	
	public void testAutoSaveFeature() throws Exception {
	    long beginLastModified = accessDataFile.lastModified();
	    FileAccessData accessData = new FileAccessData(accessDataFile, 100);
	    accessData.initialize();
	    
	    for (int i = 0; i < 100; i++) {
	        accessData.put("urn:test", AccessData.DATE_KEY, String.valueOf(i));
	        int j = Integer.parseInt(accessData.get("urn:test", AccessData.DATE_KEY));
	        assertEquals(i,j);
	        Thread.sleep(1);
	    }
	    
	    long endLastModified = accessDataFile.lastModified();
	    // an autosave should have occured in the meantime, so the lastModifiedDate should be later
	    assertTrue(endLastModified > beginLastModified);
	    
	    accessData.store();
	}
}
