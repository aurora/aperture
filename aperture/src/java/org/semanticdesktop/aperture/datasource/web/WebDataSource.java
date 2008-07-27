/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.web;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Node;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundableDataSource;
import org.semanticdesktop.aperture.util.ModelUtil;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Tue Jul 15 22:55:40 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/web/webDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/webds#WebDataSource
 */
public class WebDataSource extends DomainBoundableDataSource {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return WEBDS.WebDataSource;
    }

    /**
     * Returns the URL of the webpage were the crawling should begin
     * 
     * @return the URL of the webpage were the crawling should begin or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getRootUrl() {
          return getConfiguration().getString(WEBDS.rootUrl);
     }

    /**
     * Sets the URL of the webpage were the crawling should begin
     * 
     * @param rootUrl URL of the webpage were the crawling should begin, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setRootUrl(String rootUrl) {
         if ( rootUrl == null) {
             getConfiguration().remove(WEBDS.rootUrl);
         } else {
             getConfiguration().put(WEBDS.rootUrl,rootUrl);
         }
     }

    /**
     * Returns the Should the embedded resources (images, sounds, flash animations etc.) be included in the crawl results
     * 
     * @return the Should the embedded resources (images, sounds, flash animations etc.) be included in the crawl results or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getIncludeEmbeddedResources() {
          return getConfiguration().getBoolean(WEBDS.includeEmbeddedResources);
     }

    /**
     * Sets the Should the embedded resources (images, sounds, flash animations etc.) be included in the crawl results
     * 
     * @param includeEmbeddedResources Should the embedded resources (images, sounds, flash animations etc.) be included in the crawl results, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setIncludeEmbeddedResources(Boolean includeEmbeddedResources) {
         if ( includeEmbeddedResources == null) {
             getConfiguration().remove(WEBDS.includeEmbeddedResources);
         } else {
             getConfiguration().put(WEBDS.includeEmbeddedResources,includeEmbeddedResources);
         }
     }

    /**
     * Returns the How many levels of link references should the crawler cover.
     * 
     * @return the How many levels of link references should the crawler cover. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Integer getMaximumDepth() {
          return getConfiguration().getInteger(WEBDS.maximumDepth);
     }

    /**
     * Sets the How many levels of link references should the crawler cover.
     * 
     * @param maximumDepth How many levels of link references should the crawler cover., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumDepth(Integer maximumDepth) {
         if ( maximumDepth == null) {
             getConfiguration().remove(WEBDS.maximumDepth);
         } else {
             getConfiguration().put(WEBDS.maximumDepth,maximumDepth);
         }
     }

    /**
     * Returns the Maximum size (in bytes) of resources reported by the crawler
     * 
     * @return the Maximum size (in bytes) of resources reported by the crawler or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Long getMaximumSize() {
          return getConfiguration().getLong(WEBDS.maximumSize);
     }

    /**
     * Sets the Maximum size (in bytes) of resources reported by the crawler
     * 
     * @param maximumSize Maximum size (in bytes) of resources reported by the crawler, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumSize(Long maximumSize) {
         if ( maximumSize == null) {
             getConfiguration().remove(WEBDS.maximumSize);
         } else {
             getConfiguration().put(WEBDS.maximumSize,maximumSize);
         }
     }
}
