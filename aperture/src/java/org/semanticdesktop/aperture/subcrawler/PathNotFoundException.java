/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.io.IOException;

import org.ontoware.rdf2go.model.node.URI;

/**
 * Thrown by SubCrawlers when the requested path did not point to an existing resource. 
 */
public class PathNotFoundException extends IOException {

    private static final long serialVersionUID = 2038085786053528104L;
    
    /**
     * Constructor accepting a path
     * 
     * @param uri the uri of the data object where the subcrawler looked for the resource with the given path
     * @param path the path that has not been found
     */
    public PathNotFoundException(String className, URI uri, String path) {
        super("Path not found. Subcrawler: " +className + " Parent uri: " + uri + " path: " + path);
    }
}
