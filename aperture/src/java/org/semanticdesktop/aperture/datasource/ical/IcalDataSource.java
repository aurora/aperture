package org.semanticdesktop.aperture.datasource.ical;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Aug 13 18:04:36 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/datasource/ical/icalDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#IcalDataSource
 */
public class IcalDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return ICALDS.IcalDataSource;
    }

    /**
     * Returns the URL of the ical file to be crawled.
     * 
     * @return the URL of the ical file to be crawled. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getRootUrl() {
          return getConfiguration().getString(ICALDS.rootUrl);
     }

    /**
     * Sets the URL of the ical file to be crawled.
     * 
     * @param rootUrl URL of the ical file to be crawled., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setRootUrl(String rootUrl) {
         if ( rootUrl == null) {
             getConfiguration().remove(ICALDS.rootUrl);
         } else {
             getConfiguration().put(ICALDS.rootUrl,rootUrl);
         }
     }
}
