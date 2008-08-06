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
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Thu Jul 31 09:42:47 CEST 2008
 * input file: /Users/ck/Entwicklung/nepomuk/workspace2/aperture/src/java/org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl
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
     * Returns the The Password used to access this datasource.
     * 
     * @return the The Password used to access this datasource. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getPassword() {
          return getConfiguration().getString(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password);
     }

    /**
     * Sets the The Password used to access this datasource.
     * 
     * @param password The Password used to access this datasource., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setPassword(String password) {
         if ( password == null) {
             getConfiguration().remove(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password);
         } else {
             getConfiguration().put(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password,password);
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
