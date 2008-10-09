/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.util.HttpClientUtil;

/**
 * A common superclass for all subcrawler implementations
 */
public abstract class AbstractSubCrawler implements SubCrawler{

    /**
     * Returns the prefix used when generating uris. See the documentation for {@link SubCrawler} class for
     * more details.
     * 
     * @return the prefix used when generating uris.
     */
    public abstract String getUriPrefix();

    /**
     * Creates a URI for a subcrawled entity. Uses a scheme invented within the apache commons VFS project.
     * @param objectUri the uri of the parent data object
     * @param childPath the path within the the child object
     * @return a uri for a subcrawled entity.
     * @see <a href="http://commons.apache.org/vfs/filesystems.html">VFS Filesystems Documentation</a>        
     */
    protected URI createChildUri(URI objectUri, String childPath) {
        return new URIImpl(getUriPrefix() + ":" + 
            objectUri.toString() + "!/" + 
            HttpClientUtil.formUrlEncode(childPath,"/-_."));
    }
}
