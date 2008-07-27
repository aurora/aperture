/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.websites.bibsonomy;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Node;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.util.ModelUtil;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Jul 21 14:59:21 CEST 2008
 * input file: /Users/pikson/DFKI/psew_workspace/aperture/src/java/org/semanticdesktop/aperture/websites/bibsonomy/bibsonomyDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#BibsonomyDataSource
 */
public class BibsonomyDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return BIBSONOMYDS.BibsonomyDataSource;
    }

    /**
     * Returns the Username used for authentication in a data source
     * 
     * @return the Username used for authentication in a data source or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getUsername() {
          return getConfiguration().getString(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username);
     }

    /**
     * Sets the Username used for authentication in a data source
     * 
     * @param username Username used for authentication in a data source, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setUsername(String username) {
         if ( username == null) {
             getConfiguration().remove(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username);
         } else {
             getConfiguration().put(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username,username);
         }
     }

    /**
     * Enum of possible values of the crawlType property
     */
     public static enum CrawlType {
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#TagsAndItemsCrawlType*/
         TagsAndItemsCrawlType,
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/11/bibsonomyds#TagsOnlyCrawlType*/
         TagsOnlyCrawlType;

         public static CrawlType fromUri(URI uri) {
             if (uri == null) {
                 return null;
             }
             else if (uri.equals(BIBSONOMYDS.TagsAndItemsCrawlType)) {
                 return TagsAndItemsCrawlType;
             }
             else if (uri.equals(BIBSONOMYDS.TagsOnlyCrawlType)) {
                 return TagsOnlyCrawlType;
             }
             else {
                 return null;
             }
         }
         public URI toUri() {
             if (this.equals(TagsAndItemsCrawlType)) {
                 return BIBSONOMYDS.TagsAndItemsCrawlType;
             }
             else if (this.equals(TagsOnlyCrawlType)) {
                 return BIBSONOMYDS.TagsOnlyCrawlType;
             }
             else {
                 return null;
             }
         }
     }

    /**
     * Returns the Type of crawl
     * 
     * @return the Type of crawl or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public CrawlType getCrawlType() {
          return CrawlType.fromUri(getConfiguration().getURI(BIBSONOMYDS.crawlType));
     }


    /**
     * Sets the Type of crawl
     * 
     * @param crawlType Type of crawl, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setCrawlType(CrawlType crawlType) {
         if ( crawlType == null) {
             getConfiguration().remove(BIBSONOMYDS.crawlType);
         } else {
             getConfiguration().put(BIBSONOMYDS.crawlType,crawlType.toUri());
         }
     }
}
