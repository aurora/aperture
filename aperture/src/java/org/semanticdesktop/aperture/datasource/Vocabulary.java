/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * This interface defines a comprehensive list of URIs to be used for modeling DataSource configurations.
 */
public interface Vocabulary {

    public static final String NS = "http://aperture.semanticdesktop.org/ontology/source/";

    public static final URI ROOT_URL = new URIImpl(NS + "rootUrl");

    public static final URI MAXIMUM_DEPTH = new URIImpl(NS + "maximumDepth");

    public static final URI INCLUDE_PATTERN = new URIImpl(NS + "includePattern");

    public static final URI EXCLUDE_PATTERN = new URIImpl(NS + "excludePattern");

    public static final URI INCLUDE_HIDDEN_RESOURCES = new URIImpl(NS + "includeHiddenResources");
}
