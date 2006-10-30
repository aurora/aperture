/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

class IcalTestIncrementalCrawlerHandler implements CrawlerHandler, RDFContainerFactory {
    
    private Repository repository;
    
    private File file;

    private int numberOfObjects;
    
    private Set<String> newObjects;
    private Set<String> changedObjects;
    private Set<String> unchangedObjects;
    private Set<String> deletedObjects;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public IcalTestIncrementalCrawlerHandler(File file) {
        repository = new Repository(new MemoryStore());
        newObjects = new HashSet<String>();
        changedObjects = new HashSet<String>();
        unchangedObjects = new HashSet<String>();
        deletedObjects = new HashSet<String>();
        this.file = file;
        
        try {
            repository.initialize();
        } catch (SailInitializationException e) {
            // we cannot effectively continue
            throw new RuntimeException(e);
        }

        // set auto-commit off so that all additions and deletions between
        // two commits become a single transaction
        try {
            repository.setAutoCommit(false);
        } catch (SailUpdateException e) {
            // this will hurt performance but we can still continue.
            // Each add and remove will now be a separate transaction
            // (slow).
        }
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
        // commit all generated statements
        try {
            repository.commit();
        } catch (SailUpdateException e) {
            // don't continue when this happens
            throw new RuntimeException(e);
        }
        
        // free any resources contained by this DataObject
        object.dispose();
    }

    public void objectNew(Crawler crawler, DataObject object) {
        numberOfObjects++;
        newObjects.add(object.getID().toString());
        // commit all generated statements
        try {
            repository.commit();
        } catch (SailUpdateException e) {
            // don't continue when this happens
            throw new RuntimeException(e);
        }
        
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
        SesameRDFContainer container = 
            new SesameRDFContainer(repository,uri);
        container.setContext(uri);
        return container;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public Repository getRepository() {
        return repository;
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

