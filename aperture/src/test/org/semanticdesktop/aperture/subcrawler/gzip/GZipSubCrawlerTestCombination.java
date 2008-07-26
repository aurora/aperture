/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import info.aduna.io.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.file.FileAccessorFactory;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerRegistry;
import org.semanticdesktop.aperture.subcrawler.impl.SubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;

public class GZipSubCrawlerTestCombination extends ApertureTestBase {

    private static final String TMP_SUBDIR = "TestZipSubCrawlerCombination.tmpDir";

    private File tmpDir;

    private File subDir;

    private File tmpFile1;

    private File tmpFile2;

    private File tmpFile3;

    private File tmpFile4;

    public void setUp() throws IOException {
        // create a temporary folder containing a temporary file
        // unfortunately there is no File.createTempDir
        tmpDir = new File(System.getProperty("java.io.tmpdir"), TMP_SUBDIR).getCanonicalFile();
        FileUtil.deltree(tmpDir);
        assertTrue(tmpDir.mkdir());

        // put two files in it
        tmpFile1 = File.createTempFile("file-", ".txt", tmpDir);
        IOUtil.writeString("test file 1", tmpFile1);

        tmpFile2 = File.createTempFile("file-", ".txt", tmpDir);
        IOUtil.writeString("test file 2", tmpFile2);

        tmpFile4 = File.createTempFile("file-skipme-", ".txt", tmpDir);
        IOUtil.writeString("test file 4", tmpFile4);

        // put another folder containing another file in it
        subDir = new File(tmpDir, "subdir");
        subDir.mkdir();

        tmpFile3 = File.createTempFile("file-", ".zip", subDir);
        IOUtil.writeStream(ResourceUtil.getInputStream(DOCS_PATH + "gzip-txt-gziptest.txt.gz"), tmpFile3);

    }

    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }

    public void testCrawler() throws ModelException, InterruptedException {
        // create a DataSource
        RDFContainer configuration = createRDFContainer("urn:test:dummySource");
        FileSystemDataSource dataSource = new FileSystemDataSource();
        dataSource.setConfiguration(configuration);
        
        dataSource.setRootFolder(tmpDir.getAbsolutePath());
        
        // create a Crawler for this DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        AccessData accessData = new AccessDataImpl();
        crawler.setAccessData(accessData);
        
        crawler.setDataSource(dataSource);
        // setup a DataAccessorRegistry
        DataAccessorRegistryImpl registry = new DataAccessorRegistryImpl();
        registry.add(new FileAccessorFactory());
        crawler.setDataAccessorRegistry(registry);

        SubCrawlerRegistry subCrawlerRegistry = new SubCrawlerRegistryImpl();
        subCrawlerRegistry.add(new GZipSubCrawlerFactory());
        // setup a CrawlerHandler
        TestIncrementalCrawlerHandler crawlerHandler = new TestIncrementalCrawlerHandler(subCrawlerRegistry);
        crawler.setCrawlerHandler(crawlerHandler);

        // start Crawling
        crawler.crawl();
        
        // inspect results (we have 6 files + 1 entry in the zip archive)
        assertNewModUnmodDel(crawlerHandler, 7, 0, 0, 0);
        assertTrue(accessData.getStoredIDs().size() == 7);
        
        crawler.crawl();
        // recursive touching, the file has been reported as unmodified
        assertTrue(crawlerHandler.getUnchangedObjects().contains(toURI(tmpFile3).toString()));
        assertNewModUnmodDel(crawlerHandler, 0, 0, 7, 0);
        assertTrue(accessData.getStoredIDs().size() == 7);
        
        // recursive removal
        safelySleep(1200); //for some sanity ,it seems that a fast server is able to run two crawls in the same second
        tmpFile3.delete();
        crawler.crawl();
        
        // the folder has been modified, two resources have been deleted (gzip file and the txt inside) 
        assertNewModUnmodDel(crawlerHandler, 0, 1, 4, 2);
        assertTrue(accessData.getStoredIDs().size() == 5);

        Model model = crawlerHandler.getModel();

        model.close();
        configuration.getModel().close();
    }
    
    public void assertNewModUnmodDel(TestIncrementalCrawlerHandler handler, int newObjects,
            int changedObjects, int unchangedObjects, int deletedObjects) {
        assertEquals(handler.getNewObjects().size(), newObjects);
        assertEquals(handler.getChangedObjects().size(), changedObjects);
        assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
        assertEquals(handler.getDeletedObjects().size(), deletedObjects);
    }

    private URI toURI(File file) {
        return new URIImpl(file.toURI().toString());
    }

    private class TestIncrementalCrawlerHandler implements CrawlerHandler, RDFContainerFactory {
        
        private Model model;

        private int numberOfObjects;
        
        private SubCrawlerRegistry subCrawlerRegistry;
        
        private MimeTypeIdentifier mimeTypeIdentifier;
        
        private Set<String> newObjects;
        private Set<String> changedObjects;
        private Set<String> unchangedObjects;
        private Set<String> deletedObjects;
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        
        public TestIncrementalCrawlerHandler(SubCrawlerRegistry registry) throws ModelException {
            model = RDF2Go.getModelFactory().createModel();
            model.open();
            newObjects = new HashSet<String>();
            changedObjects = new HashSet<String>();
            unchangedObjects = new HashSet<String>();
            deletedObjects = new HashSet<String>();
            this.mimeTypeIdentifier = new MagicMimeTypeIdentifier();
            this.subCrawlerRegistry = registry;
        }
        
        public void close() {
            model.close();
        }
       
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////// CRAWLER HANDLER METHODS //////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        
        public void crawlStarted(Crawler crawler) {
            numberOfObjects = 0;
            newObjects.clear();
            changedObjects.clear();
            unchangedObjects.clear();
            deletedObjects.clear();
        }

        public void objectChanged(Crawler crawler, DataObject object) {
            changedObjects.add(object.getID().toString());
            if (object instanceof FileDataObject) {
                try {
                    process((FileDataObject)object,crawler);
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (SubCrawlerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // free any resources contained by this DataObject
            object.dispose();
        }

        public void objectNew(Crawler crawler, DataObject object) {
            numberOfObjects++;
            newObjects.add(object.getID().toString());
            if (object instanceof FileDataObject) {
                try {
                    process((FileDataObject)object,crawler);
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (SubCrawlerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            object.dispose();
        }

        public void objectNotModified(Crawler crawler, String url) {
            numberOfObjects++;
            unchangedObjects.add(url);
        }

        public void objectRemoved(Crawler crawler, String url) {
            deletedObjects.add(url);
        }
        
        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }
        
        private void process(FileDataObject object, Crawler crawler) throws IOException, SubCrawlerException {
            URI id = object.getID();
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            InputStream contentStream = object.getContent();
            contentStream.mark(minimumArrayLength + 10); // add some for safety
            byte[] bytes = IOUtil.readBytes(contentStream, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);
            if (mimeType == null) {return;}
            contentStream.reset();
            applySubCrawler(object.getID(), contentStream, mimeType, object, crawler);
        }
        
        private boolean applySubCrawler(URI id, InputStream contentStream, String mimeType,
                DataObject object, Crawler crawler) throws SubCrawlerException {
            Set subCrawlers = subCrawlerRegistry.get(mimeType);
            if (!subCrawlers.isEmpty()) {
                SubCrawlerFactory factory = (SubCrawlerFactory) subCrawlers.iterator().next();
                SubCrawler subCrawler = factory.get();
                crawler.runSubCrawler(subCrawler, object, contentStream, null, mimeType);
                return true;
            }
            else {
                return false;
            }
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
        
        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            // we don't need to do anything   
        }

        public void accessingObject(Crawler crawler, String url) {
            // we don't need to do anything
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            // we don't need to do anything
        }

        public void clearStarted(Crawler crawler) {
            // we don't need to do anything
        }

        public void clearingObject(Crawler crawler, String url) {
            // we don't need to do anything        
        }
    }
}
