/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Set;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;

/**
 * AccessDataTest defines some tests that should succeed on all types of AccessData implementations.
 */
public class AccessDataTest extends ApertureTestBase {

	public static void test(AccessData accessData) throws IOException {
		String id1 = "file:file1";
		String id2 = "file:file2";
		String id3 = "file:file3";
		String key1 = "key1";
		String key2 = "key2";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";
		
		accessData.initialize();

		// check
		assertEquals(0, accessData.getSize());

		// alter data
		accessData.put(id1, key1, value1);
		accessData.put(id1, key2, value2);
		accessData.put(id2, key1, value3);

		// check
		assertEquals(2, accessData.getSize());
		assertTrue(accessData.isKnownId(id1));
		assertTrue(accessData.isKnownId(id2));
		assertFalse(accessData.isKnownId(id3));
		assertEquals(value1, accessData.get(id1, key1));
		assertEquals(value2, accessData.get(id1, key2));
		assertEquals(value3, accessData.get(id2, key1));
		assertNull(accessData.get(id2, key2));
		assertNull(accessData.getReferredIDs(id1));

		Set ids = accessData.getStoredIDs();
		assertTrue(ids.contains(id1));
		assertTrue(ids.contains(id2));
		assertFalse(ids.contains(id3));
		
		// alter data
		accessData.putReferredID(id1, id2);
		accessData.putReferredID(id1, id3);
		accessData.putReferredID(id3, id2);
		
		// check
		assertEquals(3, accessData.getSize());

		ids = accessData.getReferredIDs(id1);
		assertEquals(2, ids.size());
		assertTrue(ids.contains(id2));
		assertTrue(ids.contains(id3));
		assertFalse(ids.contains(id1));
		
		assertEquals(1, accessData.getReferredIDs(id3).size());
		assertNull(accessData.getReferredIDs(id2));
		
		// alter data
		accessData.remove(id1, key2);
		
		// check
		assertEquals(3, accessData.getSize());
		assertEquals(value1, accessData.get(id1, key1));
		assertNull(accessData.get(id1, key2));
		
		// alter data
		accessData.remove(id1);
		
		// check
		assertEquals(2, accessData.getSize());
		assertNull(accessData.get(id1, key1));
		assertNull(accessData.getReferredIDs(id1));
		assertEquals(value3, accessData.get(id2, key1));
		assertNull(accessData.get(id3, key1));
		assertTrue(accessData.getReferredIDs(id3).contains(id2));
		
		// alter data
		accessData.remove(id1);
		accessData.put(id1, key1, value1);
		accessData.put(id1, key1, value2);
		
		// check
		assertEquals(value2, accessData.get(id1, key1));
		
		// alter data
		accessData.putReferredID(id1, id2);
		accessData.putReferredID(id1, id3);
		
		// check
		assertEquals(2, accessData.getReferredIDs(id1).size());
		
		// alter data
		accessData.put(id1, key1, value1);
		accessData.removeReferredIDs(id1);
		
		// check
		assertTrue(accessData.isKnownId(id1));
		assertNull(accessData.getReferredIDs(id1));
		
		accessData.store();
	}
}
