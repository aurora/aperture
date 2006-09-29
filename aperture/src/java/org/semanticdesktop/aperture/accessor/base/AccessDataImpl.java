/*
 * Copyright (c) 2005 - 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ArrayMap;

/**
 * Simple AccessData implementation that holds all information in main memory. No persistency facilities are
 * provided, see FileAccessData for that.
 * 
 * @see FileAccessData
 */
public class AccessDataImpl implements AccessData {

	/**
	 * A Map mapping IDs to another Map that contains the key-value pairs for that ID.
	 */
	protected HashMap idMap;

	/**
	 * A mapping from IDs to Sets of IDs that the former ID refers to. This can be used to model parent-child
	 * relationships or links between IDs. The use of this mapping is typically to register IDs that need
	 * special treatment once the referring ID has been changed or removed.
	 */
	protected HashMap referredIDMap;

	public void initialize() throws IOException {
		if (idMap == null) {
			idMap = new HashMap(1024);
		}
		if (referredIDMap == null) {
			referredIDMap = new HashMap(1024);
		}
	}

	public void store() throws IOException {}

	public void clear() throws IOException {
		idMap = null;
		referredIDMap = null;
	}

	public int getSize() {
		return getStoredIDs().size();
	}

	public Set getStoredIDs() {
		HashSet result = new HashSet(idMap.keySet());
		result.addAll(referredIDMap.keySet());
		return result;
	}

	public boolean isKnownId(String id) {
		return idMap.containsKey(id) || referredIDMap.containsKey(id);
	}

	public void put(String id, String key, String value) {
		// assumption: lots of objects with relative few things to store: use an ArrayMap
		ArrayMap infoMap = createInfoMap(id);
		infoMap.put(key, value);
	}

	public void putReferredID(String id, String referredID) {
		HashSet ids = (HashSet) referredIDMap.get(id);

		if (ids == null) {
			ids = new HashSet();
			referredIDMap.put(id, ids);
		}

		ids.add(referredID);
	}

	public String get(String id, String key) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap == null) {
			return null;
		}
		else {
			return (String) infoMap.get(key);
		}
	}

	public Set getReferredIDs(String id) {
		return (Set) referredIDMap.get(id);
	}

	public void remove(String id, String key) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap != null) {
			infoMap.remove(key);
		}
	}

	public void removeReferredID(String id, String referredID) {
		HashSet ids = (HashSet) referredIDMap.get(id);
		if (ids != null) {
			ids.remove(referredID);

			if (ids.isEmpty()) {
				referredIDMap.remove(id);
			}
		}
	}

	public void removeReferredIDs(String id) {
		referredIDMap.remove(id);
	}
	
	public void remove(String id) {
		idMap.remove(id);
		referredIDMap.remove(id);
	}

	private ArrayMap createInfoMap(String id) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap == null) {
			infoMap = new ArrayMap();
			idMap.put(id, infoMap);
		}
		return infoMap;
	}
}
