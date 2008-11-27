/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.base;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

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
		return configuration.getString(DATASOURCE.dataSourceName);
	}

	public void setName(String name) {
		configuration.put(DATASOURCE.dataSourceName, name);
	}
	
	public String getComment() {
	    return configuration.getString(DATASOURCE.dataSourceComment);
	}
	
	public void setComment(String comment) {
	    configuration.put(DATASOURCE.dataSourceComment,comment);
	}
	
	public Integer getTimeout() {
	    return configuration.getInteger(DATASOURCE.timeout);
	}
	
	public void setTimeout(int timeout) {
	    configuration.put(DATASOURCE.timeout,timeout);
	}

	public RDFContainer getConfiguration() {
		return configuration;
	}

	public void setConfiguration(RDFContainer configuration) {
		// set the configuration
		this.configuration = configuration;
		configuration.put(RDF.type, getType());
	}
	
	public void dispose() {
		configuration.dispose();
	}
}
