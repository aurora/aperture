package org.semanticdesktop.aperture.outlook;
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
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Sun Jun 29 15:53:45 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/outlook/outlookDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/outlookds#OutlookDataSource
 */
public class OutlookDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return OUTLOOKDS.OutlookDataSource;
    }

    /**
     * Returns the URL used as a prefix for URIs of all outlook resources. It should begin with outlook://
     * 
     * @return the URL used as a prefix for URIs of all outlook resources. It should begin with outlook:// or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getRootUrl() {
          return getConfiguration().getString(OUTLOOKDS.rootUrl);
     }

    /**
     * Sets the URL used as a prefix for URIs of all outlook resources. It should begin with outlook://
     * 
     * @param rootUrl URL used as a prefix for URIs of all outlook resources. It should begin with outlook://, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setRootUrl(String rootUrl) {
         if ( rootUrl == null) {
             getConfiguration().remove(OUTLOOKDS.rootUrl);
         } else {
             getConfiguration().put(OUTLOOKDS.rootUrl,rootUrl);
         }
     }
}
