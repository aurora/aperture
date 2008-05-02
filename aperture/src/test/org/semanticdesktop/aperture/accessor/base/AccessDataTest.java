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

    protected static final String id1 = "file:file1";
    protected static final String id2 = "file:file2";
    protected static final String id3 = "file:file3";
    protected static final String folderid1 = "file:folder1";
    protected static final String folderid2 = "file:folder2";
    protected static final Set idset = new HashSet(Arrays.asList(id1,id2,id3,folderid1,folderid2));
    protected static final Set idset2 = new HashSet(Arrays.asList(folderid2,id3));
    protected static final String key1 = "key1";
    protected static final String key2 = "key2";
    protected static final String value1 = "value1";
    protected static final String value2 = "value2";
    protected static final String value3 = "value3";
    
    protected AccessData accessDataToTest;
    
    public void setUp(AccessData accessData) throws IOException {
        this.accessDataToTest = accessData;
        initializeAccessData(accessDataToTest);
    }
    
    private void initializeAccessData(AccessData accessData) throws IOException {
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
        
        assertEquals(5, accessDataToTest.getSize());
        assertEquals(3, accessDataToTest.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessDataToTest.getAggregatedIDs(folderid2).size());
        assertEquals(id3, (String)accessDataToTest.getAggregatedIDs(folderid2).iterator().next());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id3).size());
    }
    
    
    //public static void test(AccessData accessData) throws IOException {
    //    normalTest(accessData);
    //    testAddRemoveAggregates(accessData);
    //}
    
    public void testEntriesAndReferredIDs() throws IOException {        
        // check
        assertTrue(accessDataToTest.isKnownId(id1));
        assertTrue(accessDataToTest.isKnownId(id2));
        assertTrue(accessDataToTest.isKnownId(id3));
        assertEquals(value1, accessDataToTest.get(id1, key1));
        assertEquals(value2, accessDataToTest.get(id1, key2));
        assertEquals(value3, accessDataToTest.get(id2, key1));
        assertNull(accessDataToTest.get(id2, key2));
        assertNull(accessDataToTest.getReferredIDs(id1));

        Set ids = accessDataToTest.getStoredIDs();
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));
        assertTrue(ids.contains(id3));
        
        // alter data
        accessDataToTest.putReferredID(id1, id2);
        accessDataToTest.putReferredID(id1, id3);
        accessDataToTest.putReferredID(id3, id2);
        
        // check
        assertEquals(5, accessDataToTest.getSize());

        ids = accessDataToTest.getReferredIDs(id1);
        assertEquals(2, ids.size());
        assertTrue(ids.contains(id2));
        assertTrue(ids.contains(id3));
        assertFalse(ids.contains(id1));
        
        assertEquals(1, accessDataToTest.getReferredIDs(id3).size());
        assertNull(accessDataToTest.getReferredIDs(id2));
        
        // alter data
        accessDataToTest.remove(id1, key2);
        
        // check
        assertEquals(5, accessDataToTest.getSize());
        assertEquals(value1, accessDataToTest.get(id1, key1));
        assertNull(accessDataToTest.get(id1, key2));
        
        // alter data
        accessDataToTest.remove(id1);
        
        // check
        assertEquals(4, accessDataToTest.getSize());
        assertNull(accessDataToTest.get(id1, key1));
        assertNull(accessDataToTest.getReferredIDs(id1));
        assertEquals(value3, accessDataToTest.get(id2, key1));
        assertNotNull(accessDataToTest.get(id3, key1));
        assertTrue(accessDataToTest.getReferredIDs(id3).contains(id2));
        
        // alter data
        accessDataToTest.remove(id1);
        accessDataToTest.put(id1, key1, value1);
        accessDataToTest.put(id1, key1, value2);
        
        // check
        assertEquals(value2, accessDataToTest.get(id1, key1));
        
        // alter data
        accessDataToTest.putReferredID(id1, id2);
        accessDataToTest.putReferredID(id1, id3);
        
        // check
        assertEquals(2, accessDataToTest.getReferredIDs(id1).size());
        
        // alter data
        accessDataToTest.put(id1, key1, value1);
        accessDataToTest.removeReferredIDs(id1);
        
        // check
        assertTrue(accessDataToTest.isKnownId(id1));
        assertNull(accessDataToTest.getReferredIDs(id1));
        
        accessDataToTest.clear();
    }

    public void testAddRemoveAggregates() throws IOException {        
        // remove an aggregation link
        accessDataToTest.removeAggregatedID(folderid2, id3);
        
        // check
        assertEquals(5, accessDataToTest.getSize());
        assertEquals(3, accessDataToTest.getAggregatedIDs(folderid1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(folderid2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id3).size());
        
        // add the aggregation link once more
        accessDataToTest.putAggregatedID(folderid2, id3);
        
        assertEquals(5, accessDataToTest.getSize());
        assertEquals(3, accessDataToTest.getAggregatedIDs(folderid1).size());
        assertEquals(1, accessDataToTest.getAggregatedIDs(folderid2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id2).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id3).size());
        
        // remove the subfolder (should cause recursive removal of the file)
        // the folder should also disappear from the list of the children of the superfolder
        accessDataToTest.remove(folderid2);
        
        // check if the recursive removal went as expected
        assertEquals(3, accessDataToTest.getSize());
        assertEquals(2, accessDataToTest.getAggregatedIDs(folderid1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id1).size());
        assertEquals(0, accessDataToTest.getAggregatedIDs(id2).size());
        
        accessDataToTest.clear();
    }
    
    // There must be no cycles in the aggregation graph, this is guaranteed by the
    // fact that each is can have at most one parent, this test checks if the implementation
    // enforces this guarantee
    public void testAggregationCycles() throws IOException {
        // the id3 is aggregated within folderid2
        accessDataToTest.putAggregatedID(folderid1, id3);
        // now it must be removed from under the folderid2 and reattached under folderid1
        assertEquals(0,accessDataToTest.getAggregatedIDs(folderid2).size());
        assertEquals(4,accessDataToTest.getAggregatedIDs(folderid1).size());
        Set id1AggregatedFiles = accessDataToTest.getAggregatedIDs(folderid1);
        for (Object obj : id1AggregatedFiles) {
            String string = obj.toString();
            assertTrue(string.equals(id1) || string.equals(id2) || string.equals(folderid2) 
                || string.equals(id3));
        }
        accessDataToTest.clear();
    }
    
    public void testTouchedAndUntouched() throws IOException {                
        accessDataToTest.store();
        
        accessDataToTest.initialize();
        // at the beginning all resources are supposed to be untouched
        int counter = 0;
        Iterator iterator = accessDataToTest.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            // check if the untouched resources belong to the set
            assertTrue(idset.contains(id));
        }
        // and check if their number is OK
        assertEquals(counter,idset.size());
        
        // this should touch a resource
        String newValue2 = accessDataToTest.get(id1,key2);
        assertEquals(newValue2, value2);
        accessDataToTest.touch(id1);
        
        // after one resource has been touched, it should no longer appear on the
        // list of touched resources
        counter = 0;
        iterator = accessDataToTest.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            assertTrue(idset.contains(id));
            assertFalse(id.equals(id1));
        }
        // note the -1, the number of encountered untouched resources should be smaller by one
        assertEquals(counter,idset.size() - 1);
        
        // now remove all untouched resources
        accessDataToTest.removeUntouchedIDs();
        // which should leave us with a single resource in the AccessData instance
        assertEquals(1,accessDataToTest.getSize());
        
        accessDataToTest.clear();
    }
    
    public void testTouchRecursively() throws IOException {        

        
        accessDataToTest.store();
        
        accessDataToTest.initialize();
        // at the beginning all resources are supposed to be untouched
        int counter = 0;
        Iterator iterator = accessDataToTest.getUntouchedIDsIterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            // check if the untouched resources belong to the set
            assertTrue(idset.contains(id));
        }
        // and check if their number is OK
        assertEquals(counter,idset.size());
        
        // this should touch a resource recursively
        accessDataToTest.touchRecursively(folderid2);
        
        // after one resource has been touched, it should no longer appear on the
        // list of touched resources
        counter = 0;
        iterator = accessDataToTest.getUntouchedIDsIterator();
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
        accessDataToTest.removeUntouchedIDs();
        // which should leave us with two resources in the accessData instance
        assertEquals(2,accessDataToTest.getSize());
        
        accessDataToTest.clear();
    }
    
    public void testGetAggregationClosure() throws IOException {    
        // first let's get the closure of the top folder
        int counter = 0;
        Iterator iterator = accessDataToTest.getAggregatedIDsClosure(folderid1);
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
        iterator = accessDataToTest.getAggregatedIDsClosure(folderid2);
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            counter++;
            assertTrue(idset2.contains(id));
        }
        // this should only yield 2 ids
        assertEquals(counter,2);
        
        accessDataToTest.clear();
    }
}
