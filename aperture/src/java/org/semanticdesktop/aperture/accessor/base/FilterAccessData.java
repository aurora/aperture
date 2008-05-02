/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.semanticdesktop.aperture.accessor.AccessData;

/**
 * Abstract superclass for AccessData implementations that wrap other AccessData instances in order to filter
 * certain information. The abstract class FilterAccessData itself provides default methods that pass requests
 * to the contained AccessData. Subclasses of FilterAccessData should override some of these methods and may
 * also provide additional methods and fields.
 */
public abstract class FilterAccessData implements AccessData {

	private AccessData accessData;
	
	public FilterAccessData(AccessData accessData) {
		this.accessData = accessData;
	}
	
	public AccessData getAccessData() {
		return accessData;
	}
	
	public void clear() throws IOException {
		accessData.clear();
	}

	public String get(String id, String key) {
		return accessData.get(id, key);
	}

	public Set getReferredIDs(String id) {
		return accessData.getReferredIDs(id);
	}

	public int getSize() {
		return accessData.getSize();
	}

	public Set getStoredIDs() {
		return accessData.getStoredIDs();
	}

	public void initialize() throws IOException {
		accessData.initialize();
	}

	public boolean isKnownId(String id) {
		return accessData.isKnownId(id);
	}

	public void put(String id, String key, String value) {
		accessData.put(id, key, value);
	}

	public void putReferredID(String id, String referredID) {
		accessData.putReferredID(id, referredID);
	}

	public void remove(String id, String key) {
		accessData.remove(id, key);
	}

	public void remove(String id) {
		accessData.remove(id);
	}

	public void removeReferredID(String id, String referredID) {
		accessData.removeReferredID(id, referredID);
	}

	public void removeReferredIDs(String id) {
		accessData.removeReferredIDs(id);
	}

	public void store() throws IOException {
		accessData.store();
	}

    /**
     * @param id
     * @return
     * @see org.semanticdesktop.aperture.accessor.AccessData#getAggregatedIDs(java.lang.String)
     */
    public Set getAggregatedIDs(String id) {
        return accessData.getAggregatedIDs(id);
    }

    /**
     * @param id
     * @return
     * @see org.semanticdesktop.aperture.accessor.AccessData#getAggregatedIDsClosure(java.lang.String)
     */
    public ClosableIterator getAggregatedIDsClosure(String id) {
        return accessData.getAggregatedIDsClosure(id);
    }

    /**
     * @return
     * @see org.semanticdesktop.aperture.accessor.AccessData#getUntouchedIDsIterator()
     */
    public ClosableIterator getUntouchedIDsIterator() {
        return accessData.getUntouchedIDsIterator();
    }

    /**
     * @param id
     * @param aggregatedID
     * @see org.semanticdesktop.aperture.accessor.AccessData#putAggregatedID(java.lang.String, java.lang.String)
     */
    public void putAggregatedID(String id, String aggregatedID) {
        accessData.putAggregatedID(id, aggregatedID);
    }

    /**
     * @param id
     * @param aggregatedID
     * @see org.semanticdesktop.aperture.accessor.AccessData#removeAggregatedID(java.lang.String, java.lang.String)
     */
    public void removeAggregatedID(String id, String aggregatedID) {
        accessData.removeAggregatedID(id, aggregatedID);
    }

    /**
     * 
     * @see org.semanticdesktop.aperture.accessor.AccessData#removeUntouchedIDs()
     */
    public void removeUntouchedIDs() {
        accessData.removeUntouchedIDs();
    }

    /**
     * @param id
     * @see org.semanticdesktop.aperture.accessor.AccessData#touchRecursively(java.lang.String)
     */
    public void touchRecursively(String id) {
        accessData.touchRecursively(id);
    }

    /**
     * @param id
     * @return
     * @see org.semanticdesktop.aperture.accessor.AccessData#isTouched(java.lang.String)
     */
    public boolean isTouched(String id) {
        return accessData.isTouched(id);
    }

    /**
     * @param id
     * @see org.semanticdesktop.aperture.accessor.AccessData#touch(java.lang.String)
     */
    public void touch(String id) {
        accessData.touch(id);
    }
}
