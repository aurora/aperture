/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.util.List;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.AbstractSubcrawlerTest;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A test case for the zip subcrawler
 */
public class ZipSubCrawlerTest extends AbstractSubcrawlerTest {
    private RDFContainer metadata;
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void testZipTest() throws Exception {
        ZipSubCrawler subCrawler = new ZipSubCrawler();
        metadata = subCrawl("zip-test.zip", subCrawler, new ArchiverSubCrawlerHandler());
        Model model = metadata.getModel();
        
        doBasicArchiverTests(model, "zip-test.zip");
        
        URI archiveUri = model.createURI("uri:dummyuri/zip-test.zip");
        Resource ziptestfolder = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        List<Resource> ziptestContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestfolder);
        List<Resource> subfolderContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestContent.get(3));
        
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


