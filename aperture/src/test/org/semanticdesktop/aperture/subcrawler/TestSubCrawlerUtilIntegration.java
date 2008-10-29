/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.io.InputStream;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.impl.DefaultSubCrawlerRegistry;
import org.semanticdesktop.aperture.util.ResourceUtil;

/**
 * An integration test for the {@link SubCrawlerUtil#getDataObject} method. It is in a separate class
 * because it invokes concrete implementations of SubCrawlers, and therefore can't belong in the core
 * bundle, with the rest of the SubCrawler tests.
 */
public class TestSubCrawlerUtilIntegration extends ApertureTestBase {

    /**
     * Tests if the method can extract a PDF attached to an .eml file, tarred and gzipped.
     * @throws Exception 
     */
    public void testGetDataObject() throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "mail-multipart-test.eml.tar.gz",
            getClass());
        URI uri = new URIImpl(
            "mime:" +
              "tar:" +
                "gzip:" +
                  "file:///C:/somefolder/mail-multipart-test.eml.tar.gz" +
                "!/mail-multipart-test.eml.tar" +
              "!/mail-multipart-test.eml" +
            "!/1");
        TestRDFContainerFactory fac = new TestRDFContainerFactory();
        DataObject obj = SubCrawlerUtil.getDataObject(uri, stream, null, null, null, fac,
            new DefaultSubCrawlerRegistry());
        assertNotNull(obj);
        assertTrue(obj instanceof FileDataObject);
        assertMimeType("application/pdf", uri, ((FileDataObject)obj).getContent());
        
        obj.dispose();
        for (Map.Entry<String, RDFContainer> entry : fac.returnedContainers.entrySet()) {
            assertFalse(entry.getValue().getModel().isOpen());
        }
    }
    
    /**
     * Tests if the method can extract a file whose name contains a space from inside a ZIP archive.
     * @throws Exception 
     */
    public void testGetDataObjectWithSpace() throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "zip-problem.zip",
            getClass());
        URI uri = new URIImpl(
            "zip:" +
               "file:///C:/somefolder/zip-problem.zip" +
            "!/D_/Installers/installer+2005.1+rc1/icon-16x16.gif");
        TestRDFContainerFactory fac = new TestRDFContainerFactory();
        DataObject obj = SubCrawlerUtil.getDataObject(uri, stream, null, null, null, fac,
            new DefaultSubCrawlerRegistry());
        assertNotNull(obj);
        assertTrue(obj instanceof FileDataObject);
        assertMimeType("image/png", uri, ((FileDataObject)obj).getContent());
        
        obj.dispose();
        for (Map.Entry<String, RDFContainer> entry : fac.returnedContainers.entrySet()) {
            assertFalse(entry.getValue().getModel().isOpen());
        }
    }
}

