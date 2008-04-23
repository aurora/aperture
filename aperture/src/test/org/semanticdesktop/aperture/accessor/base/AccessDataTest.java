/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;

/**
 * AccessDataTest defines some tests that should succeed on all types of AccessData implementations.
 */
public class AccessDataTest extends ApertureTestBase {

    public static void test(AccessData accessData) throws IOException {
        normalTest(accessData);
        testAddRemoveAggregates(accessData);
    }
    
    private static void normalTest(AccessData accessData) throws IOException {
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
        
        accessData.clear();
    }

    private static void testAddRemoveAggregates(AccessData accessData) throws IOException {
        String id1 = "file:file1";
        String id2 = "file:file2";
        String id3 = "file:file3";
        String folderid1 = "file:folder1";
        String folderid2 = "file:folder2";
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";
        
        accessData.initialize();
        
        // check
        assertEquals(0, accessData.getSize());

        // create a a folder with two files and a subfolder, the subfolder contains a third file
        accessData.put(folderid1, key1, value1);
        accessData.put(id1, key1, value1);
        accessData.put(id1, key2, value2);
        accessData.put(id2, key1, value3);
        accessData.putAggregatedID(folderid1, id1);
        accessData.putAggregatedID(folderid1, id2);
        accessData.put(folderid2, key2, value3);
        accessData.putAggregatedID(folderid1, folderid2);
        accessData.put(id3, key1, value2);
        accessData.putAggregatedID(folderid2, id3);
        
        // check 
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessData.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        // remove an aggregation link
        accessData.removeAggregatedID(folderid2, id3);
        
        // check
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(0, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        // add the aggregation link once more
        accessData.putAggregatedID(folderid2, id3);
        
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        // remove the subfolder (should cause recursive removal of the file)
        // the folder should also disappear from the list of the children of the superfolder
        accessData.remove(folderid2);
        
        // check if the recursive removal went as expected
        assertEquals(3, accessData.getSize());
        assertEquals(2, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        
        accessData.clear();
    }
    
    public static void testTouchedAndUntouched(AccessData accessData) throws IOException {
        String id1 = "file:file1";
        String id2 = "file:file2";
        String id3 = "file:file3";
        String folderid1 = "file:folder1";
        String folderid2 = "file:folder2";
        Set idset = new HashSet(Arrays.asList(id1,id2,id3,folderid1,folderid2));
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";
        
        accessData.initialize();
        
        // check
        assertEquals(0, accessData.getSize());

        // alter data
        accessData.put(folderid1, key1, value1);
        accessData.put(id1, key1, value1);
        accessData.put(id1, key2, value2);
        accessData.put(id2, key1, value3);
        accessData.putAggregatedID(folderid1, id1);
        accessData.putAggregatedID(folderid1, id2);
        accessData.put(folderid2, key2, value3);
        accessData.putAggregatedID(folderid1, folderid2);
        accessData.put(id3, key1, value2);
        accessData.putAggregatedID(folderid2, id3);
        
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessData.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        accessData.store();
        
        accessData.initialize();
        // at the beginning all resources are supposed to be untouched
        int counter = 0;
        Iterator iterator = accessData.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            // check if the untouched resources belong to the set
            assertTrue(idset.contains(id));
        }
        // and check if their number is OK
        assertEquals(counter,idset.size());
        
        // this should touch a resource
        String newValue2 = accessData.get(id1,key2);
        assertEquals(newValue2, value2);
        
        // after one resource has been touched, it should no longer appear on the
        // list of touched resources
        counter = 0;
        iterator = accessData.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            assertTrue(idset.contains(id));
            assertFalse(id.equals(id1));
        }
        // note the -1, the number of encountered untouched resources should be smaller by one
        assertEquals(counter,idset.size() - 1);
        
        // now remove all untouched resources
        accessData.removeUntouchedIDs();
        // which should leave us with a single resource in the AccessData instance
        assertEquals(1,accessData.getSize());
        
        accessData.clear();
    }
    
