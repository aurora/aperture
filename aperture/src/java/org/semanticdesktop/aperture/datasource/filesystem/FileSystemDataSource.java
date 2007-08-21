package org.semanticdesktop.aperture.datasource.filesystem;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Aug 13 18:04:35 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/datasource/filesystem/filesystemDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource
 */
public class FileSystemDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return FILESYSTEMDS.FileSystemDataSource;
    }

    /**
     * Returns the Path to the root of the folder tree to be crawled
     * 
     * @return the Path to the root of the folder tree to be crawled or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getRootFolder() {
          return getConfiguration().getString(FILESYSTEMDS.rootFolder);
     }

    /**
     * Sets the Path to the root of the folder tree to be crawled
     * 
     * @param rootFolder Path to the root of the folder tree to be crawled, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setRootFolder(String rootFolder) {
         if ( rootFolder == null) {
             getConfiguration().remove(FILESYSTEMDS.rootFolder);
         } else {
             getConfiguration().put(FILESYSTEMDS.rootFolder,rootFolder);
         }
     }

    /**
     * Returns the How many levels below the root folder should the crawled descend.
     * 
     * @return the How many levels below the root folder should the crawled descend. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Integer getMaximumDepth() {
          return getConfiguration().getInteger(FILESYSTEMDS.maximumDepth);
     }

    /**
     * Sets the How many levels below the root folder should the crawled descend.
     * 
     * @param maximumDepth How many levels below the root folder should the crawled descend., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumDepth(Integer maximumDepth) {
         if ( maximumDepth == null) {
             getConfiguration().remove(FILESYSTEMDS.maximumDepth);
         } else {
             getConfiguration().put(FILESYSTEMDS.maximumDepth,maximumDepth);
         }
     }

    /**
     * Returns the Maximum size (in bytes) of files reported by the crawler
     * 
     * @return the Maximum size (in bytes) of files reported by the crawler or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Long getMaximumSize() {
          return getConfiguration().getLong(FILESYSTEMDS.maximumSize);
     }

    /**
     * Sets the Maximum size (in bytes) of files reported by the crawler
     * 
     * @param maximumSize Maximum size (in bytes) of files reported by the crawler, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumSize(Long maximumSize) {
         if ( maximumSize == null) {
             getConfiguration().remove(FILESYSTEMDS.maximumSize);
         } else {
             getConfiguration().put(FILESYSTEMDS.maximumSize,maximumSize);
         }
     }

    /**
     * Returns the Should the hidden files and folders be included in crawl results?
     * 
     * @return the Should the hidden files and folders be included in crawl results? or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getIncludeHiddenResources() {
          return getConfiguration().getBoolean(FILESYSTEMDS.includeHiddenResources);
     }

    /**
     * Sets the Should the hidden files and folders be included in crawl results?
     * 
     * @param includeHiddenResources Should the hidden files and folders be included in crawl results?, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setIncludeHiddenResources(Boolean includeHiddenResources) {
         if ( includeHiddenResources == null) {
             getConfiguration().remove(FILESYSTEMDS.includeHiddenResources);
         } else {
             getConfiguration().put(FILESYSTEMDS.includeHiddenResources,includeHiddenResources);
         }
     }

    /**
     * Returns the Should the crawler follow symbolic links?
     * 
     * @return the Should the crawler follow symbolic links? or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getFollowSymbolicLinks() {
          return getConfiguration().getBoolean(FILESYSTEMDS.followSymbolicLinks);
     }

    /**
     * Sets the Should the crawler follow symbolic links?
     * 
     * @param followSymbolicLinks Should the crawler follow symbolic links?, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setFollowSymbolicLinks(Boolean followSymbolicLinks) {
         if ( followSymbolicLinks == null) {
             getConfiguration().remove(FILESYSTEMDS.followSymbolicLinks);
         } else {
             getConfiguration().put(FILESYSTEMDS.followSymbolicLinks,followSymbolicLinks);
         }
     }

    /**
     * Returns the domain boundaries for this data source
     * 
     * @return the domain boundaries for this data source
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public DomainBoundaries getDomainBoundaries() {
          return ConfigurationUtil.getDomainBoundaries(getConfiguration());
     }

    /**
     * Sets the domain boundaries for this data source
     * 
     * @param domainBoundaries the domain boundaries, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setDomainBoundaries(DomainBoundaries domainBoundaries) {
          if (domainBoundaries == null) {
              DomainBoundaries emptyBoundaries = new DomainBoundaries();
              ConfigurationUtil.setDomainBoundaries(emptyBoundaries,getConfiguration());
          } else {
              ConfigurationUtil.setDomainBoundaries(domainBoundaries,getConfiguration());
          }
     }
}
