package org.semanticdesktop.aperture.addressbook.thunderbird;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Aug 13 18:04:38 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/addressbook/thunderbird/ThunderbirdAddressbookDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/thunderbirdaddresbookds#ThunderbirdAddressbookDataSource
 */
public class ThunderbirdAddressbookDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return THUNDERBIRDADDRESSBOOKDS.ThunderbirdAddressbookDataSource;
    }

    /**
     * Returns the Path to the file where the addresbook is stored.
     * 
     * @return the Path to the file where the addresbook is stored. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getThunderbirdAddressbookPath() {
          return getConfiguration().getString(THUNDERBIRDADDRESSBOOKDS.thunderbirdAddressbookPath);
     }

    /**
     * Sets the Path to the file where the addresbook is stored.
     * 
     * @param thunderbirdAddressbookPath Path to the file where the addresbook is stored., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setThunderbirdAddressbookPath(String thunderbirdAddressbookPath) {
         if ( thunderbirdAddressbookPath == null) {
             getConfiguration().remove(THUNDERBIRDADDRESSBOOKDS.thunderbirdAddressbookPath);
         } else {
             getConfiguration().put(THUNDERBIRDADDRESSBOOKDS.thunderbirdAddressbookPath,thunderbirdAddressbookPath);
         }
     }
}
