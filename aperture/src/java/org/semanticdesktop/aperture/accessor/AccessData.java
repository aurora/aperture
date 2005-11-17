package org.semanticdesktop.aperture.accessor;

import java.util.Set;

/**
 * An AccessData instance stores information about accessed resources such as last modification dates,
 * locations, etc. This primarily facilitates incremental crawling of DataSources.
 * 
 * AccessData proposes a number of keys to use when storing values, combined with a proposed value
 * encoding. This is to ensure that several DataAccessors and possibly other components can share the
 * same AccessData instance without resulting in conflicts.
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
     * Clears this AccessData.
     */
    public void clear();

    /**
     * Stores information (a key-value pair) for the specified id.
     * 
     * @param id The resource's ID.
     * @param key The info key.
     * @param value The info value.
     */
    public void put(String id, String key, String value);

    /**
     * Stores a parent child relationship between two resources.
     * 
     * @param id The parent resource's ID.
     * @param child The child resource's ID.
     */
    public void putChild(String id, String child);

    /**
     * Gets specific information about the specified id.
     * 
     * @param id The resource's ID.
     * @param key The info key.
     * @return The stored info value, or null if no info has been stored for the specified id and key.
     */
    public String get(String id, String key);

    /**
     * Returns all child resources of the specified resource.
     * 
     * @return A Set of Strings, or null when there are no child resources registered for this resource.
     */
    public Set getChildren(String id);

    /**
     * Removes the value for the specified id and key.
     * 
     * @param id A resource ID.
     * @param key A key under which info is stored.
     */
    public void remove(String id, String key);

    /**
     * Removes a parent-child relationship between two resources.
     * 
     * @param id The parent resource's ID.
     * @param child The child resource's ID.
     */
    public void removeChild(String id, String child);

    /**
     * Removes all information about the resource with the specified ID.
     * 
     * @param id A resource ID.
     */
    public void remove(String id);
}
