/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.semanticdesktop.aperture.accessor.AccessData;

/**
 * A synchronized wrapper around an AccessData implementation.
 */
public class SynchronizedAccessData implements AccessData {

    /** The wrapper accessData implementation */
    private AccessData accessData;
    
    /**
     * The default constructor.
     * @param accessData The access data to be wrapped.
     */
    public SynchronizedAccessData(AccessData accessData) {
        this.accessData = accessData;
    }
    
    /**
     * @see AccessData#clear()
     */
    public synchronized void clear() throws IOException {
        accessData.clear();
    }

    /**
     * @see AccessData#get(String, String)
     */
    public synchronized String get(String id, String key) {
        return accessData.get(id, key);
    }

    /**
     * @see AccessData#getReferredIDs(String)
     */
    public synchronized Set getReferredIDs(String id) {
        return accessData.getReferredIDs(id);
    }

    /**
     * @see AccessData#getSize()
     */
    public synchronized int getSize() {
        return accessData.getSize();
    }

    /**
     * @see AccessData#getStoredIDs()
     */
    public synchronized Set getStoredIDs() {
        return accessData.getStoredIDs();
    }

    /**
     * @see AccessData#initialize()
     */
    public synchronized void initialize() throws IOException {
        accessData.initialize();
    }

    /**
     * @see AccessData#isKnownId(String)
     */
    public synchronized boolean isKnownId(String id) {
        return accessData.isKnownId(id);
    }

    /**
     * @see AccessData#put(String, String, String)
     */
    public synchronized void put(String id, String key, String value) {
        accessData.put(id, key, value);
    }

    /**
     * @see AccessData#putReferredID(String, String)
     */
    public synchronized void putReferredID(String id, String referredID) {
        accessData.putReferredID(id, referredID);
    }

    /**
     * @see AccessData#remove(String,String)
     */
    public synchronized void remove(String id, String key) {
        accessData.remove(id,key);
    }

    /**
     * @see AccessData#remove(String)
     */
    public synchronized void remove(String id) {
        accessData.remove(id);
    }

    /**
     * @see AccessData#removeReferredID(String,String)
     */
    public synchronized void removeReferredID(String id, String referredID) {
         accessData.removeReferredID(id, referredID);
    }

    /**
     * @see AccessData#removeReferredIDs(String)
     */
    public synchronized void removeReferredIDs(String id) {
        accessData.removeReferredIDs(id);
    }

    /**
     * @see AccessData#store() 
     */
    public synchronized void store() throws IOException {
        accessData.store();
    }

    /**
     * @see AccessData#getAggregatedIDs(String)
     */
    public synchronized Set getAggregatedIDs(String id) {
        return accessData.getAggregatedIDs(id);
    }

    /**
     * @see AccessData#getAggregatedIDsClosure(String)
     */
    public synchronized ClosableIterator getAggregatedIDsClosure(String id) {
        return accessData.getAggregatedIDsClosure(id);
    }

    /**
     * @see AccessData#getUntouchedIDsIterator()
     */
    public synchronized ClosableIterator getUntouchedIDsIterator() {
        return accessData.getUntouchedIDsIterator();
    }

    /**
     * @see AccessData#putAggregatedID(String, String)
     */
    public synchronized void putAggregatedID(String id, String aggregatedID) {
        accessData.putAggregatedID(id, aggregatedID);
    }

    /**
     * @see AccessData#removeAggregatedID(String, String)
     */
    public synchronized void removeAggregatedID(String id, String aggregatedID) {
        accessData.removeAggregatedID(id, aggregatedID);
    }

    /**
     * @see AccessData#removeUntouchedIDs()
     */
    public synchronized void removeUntouchedIDs() {
        accessData.removeUntouchedIDs();
    }

    /**
     * @see AccessData#touchRecursively(String)
     */
    public synchronized void touchRecursively(String id) {
        accessData.touchRecursively(id);
    }

    /**
     * @see org.semanticdesktop.aperture.accessor.AccessData#isTouched(java.lang.String)
     */
    public synchronized boolean isTouched(String id) {
        return accessData.isTouched(id);
    }

    /**
     * @see org.semanticdesktop.aperture.accessor.AccessData#touch(java.lang.String)
     */
    public synchronized void touch(String id) {
        accessData.touch(id);
    }

}

