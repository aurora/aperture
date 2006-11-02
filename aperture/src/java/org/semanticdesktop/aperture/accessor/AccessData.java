/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import java.io.IOException;
import java.util.Set;

/**
 * An AccessData instance stores information about accessed resources such as last modification dates,
 * locations, etc. This primarily facilitates incremental crawling of DataSources.
 * 
 * AccessData proposes a number of keys to use when storing values, combined with a proposed value encoding.
 * This is to ensure that several DataAccessors and possibly other components can share the same AccessData
 * instance without resulting in conflicts.
 */
public interface AccessData {

	/**
	 * Recommended key to store a resource's date. Recommended value encoding: time in milliseconds as a
	 * string.
	 */
	public static final String DATE_KEY = "date";

	/**
	 * Recommended key to store a resource's byte size. Recommended value encoding: String-encoded long.
	 */
	public static final String BYTE_SIZE_KEY = "byteSize";

	/**
	 * Recommended key to store the redirected URL of an original URL. Recommended value encoding: a legal
	 * URI.
	 */
	public static final String REDIRECTS_TO_KEY = "redirectsTo";

	/**
	 * Prepares the AccessData for operation. This may for example mean reading files or opening repositories
	 * that hold the stored data.
	 */
	public void initialize() throws IOException;

	/**
	 * Informs the AccessData that processing has completed and, in case of a persistent storage, now is a
	 * good time to write or flush results. Afterwards the AccessData may be in an unusable state until
	 * 'initialize' is invoked again.
	 */
	public void store() throws IOException;

	/**
	 * Clears this AccessData. This may be invoked on initialized and unititialized AccessData's. Both
	 * in-memory information as any persistent storage will be cleared. Afterwards the AccessData may be in an
	 * unusable state until 'initialize' is invoked again.
	 */
	public void clear() throws IOException;

	/**
	 * Gets the number of resources for which information has been stored in this AccessData.
	 * 
	 * @return The number of registered resources.
	 */
	public int getSize();

	/**
	 * Gets the IDs of all resources for which information has been stored in this AccessData.
	 * 
	 * @return A Set of Strings.
	 */
	public Set getStoredIDs();

	/**
	 * Returns whether this AccessData holds any information about the specified ID.
	 * 
	 * @return "true" when this AccessData has information about the specified ID, "false" otherwise.
	 */
	public boolean isKnownId(String id);

	/**
	 * Stores information (a key-value pair) for the specified id.
	 * 
	 * @param id The resource's ID.
	 * @param key The info key.
	 * @param value The info value.
	 */
	public void put(String id, String key, String value);

	/**
	 * Stores a reference relation between two resources, modeling e.d. a parent-child relationship or a link.
	 * 
	 * @param id The referring resource's ID.
	 * @param referredID The referred resource's ID.
	 */
	public void putReferredID(String id, String referredID);

	/**
	 * Gets specific information about the specified id.
	 * 
	 * @param id The resource's ID.
	 * @param key The info key.
	 * @return The stored info value, or null if no info has been stored for the specified id and key.
	 */
	public String get(String id, String key);

	/**
	 * Returns all referred resources of the specified resource.
	 * 
	 * @return A Set of Strings, or null when there are no referred resources registered for this resource.
	 */
	public Set getReferredIDs(String id);

	/**
	 * Removes the value for the specified id and key.
	 * 
	 * @param id A resource ID.
	 * @param key A key under which info is stored.
	 */
	public void remove(String id, String key);

	/**
	 * Removes a reference relationship between two resources.
	 * 
	 * @param id The referring resource's ID.
	 * @param referredID The referred resource's ID.
	 */
	public void removeReferredID(String id, String referredID);

	/**
	 * Removes all referred IDs of a resource.
	 * 
	 * @param id The referring resource's ID.
	 */
	public void removeReferredIDs(String id);
	
	/**
	 * Removes all information about the resource with the specified ID.
	 * 
	 * @param id A resource ID.
	 */
	public void remove(String id);
}
