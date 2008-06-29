package org.semanticdesktop.aperture.addressbook.apple;
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
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Sun Jun 29 15:53:55 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/addressbook/apple/AppleAddressbookDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/appleaddresbookds#AppleAddressbookDataSource
 */
public class AppleAddressbookDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return APPLEADDRESSBOOKDS.AppleAddressbookDataSource;
    }
}
