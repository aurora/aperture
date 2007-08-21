package org.semanticdesktop.aperture.websites.delicious;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * A factory of del.icio.us data source instances
 */
public class DeliciousDataSourceFactory implements DataSourceFactory {

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#getSupportedType()
	 */
	public URI getSupportedType() {
		return DELICIOUSDS.DeliciousDataSource;
	}

	/**
	 * @see org.semanticdesktop.aperture.datasource.DataSourceFactory#newInstance()
	 */
	public DataSource newInstance() {
		return new DeliciousDataSource();
	}

	/**
	 * @see DataSourceFactory#getDescription(Model)
	 */
    public boolean getDescription(Model model) {
        DELICIOUSDS.getDELICIOUSDSOntology(model);
        return true;
    }
}
