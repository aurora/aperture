/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.base;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

/**
 * A trivial default implementation of the CrawlerHandler interface. The method implementations are simplest
 * possible, that fulfill the contract. The applications are expected to override the methods they need.
 */
public class CrawlerHandlerBase implements CrawlerHandler {

    /**
     * Returns an rdf container factory. This method implementation returns a factory which delivers simple
     * RDFContainers backed by in-memory models obtained from the {@link RDF2Go#getModelFactory()} method.
     * Each model is separate.
     * 
     * @see CrawlerHandler#getRDFContainerFactory(Crawler, String)
     */
    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return new RDFContainerFactory() {

            public RDFContainer getRDFContainer(URI uri) {
                Model model = RDF2Go.getModelFactory().createModel();
                model.open();
                return new RDFContainerImpl(model, uri);
            }
        };
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#accessingObject(Crawler, String)
     */
    public void accessingObject(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearFinished(Crawler, ExitCode)
     */
    public void clearFinished(Crawler crawler, ExitCode exitCode) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearingObject(Crawler, String)
     */
    public void clearingObject(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#clearStarted(Crawler)
     */
    public void clearStarted(Crawler crawler) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#crawlStarted(Crawler)
     */
    public void crawlStarted(Crawler crawler) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#crawlStopped(Crawler, ExitCode)
     */
    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
    // don't do anything, please override me
    }

    /**
     * This method implementation only disposes the data object and does nothing more. It is meant to be
     * overridden.
     * 
     * @see CrawlerHandler#objectChanged(Crawler, DataObject)
     */
    public void objectChanged(Crawler crawler, DataObject object) {
    // don't do anything, please override me
    }

    /**
     * This method implementation only disposes the data object and does nothing more. It is meant to be
     * overridden.
     * 
     * @see CrawlerHandler#objectNew(Crawler, DataObject)
     */
    public void objectNew(Crawler crawler, DataObject object) {
        object.dispose();
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#objectNotModified(Crawler, String)
     */
    public void objectNotModified(Crawler crawler, String url) {
    // don't do anything, please override me
    }

    /**
     * This method implementation doesn't do anything, it is meant to be overridden.
     * 
     * @see CrawlerHandler#objectRemoved(Crawler, String)
     */
    public void objectRemoved(Crawler crawler, String url) {
    // don't do anything, please override me
    }
}
