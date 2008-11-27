/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.filesystem;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.DomainBoundableDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Tue Jul 15 22:55:09 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/filesystem/filesystemDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource
 */
public class FileSystemDataSource extends DomainBoundableDataSource {

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
     * Returns the 
     * 
     * @return the  or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getSuppressParentChildLinks() {
          return getConfiguration().getBoolean(FILESYSTEMDS.suppressParentChildLinks);
     }

    /**
     * Sets the 
     * 
     * @param suppressParentChildLinks , can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setSuppressParentChildLinks(Boolean suppressParentChildLinks) {
         if ( suppressParentChildLinks == null) {
             getConfiguration().remove(FILESYSTEMDS.suppressParentChildLinks);
         } else {
             getConfiguration().put(FILESYSTEMDS.suppressParentChildLinks,suppressParentChildLinks);
         }
     }
}
