/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;

class IcalTestSimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {
    
    private Model model;

    private int numberOfObjects;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public IcalTestSimpleCrawlerHandler() throws ModelException {
        model = RDF2Go.getModelFactory().createModel();
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public Model getModel() {
        return model;
    }
    
    public int getNumberOfObjects() {
        return numberOfObjects;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// CRAWLER HANDLER METHODS //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void crawlStarted(Crawler crawler) {
        numberOfObjects = 0;
    }
    
    public void crawlStopped(Crawler crawler, ExitCode exitCode) {
        // we don't need to do anything   
    }

    public void accessingObject(Crawler crawler, String url) {
        // we don't need to do anything
    }

    public void clearFinished(Crawler crawler, ExitCode exitCode) {
        // we don't need to do anything
    }

    public void clearStarted(Crawler crawler) {
        // we don't need to do anything
    }

    public void clearingObject(Crawler crawler, String url) {
        // we don't need to do anything        
    }

    public void objectChanged(Crawler crawler, DataObject object) {
        // we don't need to do anything
    }

    public void objectNew(Crawler crawler, DataObject object) {
        numberOfObjects++;
        
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNotModified(Crawler crawler, String url) {
        // we don't need to do anything
    }

    public void objectRemoved(Crawler crawler, String url) {
        // we don't need to do anything
    }
    
    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return this;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// RDF CONTAINER FACTORY METHOD //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public RDFContainer getRDFContainer(URI uri) {
        RDF2GoRDFContainer container = 
            new RDF2GoRDFContainer(model,uri,true);
        return container;
    }
}