    public static void testTouchRecursively(AccessData accessData) throws IOException {
        String id1 = "file:file1";
        String id2 = "file:file2";
        String id3 = "file:file3";
        String folderid1 = "file:folder1";
        String folderid2 = "file:folder2";
        Set idset = new HashSet(Arrays.asList(id1,id2,id3,folderid1,folderid2));
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";
        
        accessData.initialize();
        
        // check
        assertEquals(0, accessData.getSize());

        // alter data
        accessData.put(folderid1, key1, value1);
        accessData.put(id1, key1, value1);
        accessData.put(id1, key2, value2);
        accessData.put(id2, key1, value3);
        accessData.putAggregatedID(folderid1, id1);
        accessData.putAggregatedID(folderid1, id2);
        accessData.put(folderid2, key2, value3);
        accessData.putAggregatedID(folderid1, folderid2);
        accessData.put(id3, key1, value2);
        accessData.putAggregatedID(folderid2, id3);
        
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessData.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        accessData.store();
        
        accessData.initialize();
        // at the beginning all resources are supposed to be untouched
        int counter = 0;
        Iterator iterator = accessData.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            // check if the untouched resources belong to the set
            assertTrue(idset.contains(id));
        }
        // and check if their number is OK
        assertEquals(counter,idset.size());
        
        // this should touch a resource recursively
        accessData.touchRecursively(folderid2);
        
        // after one resource has been touched, it should no longer appear on the
        // list of touched resources
        counter = 0;
        iterator = accessData.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            assertTrue(idset.contains(id));
            assertFalse(id.equals(id3));
            assertFalse(id.equals(folderid2));
        }
        // note the -1, the number of encountered untouched resources should be smaller by one
        assertEquals(counter,idset.size() - 2);
        
        // now remove all untouched resources
        accessData.removeUntouchedIDs();
        // which should leave us with two resources in the accessData instance
        assertEquals(2,accessData.getSize());
        
        accessData.clear();
    }
    
    public static void testGetAggregationClosure(AccessData accessData) throws IOException {
        String id1 = "file:file1";
        String id2 = "file:file2";
        String id3 = "file:file3";
        String folderid1 = "file:folder1";
        String folderid2 = "file:folder2";
        Set idset = new HashSet(Arrays.asList(id1,id2,id3,folderid1,folderid2));
        Set idset2 = new HashSet(Arrays.asList(folderid2,id3));
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "value3";
        
        accessData.initialize();
        
        // check
        assertEquals(0, accessData.getSize());

        // alter data
        accessData.put(folderid1, key1, value1);
        accessData.put(id1, key1, value1);
        accessData.put(id1, key2, value2);
        accessData.put(id2, key1, value3);
        accessData.putAggregatedID(folderid1, id1);
        accessData.putAggregatedID(folderid1, id2);
        accessData.put(folderid2, key2, value3);
        accessData.putAggregatedID(folderid1, folderid2);
        accessData.put(id3, key1, value2);
        accessData.putAggregatedID(folderid2, id3);
        
        assertEquals(5, accessData.getSize());
        assertEquals(3, accessData.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessData.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessData.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessData.getAggregatedIDs(id1).size());
        assertEquals(0, accessData.getAggregatedIDs(id2).size());
        assertEquals(0, accessData.getAggregatedIDs(id3).size());
        
        // first let's get the closure of the top folder
        int counter = 0;
        Iterator iterator = accessData.getAggregatedIDsClosure(folderid1);
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            // it should contain all ids 
            assertTrue(idset.contains(id));
        }
        // and check if their number is OK
        assertEquals(counter,idset.size());
                
        // then get the closure of the subfolder
        counter = 0;
        iterator = accessData.getAggregatedIDsClosure(folderid2);
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            assertTrue(idset2.contains(id));
        }
        // this should only yield 2 ids
        assertEquals(counter,2);
        
        accessData.clear();
    }
}
