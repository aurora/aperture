/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

class MboxTestIncrementalCrawlerHandler implements CrawlerHandler, RDFContainerFactory {
    
    private Model model;
    
    private File file;

    private int numberOfObjects;
    
    private Set<String> newObjects;
    private Set<String> changedObjects;
    private Set<String> unchangedObjects;
    private Set<String> deletedObjects;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public MboxTestIncrementalCrawlerHandler(File file) throws ModelException {
        model = RDF2Go.getModelFactory().createModel();
        model.open();
        newObjects = new HashSet<String>();
        changedObjects = new HashSet<String>();
        unchangedObjects = new HashSet<String>();
        deletedObjects = new HashSet<String>();
        this.file = file;
    }
    
    public void close() {
    	model.close();
    }
   
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// CRAWLER HANDLER METHODS //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void crawlStarted(Crawler crawler) {
        numberOfObjects = 0;
        newObjects.clear();
        changedObjects.clear();
        unchangedObjects.clear();
        deletedObjects.clear();
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
        changedObjects.add(object.getID().toString());
        
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNew(Crawler crawler, DataObject object) {
        numberOfObjects++;
        newObjects.add(object.getID().toString());
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNotModified(Crawler crawler, String url) {
    	numberOfObjects++;
        unchangedObjects.add(url);
    }

    public void objectRemoved(Crawler crawler, String url) {
        deletedObjects.add(url);
    }
    
    public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
        return this;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// RDF CONTAINER FACTORY METHOD //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public RDFContainer getRDFContainer(URI uri) {
        return new RDFContainerImpl(model, uri, true);
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
    
	public Set<String> getChangedObjects() {
		return changedObjects;
	}
	
	public Set<String> getDeletedObjects() {
		return deletedObjects;
	}
	
	public Set<String> getNewObjects() {
		return newObjects;
	}

	public Set<String> getUnchangedObjects() {
		return unchangedObjects;
	}
	
	public File getFile() {
		return file;
	}
}

