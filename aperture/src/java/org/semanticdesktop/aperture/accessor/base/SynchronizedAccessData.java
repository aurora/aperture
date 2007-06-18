/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Set;

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

}

