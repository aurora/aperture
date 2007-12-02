/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.BufferedInputStream;
import java.io.File;
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
        InputStream stream = 
            ResourceUtil.getInputStream(DOCS_PATH + "microsoft-excel-2000.xls", TestFileDataObject.class);
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model,TEST_URI);
        
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        
        FileDataObject object = new FileDataObjectBase(new URIImpl("uri:testuri"),null,container,stream);
        InputStream otherStream = object.getContent();
        
        assertTrue(stream == otherStream);
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
}

