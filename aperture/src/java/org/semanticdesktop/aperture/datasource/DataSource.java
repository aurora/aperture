/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A DataSource defines the characteristics of a source from which DataObjects can be extracted. A Datasource
 * contains all information necessary to realize these objects, such as paths, usernames, passwords, etc.
 */
public interface DataSource {

	/**
	 * Gets the id of this data source.
	 * 
	 * @return A URI identifier for the data source.
	 */
	public URI getID();

	/**
	 * Gets the name of this data source.
	 * 
	 * @return A descriptive name for the data source.
	 */
	public String getName();

	/**
	 * Sets the name of this data source.
	 * 
	 * @param name A descriptive name for the data source.
	 */
	public void setName(String name);

	/**
	 * Returns a URI that indicates the DataSource type in a platform- and programming language-independent
	 * way.
	 * 
	 * @return A URI indicating the DataSource type
	 */
	public URI getType();

	/**
	 * Gets the RDF model containing the configuration of this DataSource. The returned RDFContainer can be
	 * modified in order to update the configuration.
	 * 
	 * @return A mutable RDFContainer, or 'null' when the configuration container has not been set yet.
	 */
	public RDFContainer getConfiguration();

	/**
	 * Sets the RDF model containing the configuration of this DataSource. The specified RDFContainer is
	 * expected to be mutable.
	 * 
	 * @param configuration A mutable RDFContainer, or 'null' when the DataSource's configuration needs to be
	 *            temporarily unset.
	 */
	public void setConfiguration(RDFContainer configuration);
	
	/**
	 * Disposes this DataSource. This method disposes the underlying configuration RDFContainer. May also perform
	 * any other cleanup operations necessary for this particular implementation.
	 */
	public void dispose();
}
