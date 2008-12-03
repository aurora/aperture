/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.flickr;
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
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Wed Dec 03 00:32:23 CET 2008
 * input file: D:\ganymedeworkspace\aperture-trunk/src/java/org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource
 */
public class FlickrDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return FLICKRDS.FlickrDataSource;
    }

    /**
     * Returns the The Flickr account that should be crawled
     * 
     * @return the The Flickr account that should be crawled or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getAccountToCrawl() {
          return getConfiguration().getString(FLICKRDS.accountToCrawl);
     }

    /**
     * Sets the The Flickr account that should be crawled
     * 
     * @param accountToCrawl The Flickr account that should be crawled, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setAccountToCrawl(String accountToCrawl) {
         if ( accountToCrawl == null) {
             getConfiguration().remove(FLICKRDS.accountToCrawl);
         } else {
             getConfiguration().put(FLICKRDS.accountToCrawl,accountToCrawl);
         }
     }

    /**
     * Returns the The Flickr API key
     * 
     * @return the The Flickr API key or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getApikey() {
          return getConfiguration().getString(FLICKRDS.apikey);
     }

    /**
     * Sets the The Flickr API key
     * 
     * @param apikey The Flickr API key, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setApikey(String apikey) {
         if ( apikey == null) {
             getConfiguration().remove(FLICKRDS.apikey);
         } else {
             getConfiguration().put(FLICKRDS.apikey,apikey);
         }
     }

    /**
     * Returns the The shared secret associated with your Flicckr API key
     * 
     * @return the The shared secret associated with your Flicckr API key or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getSharedSecret() {
          return getConfiguration().getString(FLICKRDS.sharedSecret);
     }

    /**
     * Sets the The shared secret associated with your Flicckr API key
     * 
     * @param sharedSecret The shared secret associated with your Flicckr API key, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setSharedSecret(String sharedSecret) {
         if ( sharedSecret == null) {
             getConfiguration().remove(FLICKRDS.sharedSecret);
         } else {
             getConfiguration().put(FLICKRDS.sharedSecret,sharedSecret);
         }
     }

    /**
     * Returns the Path to the folder where local copies of your photos will be stored
     * 
     * @return the Path to the folder where local copies of your photos will be stored or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getTargetFolder() {
          return getConfiguration().getString(FLICKRDS.targetFolder);
     }

    /**
     * Sets the Path to the folder where local copies of your photos will be stored
     * 
     * @param targetFolder Path to the folder where local copies of your photos will be stored, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setTargetFolder(String targetFolder) {
         if ( targetFolder == null) {
             getConfiguration().remove(FLICKRDS.targetFolder);
         } else {
             getConfiguration().put(FLICKRDS.targetFolder,targetFolder);
         }
     }

    /**
     * Enum of possible values of the crawlType property
     */
     public static enum CrawlType {
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#MetadataAndPicturesCrawlType*/
         MetadataAndPicturesCrawlType,
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#MetadataOnlyCrawlType*/
         MetadataOnlyCrawlType;

         public static CrawlType fromUri(URI uri) {
             if (uri == null) {
                 return null;
             }
             else if (uri.equals(FLICKRDS.MetadataAndPicturesCrawlType)) {
                 return MetadataAndPicturesCrawlType;
             }
             else if (uri.equals(FLICKRDS.MetadataOnlyCrawlType)) {
                 return MetadataOnlyCrawlType;
             }
             else {
                 return null;
             }
         }
         public URI toUri() {
             if (this.equals(MetadataAndPicturesCrawlType)) {
                 return FLICKRDS.MetadataAndPicturesCrawlType;
             }
             else if (this.equals(MetadataOnlyCrawlType)) {
                 return FLICKRDS.MetadataOnlyCrawlType;
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
          return CrawlType.fromUri(getConfiguration().getURI(FLICKRDS.crawlType));
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
             getConfiguration().remove(FLICKRDS.crawlType);
         } else {
             getConfiguration().put(FLICKRDS.crawlType,crawlType.toUri());
         }
     }
}
