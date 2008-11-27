/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.File;
import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.FileUtil;

public class TestFileAccessData extends AccessDataTest {

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
		super.setUp(new FileAccessData(accessDataFile));
	}
	
    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }
    
	public void testFillStoreAndLoad() throws IOException {
		accessDataToTest.put("urn:test", AccessData.DATE_KEY, "12");
		accessDataToTest.putReferredID(id1, id3);
		accessDataToTest.putReferredID(id3, id2);
		accessDataToTest.store();

		// load
		AccessData accessData = new FileAccessData(accessDataFile);
		accessData.initialize();
		
		// check if the aggregated id's have been stored and read correctly
		
		// originally there were 5 id's, the sixth one is urn:test
		assertEquals(6, accessDataToTest.getSize());
        assertEquals(3, accessDataToTest.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessDataToTest.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessDataToTest.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id3).size());
		
        // check tif the value is correct
		assertEquals("12",accessData.get("urn:test", AccessData.DATE_KEY));
		assertEquals(value1,accessData.get(id1, key1));
		assertEquals(value2,accessData.get(id1, key2));
		assertEquals(value3,accessData.get(id2, key1));
		
		assertEquals(1, accessData.getReferredIDs(id1).size());
		assertEquals(id3, accessData.getReferredIDs(id1).iterator().next().toString());
		
		assertEquals(1, accessData.getReferredIDs(id3).size());
		assertEquals(id2, accessData.getReferredIDs(id3).iterator().next().toString());

		
		
		
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
