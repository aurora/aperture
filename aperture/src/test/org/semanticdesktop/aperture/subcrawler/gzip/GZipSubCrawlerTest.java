/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A test case for the gzip subcrawler
 */
public class GZipSubCrawlerTest extends ApertureTestBase {

    private static final String GZIP_TEST_FILE_URI = "uri:dummyuri/gzip-txt-gziptest.txt.gz";
    
    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testGZipTest() throws Exception {
        GZipSubCrawler subCrawler = new GZipSubCrawler();
        GZipSubCrawlerHandler handler = new GZipSubCrawlerHandler();
        metadata = subCrawl(DOCS_PATH + "gzip-txt-gziptest.txt.gz", subCrawler, handler);
        Model model = metadata.getModel();
        URI archiveUri = model.createURI(GZIP_TEST_FILE_URI);
        // the archiveUri is an archive
        assertTrue(model.contains(archiveUri, RDF.type, NFO.Archive));
        // the archive has a single part
        Resource contentResource = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        // that content part is an archive item
        checkStatement(contentResource.asURI(), RDF.type, NFO.ArchiveItem, model);
        // the uri of the archive item is the same as those of the archive with the .gz truncated
        assertEquals("uri:dummyuri/gzip-txt-gziptest.txt", contentResource.toString());
        // the name of the content file has been properly extracted from the content uri
        assertSingleValueProperty(model, contentResource, NFO.fileName, "gzip-txt-gziptest.txt");
        // the handler has spotted one new object and nothing else
        assertNewModUnmodDel(handler, 1, 0, 0, 0);
        // the extracted text is as follows, this means that the decompression works
        assertEquals("This is a text file for Aperture\n", handler.getExtractedString());
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }

    private RDFContainer subCrawl(String string, GZipSubCrawler subCrawler, GZipSubCrawlerHandler handler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(string, this.getClass());
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl(GZIP_TEST_FILE_URI));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
    }
    
    private void assertNewModUnmodDel(GZipSubCrawlerHandler handler, int newObjects,
            int changedObjects, int unchangedObjects, int deletedObjects) {
        assertEquals(handler.getNewObjects().size(), newObjects);
        assertEquals(handler.getChangedObjects().size(), changedObjects);
        assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
        assertEquals(handler.getDeletedObjects().size(), deletedObjects);
    }
}


class GZipSubCrawlerHandler implements SubCrawlerHandler, RDFContainerFactory {
    
    private Model model;

    private int numberOfObjects;
    
    private Set<String> newObjects;
    private Set<String> changedObjects;
    private Set<String> unchangedObjects;
    private Set<String> deletedObjects;
    
    private String extractedString;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs the GZipSubCrawlerHandler
     */
    public GZipSubCrawlerHandler() {
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
    
    /**
     * Closes the underlying model
     */
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
        if (object instanceof FileDataObject) {
            try {
                extractedString = IOUtil.readString(((FileDataObject)object).getContent(), Charset.forName("US-ASCII"));
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
    
    Model getModel() {
        return model;
    }
    
    int getNumberOfObjects() {
        return numberOfObjects;
    }
    
    Set<String> getChangedObjects() {
        return changedObjects;
    }
    
    Set<String> getDeletedObjects() {
        return deletedObjects;
    }
    
    Set<String> getNewObjects() {
        return newObjects;
    }

    Set<String> getUnchangedObjects() {
        return unchangedObjects;
    }
    
    String getExtractedString() {
        return extractedString;
    }

    public RDFContainerFactory getRDFContainerFactory(String url) {
        return this;
    }
}