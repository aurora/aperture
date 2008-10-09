/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A test case for the zip subcrawler
 */
public class ZipSubCrawlerTest extends SubCrawlerTestBase {
    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testZipTest() throws Exception {
        ZipSubCrawler subCrawler = new ZipSubCrawler();
        metadata = subCrawl("zip-test.zip", subCrawler, new TestBasicSubCrawlerHandler());
        Model model = metadata.getModel();
        
        doBasicArchiverTests(model, "zip-test.zip", "zip");
        
        URI archiveUri = model.createURI("uri:dummyuri/zip-test.zip");
        Resource ziptestfolder = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        Set<Resource> ziptestContent = findSubjectResourceSet(model, NFO.belongsToContainer, ziptestfolder);
        URI subfolderUri = model.createURI("zip:uri:dummyuri/zip-test.zip!/subfolder/");
        Set<Resource> subfolderContent = findSubjectResourceSet(model, NFO.belongsToContainer, subfolderUri);
        
        // hashes
        assertCrc32Hash(model, ziptestfolder, 0);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/test1.txt"), 2227022722L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/test2.txt"), 2218881576L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/test3.txt"), 2479617527L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/subfolder/"), 0);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/subfolder/test4.txt"), 4267625106L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/subfolder/test5.txt"), 544725069L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/subfolder/pdf-manyauthors.pdf"), 1261442136L);
        assertCrc32Hash(model, new URIImpl("zip:uri:dummyuri/zip-test.zip!/zip-test/microsoft-word-2000.doc"), 389689384);
        
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testZipSubCrawlerIncrementalCombination() throws Exception {
        testCrawlerIncremental(new ZipSubCrawlerFactory(), "TestZipSubCrawlerCombination.tmpDir", "zip-test.zip", ".zip",9);
    }
    
    private void assertCrc32Hash(Model model, Resource resource, long i) {
        Resource hashResource = findSingleObjectResource(model, resource, NFO.hasHash);
        assertSingleValueProperty(model, hashResource, RDF.type, NFO.FileHash);
        assertSingleValueProperty(model, hashResource, NFO.hashAlgorithm, "CRC-32");
        assertSingleValueProperty(model, hashResource, NFO.hashValue, String.valueOf(i));
    }
}


