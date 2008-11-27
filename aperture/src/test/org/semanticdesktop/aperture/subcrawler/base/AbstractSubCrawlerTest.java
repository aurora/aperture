/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.subcrawler.PathNotFoundException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A suite of tests of the functionality provided by the AbstractSubCrawler
 */
public class AbstractSubCrawlerTest extends SubCrawlerTestBase {

    /**
     * Tests the default implementation of the getDataObject method. The AbstractSubCrawler should
     * 
     * <ul>
     * <li>bypass all the objects before the desired one - dispose them properly</li>
     * <li>return the correct one</li>
     * <li>stop the subCrawler after returning the correct object so that no other rdfcontainers are obtained
     *   from the factory</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testGetDataObject() throws Exception {
        GetDataObjectDummySubCrawler subCrawler = new GetDataObjectDummySubCrawler();
        DataObject obj = subCrawler.getDataObject(new URIImpl("uri:dummyuri/file.dummy"), "folder/file.txt", 
            null, null, null, null, new RDFContainerFactoryImpl());
        assertEquals(new URIImpl("dummy1:uri:dummyuri/file.dummy!/folder/file.txt"),obj.getID());
        assertEquals("file.txt",obj.getMetadata().getString(NFO.fileName));
        assertTrue(subCrawler.stopCalled);
        assertEquals(2,subCrawler.getCounter());
        obj.dispose();
    }
    
    /**
     * Tests the default implementation of the getDataObject called with a non-existent resource path. The
     * AbstractSubCrawler should iterate through all data objects returned by the subcrawler, dispose them all
     * and throw a PathNotFoundException at the end
     * 
     * @throws Exception
     */
    public void testGetNonExistentDataObject() throws Exception {
        GetDataObjectDummySubCrawler subCrawler = new GetDataObjectDummySubCrawler();
        DataObject obj = null;
        try {
            obj = subCrawler.getDataObject(new URIImpl("uri:dummyuri/file.dummy"),
                "folder/nonexistentfile.txt", null, null, null, null, new RDFContainerFactoryImpl());
        }
        catch (PathNotFoundException e) {
            assertNull(obj);
            assertFalse(subCrawler.stopCalled);
            assertEquals(5, subCrawler.getCounter());
        }
    }
    
    private class GetDataObjectDummySubCrawler extends AbstractSubCrawler {

        boolean stopCalled = false;
        private int counter = 0;
        
        
        public int getCounter() {
            return counter;
        }

        @Override
        public String getUriPrefix() {
            return "dummy1";
        }

        public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
                AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata)
                throws SubCrawlerException {
            URI parentUri = parentMetadata.getDescribedUri();
            RDFContainerFactory fac = handler.getRDFContainerFactory(parentUri.toString());
            URI childUri = null;
            RDFContainer cont = null;
            while (!stopCalled) {
                counter++;
                switch (counter) {
                case 1:
                    childUri = createChildUri(parentUri, "folder/");
                    cont = fac.getRDFContainer(childUri);
                    cont.put(RDF.type, NFO.Folder);
                    cont.put(NFO.fileName, "folder");
                    break;
                case 2:
                    childUri = createChildUri(parentUri, "folder/file.txt");
                    cont = fac.getRDFContainer(childUri);
                    cont.put(RDF.type, NFO.FileDataObject);
                    cont.put(NFO.fileName, "file.txt");
                    break;
                case 3:
                    childUri = createChildUri(parentUri, "folder/subfolder/");
                    cont = fac.getRDFContainer(childUri);
                    cont.put(RDF.type, NFO.Folder);
                    cont.put(NFO.fileName, "subfolder");
                    break;
                case 4:
                    childUri = createChildUri(parentUri, "folder/subfolder/subfile.txt");
                    cont = fac.getRDFContainer(childUri);
                    cont.put(RDF.type, NFO.FileDataObject);
                    cont.put(NFO.fileName, "subfile.txt");
                    break;
                default:
                    return;
                }
                DataObject obj = new DataObjectBase(childUri,dataSource,cont);
                handler.objectNew(obj);
            }
        }

        public void stopSubCrawler() {
            this.stopCalled = true;
        }        
    }
}

