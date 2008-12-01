/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.subcrawler.base.AbstractArchiverSubCrawler.ArchiveEntry;

/**
 * Tests the basic functionality provided by the AbstractArchiverSubCrawler
 */
public class AbstractArchiverSubCrawlerTest extends SubCrawlerTestBase {

    /**
     * Tests whether the uris of archive entries conform to the scheme
     * @throws Exception
     */
    public void testPlainUriGeneration() throws Exception {
        DummyArchiverSubCrawler sc = new DummyArchiverSubCrawler();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        URI archiveUri = new URIImpl("file://dummyarchive.zip");
        RDFContainer parentMetadata = new RDFContainerImpl(createModel(),archiveUri);
        sc.subCrawl(archiveUri, getDummyStream(), handler, null, null, null, null, parentMetadata);
        assertTrue(handler.getNewObjects().contains("zip:file://dummyarchive.zip!/rootfolder/"));
        assertTrue(handler.getNewObjects().contains("zip:file://dummyarchive.zip!/root-file.txt"));
        assertTrue(handler.getNewObjects().contains("zip:file://dummyarchive.zip!/rootfolder/subfolder/"));
        assertTrue(handler.getNewObjects().contains("zip:file://dummyarchive.zip!/rootfolder/subfolder/sub-file.txt"));
        handler.close();
        parentMetadata.dispose();
    }
    
    /**
     * Tests whether the uris of archive entries conform to the scheme
     * @throws Exception
     */
    public void testEmbeddedUriGeneration() throws Exception {
        DummyArchiverSubCrawler sc = new DummyArchiverSubCrawler();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        URI archiveUri = new URIImpl("tar:gzip:file://parentarchive.tar.gz!/parentarchive.tar!/dummyarchive.zip");
        RDFContainer parentMetadata = new RDFContainerImpl(createModel(),archiveUri);
        sc.subCrawl(archiveUri, getDummyStream(), handler, null, null, null, null, parentMetadata);
        assertTrue(handler.getNewObjects().contains(
            "zip:tar:gzip:file://parentarchive.tar.gz!/parentarchive.tar!/dummyarchive.zip!/rootfolder/"));
        assertTrue(handler.getNewObjects().contains(
            "zip:tar:gzip:file://parentarchive.tar.gz!/parentarchive.tar!/dummyarchive.zip!/root-file.txt"));
        assertTrue(handler.getNewObjects().contains(
            "zip:tar:gzip:file://parentarchive.tar.gz!/parentarchive.tar!/dummyarchive.zip!/rootfolder/subfolder/"));
        assertTrue(handler.getNewObjects().contains(
            "zip:tar:gzip:file://parentarchive.tar.gz!/parentarchive.tar!/dummyarchive.zip!/rootfolder/subfolder/sub-file.txt"));
        
        assertEquals(4,handler.getNewObjects().size());
        handler.close();
        parentMetadata.dispose();
    }
    
    /**
     * This is supposed to return a non-null input stream, the actual content is irrelevant, since it's not used
     * by the dummy subcrawler.
     * @return
     */
    private InputStream getDummyStream() {
        return org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(DOCS_PATH + "tar-test.tar", this.getClass());
    }

    private static class DummyArchiverSubCrawler extends AbstractArchiverSubCrawler {

        private class DummyArchiveInputStream extends ArchiveInputStream {

            private int counter = 1;

            public DummyArchiveInputStream(InputStream stream) {
                super(stream);
            }

            @Override
            public ArchiveEntry getNextEntry() throws IOException {
                ArchiveEntry entry = null;
                switch (counter) {
                case 1:
                    entry = new DummyArchiveEntry("rootfolder/");
                    break;
                case 2:
                    entry = new DummyArchiveEntry("root-file.txt");
                    break;
                case 3:
                    entry = new DummyArchiveEntry("rootfolder/subfolder/");
                    break;
                case 4:
                    entry = new DummyArchiveEntry("rootfolder/subfolder/sub-file.txt");
                    break;
                default:
                    // leave entry at null
                    break;
                }
                counter++;
                return entry;
            }

            @Override
            public void closeEntry() throws IOException {/* do nothing */}
        }

        @Override
        protected ArchiveInputStream getArchiveInputStream(InputStream in) {
            return new DummyArchiveInputStream(in);
        }

        @Override
        public String getUriPrefix() {
            return "zip";
        }
    }

    private static class DummyArchiveEntry extends ArchiveEntry {

        private String path;

        public DummyArchiveEntry(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }
}

