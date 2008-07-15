/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;

/**
 * DataSourceFactories create instances of DataSources.
 * 
 * <p>
 * DataSources are globally typed using URIs. This makes it possible to talk about specific types of
 * DataSources outside the scope of a Java application and makes sharing of DataSources over technical
 * boundaries such as platforms and programming languages possible. Each DataSourceFactory should be able
 * to tell which DataSource type it supports, and thus which configurable properties the returned
 * DataSource will have, by returning the DataSource type identifier as a URI.
 */
public interface DataSourceFactory {

    /**
     * Returns the URI of the supported DataSource type.
     */
    public URI getSupportedType();

    /**
     * Returns a new instance of the supported DataSource type.
     */
    public DataSource newInstance();
    
    /**
     * Puts a description of this data source into the given model. 
     * This description should be expressed in fresnel vocabulary. It should
     * be a single fresnel:Lens, with a fresnel:classLensDomain set to the
     * URI of the type supported by this factory (returned by the 
     * getSupportedType method). See the fresnel user manual for details. <br>
     * 
     * @return true if the description has been put into the model <br>
     *         false if not e.g. if there is no such description.
     */
    public boolean getDescription(Model model);
}
