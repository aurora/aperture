/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

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
        Model model = metadata.getModel();
        URI archiveUri = model.createURI("uri:dummyuri");
        assertTrue(model.contains(archiveUri, RDF.type, NFO.Archive));
        // everything that is directly linked with an isPartOf link to the archive itself
        Resource ziptestfolder = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        checkStatement(ziptestfolder.asURI(), RDF.type, NFO.Folder, model);
        assertEquals("uri:dummyuri/zip-test/", ziptestfolder.toString());
        
        List<Resource> ziptestContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestfolder);        
        assertEquals("uri:dummyuri/zip-test/test1.txt", ziptestContent.get(0).toString());
        assertEquals("uri:dummyuri/zip-test/test2.txt", ziptestContent.get(1).toString());
        assertEquals("uri:dummyuri/zip-test/test3.txt", ziptestContent.get(2).toString());
        assertEquals("uri:dummyuri/zip-test/subfolder/", ziptestContent.get(3).toString());
        assertEquals("uri:dummyuri/zip-test/microsoft-word-2000.doc", ziptestContent.get(4).toString());
        assertEquals(5, ziptestContent.size());
        checkStatement(ziptestContent.get(3).asURI(), RDF.type, NFO.Folder, model);
        
        List<Resource> subfolderContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestContent.get(3));        
        assertEquals("uri:dummyuri/zip-test/subfolder/test4.txt", subfolderContent.get(0).toString());
        assertEquals("uri:dummyuri/zip-test/subfolder/test5.txt", subfolderContent.get(1).toString());
        assertEquals("uri:dummyuri/zip-test/subfolder/pdf-manyauthors.pdf", subfolderContent.get(2).toString());
        assertEquals(3, subfolderContent.size());
        
        // file names
        assertSingleValueProperty(model, ziptestfolder, NFO.fileName, "zip-test");
        assertSingleValueProperty(model, ziptestContent.get(0), NFO.fileName, "test1.txt");
        assertSingleValueProperty(model, ziptestContent.get(1), NFO.fileName, "test2.txt");
        assertSingleValueProperty(model, ziptestContent.get(2), NFO.fileName, "test3.txt");
        assertSingleValueProperty(model, ziptestContent.get(3), NFO.fileName, "subfolder");
        assertSingleValueProperty(model, subfolderContent.get(0), NFO.fileName, "test4.txt");
        assertSingleValueProperty(model, subfolderContent.get(1), NFO.fileName, "test5.txt");
        assertSingleValueProperty(model, subfolderContent.get(2), NFO.fileName, "pdf-manyauthors.pdf");
        assertSingleValueProperty(model, ziptestContent.get(4), NFO.fileName, "microsoft-word-2000.doc");
        
        // hashes
        assertCrc32Hash(model, ziptestfolder, 0);
        assertCrc32Hash(model, ziptestContent.get(0), 2227022722L);
        assertCrc32Hash(model, ziptestContent.get(1), 2218881576L);
        assertCrc32Hash(model, ziptestContent.get(2), 2479617527L);
        assertCrc32Hash(model, ziptestContent.get(3), 0);
        assertCrc32Hash(model, subfolderContent.get(0), 4267625106L);
        assertCrc32Hash(model, subfolderContent.get(1), 544725069L);
        assertCrc32Hash(model, subfolderContent.get(2), 1261442136L);
        assertCrc32Hash(model, ziptestContent.get(4), 389689384);
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    private void assertCrc32Hash(Model model, Resource resource, long i) {
        Resource hashResource = findSingleObjectResource(model, resource, NFO.hasHash);
        assertSingleValueProperty(model, hashResource, RDF.type, NFO.FileHash);
        assertSingleValueProperty(model, hashResource, NFO.hashAlgorithm, "CRC-32");
        assertSingleValueProperty(model, hashResource, NFO.hashValue, String.valueOf(i));
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