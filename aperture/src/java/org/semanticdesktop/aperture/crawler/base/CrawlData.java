package org.semanticdesktop.aperture.crawler.base;

import java.util.Set;

/**
 * A CrawlData instance stores information about crawled resources such as last modification dates,
 * locations, etc. This facilitates incremental crawling of DataSources.
 */
public interface CrawlData {

    /**
     * Gets the number of resources for which information has been stored in this CrawlData.
     * 
     * @return The number of registered resources.
     */
    public int getSize();

    /**
     * Gets the IDs of all resources for which information has been stored in this CrawlData.
     * 
     * @return A Set of Strings.
     */
    public Set getStoredIDs();

    /**
     * Returns whether this CrawlData holds any information about the specified ID.
     * 
     * @return "true" when this CrawlData has information about the specified ID, "false" otherwise.
     */
    public boolean isKnownId(String id);

    /**
     * Clears this CrawlData.
     */
    public void clear();

    /**
     * Stores infonformation (a key-value pair) for the specified id.
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
     * Removes the for the specified id and key.
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
