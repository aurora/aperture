/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.base;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A trivial default implementation of the DataSource interface.
 */
public abstract class DataSourceBase implements DataSource {

	// Note: the utility get methods operating on the RDFContainer interpret invalid data as no
	// configuration, i.e. exceptions result in null return values. This is in line with the general
	// spirit of RDF that it should be possible to make any arbitrary statement, only some statements
	// cannot be interpreted automatically.

	private RDFContainer configuration;

	public DataSourceBase() {
		// no-op
	}
	
	public DataSourceBase(RDFContainer configuration) {
		this.configuration = configuration;
	}
	
	public URI getID() {
		return configuration.getDescribedUri();
	}

	public String getName() {
		return configuration.getString(DATASOURCE_GEN.name);
	}

	public void setName(String name) {
		configuration.put(DATASOURCE_GEN.name, name);
	}

	public RDFContainer getConfiguration() {
		return configuration;
	}

	public void setConfiguration(RDFContainer configuration) {
		// set the configuration
		this.configuration = configuration;
	}
	
	public void dispose() {
		configuration.dispose();
	}
}
