/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.Collection;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;

/**
 * A UrlPattern defines a boolean pattern test on a URL.
 */
public abstract class UrlPattern {

	/**
	 * Apply the pattern matching test on a URL.
	 * 
	 * @param url The URL to test the pattern on.
	 * @return 'true' when the URL matches this UrlPattern, 'false' otherwise.
	 */
	public abstract boolean matches(String url);

	/**
	 * Return a Collection of RDF Statements modeling the contents of this UrlPattern.
	 * 
	 * @param subject The resource to use as the subject of the statements, as a UrlPattern has no kind of ID
	 *            of its own.
	 * @return A Collection of Statement instances.
	 */
	public abstract Collection<Statement> getStatements(Model model, Resource subject);
}
