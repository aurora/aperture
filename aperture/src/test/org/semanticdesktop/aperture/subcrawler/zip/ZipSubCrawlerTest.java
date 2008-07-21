/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;

/**
 * A test case for the zip subcrawler
 */
public class ZipSubCrawlerTest extends ApertureTestBase {

    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testZipTest() throws Exception {
        ZipSubCrawler subCrawler = new ZipSubCrawler();
        metadata = subCrawl(DOCS_PATH + "zip-test.zip", subCrawler);
        metadata.getModel().writeTo(System.out,Syntax.RdfXml);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    private RDFContainer subCrawl(String string, ZipSubCrawler subCrawler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(string, this.getClass());
        ZipSubCrawlerHandler handler = new ZipSubCrawlerHandler();
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl("uri:dummyuri"));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
    }
}


class ZipSubCrawlerHandler implements SubCrawlerHandler, RDFContainerFactory {
    
    private Model model;

    private int numberOfObjects;
    
    private Set<String> newObjects;
    private Set<String> changedObjects;
    private Set<String> unchangedObjects;
    private Set<String> deletedObjects;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs the ZipSubCrawlerHandler
     */
    public ZipSubCrawlerHandler() throws ModelException {
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