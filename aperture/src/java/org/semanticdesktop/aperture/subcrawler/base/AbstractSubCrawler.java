/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.PathNotFoundException;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A common superclass for all subcrawler implementations
 */
public abstract class AbstractSubCrawler implements SubCrawler {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
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

    public DataObject getDataObject(URI parentUri, String path, InputStream stream, DataSource dataSource, Charset charset,
            String mimeType, RDFContainerFactory factory) throws SubCrawlerException, PathNotFoundException {
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer parentMetadata = new RDFContainerImpl(model,parentUri);
        URI childUri = createChildUri(parentUri, path.startsWith("/") ? path.substring(1) : path);
        GetDataObjectSubCrawlerHandler handler = new GetDataObjectSubCrawlerHandler(factory,childUri);
        subCrawl(parentUri, stream, handler, dataSource, null, charset, mimeType, parentMetadata);
        parentMetadata.dispose();
        DataObject result = handler.getObjectToReturn();
        if (result != null) {
            return result;
        } else {
            throw new PathNotFoundException(this.getClass().getName(), parentUri, path);
        }
    } 
    
    private class GetDataObjectSubCrawlerHandler implements SubCrawlerHandler {
        
        private RDFContainerFactory fac;
        private URI requiredUri;
        private DataObject objectToReturn;
        
        public GetDataObjectSubCrawlerHandler(RDFContainerFactory fac, URI requiredUri) {
            this.fac = fac;
            this.requiredUri = requiredUri;
            this.objectToReturn = null;
        }
        
        public DataObject getObjectToReturn() {
            return objectToReturn;
        }
        
        public RDFContainerFactory getRDFContainerFactory(String url) {
            return fac;
        }

        public void objectNew(DataObject object) {
            if (object.getID().equals(requiredUri)) {
                this.objectToReturn = object;
                stopSubCrawler();
            } else {
                object.dispose();
            }            
        }

        public void objectChanged(DataObject object) {
            logger.warn("Got an \"objectChanged\" call inside a getDataObject method, uri:" + object.getID());
            object.dispose();
        }
        
        public void objectNotModified(String url) { 
            logger.warn("Got an \"objectNotModified\" call inside a getDataObject method, uri:" + url);
        }
    }
}
