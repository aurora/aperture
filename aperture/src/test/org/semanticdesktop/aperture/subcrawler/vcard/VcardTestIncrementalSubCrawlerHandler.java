/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

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
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;

class VcardTestIncrementalSubCrawlerHandler implements SubCrawlerHandler, RDFContainerFactory {
    
    private Model model;

    private int numberOfObjects;
    
    private Set<String> newObjects;
    private Set<String> changedObjects;
    private Set<String> unchangedObjects;
    private Set<String> deletedObjects;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public VcardTestIncrementalSubCrawlerHandler() throws ModelException {
        model = RDF2Go.getModelFactory().createModel();
        model.open();
        newObjects = new HashSet<String>();
        changedObjects = new HashSet<String>();
        unchangedObjects = new HashSet<String>();
        deletedObjects = new HashSet<String>();
        numberOfObjects = 0;
        newObjects.clear();
        changedObjects.clear();
        unchangedObjects.clear();
        deletedObjects.clear();
    }
    
    public void close() {
    	model.close();
    }
   
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// CRAWLER HANDLER METHODS //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void objectChanged(DataObject object) {
        changedObjects.add(object.getID().toString());
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNew(DataObject object) {
        numberOfObjects++;
        newObjects.add(object.getID().toString());
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNotModified(String url) {
    	numberOfObjects++;
        unchangedObjects.add(url);
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

    public RDFContainerFactory getRDFContainerFactory(String url) {
        return this;
    }
}

