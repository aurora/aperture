package org.semanticdesktop.aperture.websites.iphoto;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Aug 13 18:04:34 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/websites/iphoto/iphotoDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#IPhotoKeywordDataSource
 */
public class IPhotoKeywordDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return IPHOTODS.IPhotoKeywordDataSource;
    }
}
