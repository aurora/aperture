/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;

/**
 * Tests the FileDataObject class
 */
public class TestFileDataObject extends ApertureTestBase {

    private static final URI TEST_URI = new URIImpl("uri:test");
    
    public void testStreamStream() throws IOException {
        InputStream streamFromResource = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        byte [] bytesFromResource = IOUtil.readBytes(streamFromResource);
        streamFromResource = ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        if (!streamFromResource.markSupported()) {
            streamFromResource = new BufferedInputStream(streamFromResource);
        }
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,streamFromResource);
        InputStream streamFromDataObject = object.getContent();
        byte [] bytesFromDataObject = IOUtil.readBytes(streamFromDataObject);
        assertTrue(bytesFromDataObject.length == bytesFromResource.length);
        for (int i = 0; i< bytesFromDataObject.length; i++) {
            assertTrue(bytesFromDataObject[i] == bytesFromResource[i]);
        }
        bytesFromDataObject = null;
        bytesFromResource = null;
        object.dispose();
    }
    
    public void testStreamFile() throws IOException {
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,stream);
        File file = object.getFile();
        assertNull(file);
        object.dispose();
    }
    
    public void testFileStream() throws IOException {
        File tempFile = File.createTempFile("test", "tmp");
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        IOUtil.writeStream(stream, tempFile);
        stream.close();
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        long fileSize = tempFile.length();
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,tempFile);
        InputStream contentStream = object.getContent();
        byte [] bytes = IOUtil.readBytes(contentStream);
        assertEquals(bytes.length,fileSize);
        bytes = null;
        object.dispose();
        tempFile.delete();
    }
    
    public void testFileFile() throws IOException {
        File tempFile = File.createTempFile("test", "tmp");
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        IOUtil.writeStream(stream, tempFile);
        stream.close();
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        long fileSize = tempFile.length();
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,tempFile);
        File otherFile = object.getFile();
        assertTrue(otherFile == tempFile);
        object.dispose();
        tempFile.delete();
    }
    
    public void testDownloadFile() throws IOException {
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        byte [] originalBytes = IOUtil.readBytes(stream);
        stream = ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,stream);
        File tempFile = object.downloadContent();
        tempFile.deleteOnExit();
        assertNotNull(tempFile);
        byte [] bytesFromTempFile = IOUtil.readBytes(new FileInputStream(tempFile));
        assertTrue(originalBytes.length == bytesFromTempFile.length);
        for (int i = 0; i < originalBytes.length; i++) {
            assertTrue(originalBytes[i] == bytesFromTempFile[i]);
        }
        originalBytes = null;
        bytesFromTempFile = null;
        object.dispose();
    }
    
    public void testFaultyDownloadFile() throws IOException {
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,stream);
        object.getContent().mark(100);
        object.getContent().read();
        object.getContent().read();
        try {
            File tempFile = object.downloadContent();
            fail();
        } catch (IOException e) {
            // the content stream has not been reset so this should happen
        }
        object.getContent().reset();
        // now after a proper reset no exception should be thrown
        File tempFile = object.downloadContent();
        tempFile.deleteOnExit();
        assertNotNull(tempFile);
        object.dispose();
    }
}

