/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import info.aduna.io.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
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
import org.semanticdesktop.aperture.subcrawler.impl.SubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A base class for archiver tests
 */
public class SubCrawlerTestBase extends ApertureTestBase {
    
    /**
     * Performs basic checks of the model. It is used by archivers that have crawled the test archive file.
     * I.e. the same content archived with different algorithms. See for example the zip-test.zip file for
     * details.
     * 
     * @param model
     * @throws Exception
     */
    protected void doBasicArchiverTests(Model model, String resourceName) throws Exception {
        URI archiveUri = model.createURI("uri:dummyuri/" + resourceName);
        assertTrue(model.contains(archiveUri, RDF.type, NFO.Archive));
        // everything that is directly linked with an isPartOf link to the archive itself
        Resource ziptestfolder = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        checkStatement(ziptestfolder.asURI(), RDF.type, NFO.Folder, model);
        assertEquals(archiveUri.toString() + "/zip-test/", ziptestfolder.toString());
        
        List<Resource> ziptestContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestfolder);        
        assertEquals(ziptestfolder.toString() + "test1.txt", ziptestContent.get(0).toString());
        assertEquals(ziptestfolder.toString() + "test2.txt", ziptestContent.get(1).toString());
        assertEquals(ziptestfolder.toString() + "test3.txt", ziptestContent.get(2).toString());
        assertEquals(ziptestfolder.toString() + "subfolder/", ziptestContent.get(3).toString());
        assertEquals(ziptestfolder.toString() + "microsoft-word-2000.doc", ziptestContent.get(4).toString());
        assertEquals(5, ziptestContent.size());
        checkStatement(ziptestContent.get(3).asURI(), RDF.type, NFO.Folder, model);
        
        List<Resource> subfolderContent = findSubjectResourceList(model, NFO.belongsToContainer, ziptestContent.get(3));        
        assertEquals(ziptestfolder.toString() + "subfolder/test4.txt", subfolderContent.get(0).toString());
        assertEquals(ziptestfolder.toString() + "subfolder/test5.txt", subfolderContent.get(1).toString());
        assertEquals(ziptestfolder.toString() + "subfolder/pdf-manyauthors.pdf", subfolderContent.get(2).toString());
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
    }
    
    /**
     * A basic test if the extraction actually works
     * @throws Exception
     */
    public void doBasicCompressorTest(Model model, String resourceName, String contentFileName) throws Exception {
        String compressedFileUri = "uri:dummyuri/" + resourceName;
        String contentUri = "uri:dummyuri/" + contentFileName;
        URI archiveUri = model.createURI(compressedFileUri);
        // the archiveUri is an archive
        assertTrue(model.contains(archiveUri, RDF.type, NFO.Archive));
        // the archive has a single part
        Resource contentResource = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        // that content part is an archive item
        checkStatement(contentResource.asURI(), RDF.type, NFO.ArchiveItem, model);
        // the uri of the archive item is the same as those of the archive with the .gz truncated
        assertEquals(contentUri, contentResource.toString());
        // the name of the content file has been properly extracted from the content uri
        assertSingleValueProperty(model, contentResource, NFO.fileName, contentFileName);
        // the handler has spotted one new object and nothing else
    }
    
    public void testCrawlerIncremental(SubCrawlerFactory factory, String subdirName, String resourceName, String fileExtension, int numberOfEntries) throws Exception {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), subdirName).getCanonicalFile();
        try {
            // create a temporary folder containing a temporary file
            // unfortunately there is no File.createTempDir
            
            FileUtil.deltree(tmpDir);
            assertTrue(tmpDir.mkdir());
    
            // put two files in it
            File tmpFile1 = File.createTempFile("file-", ".txt", tmpDir);
            IOUtil.writeString("test file 1", tmpFile1);
    
            File tmpFile2 = File.createTempFile("file-", ".txt", tmpDir);
            IOUtil.writeString("test file 2", tmpFile2);
    
            File tmpFile4 = File.createTempFile("file-skipme-", ".txt", tmpDir);
            IOUtil.writeString("test file 4", tmpFile4);
    
            // put another folder containing another file in it
            File subDir = new File(tmpDir, "subdir");
            subDir.mkdir();
    
            File tmpFile3 = File.createTempFile("file-", fileExtension, subDir);
            IOUtil.writeStream(ResourceUtil.getInputStream(DOCS_PATH + resourceName), tmpFile3);        
            
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
            subCrawlerRegistry.add(factory);
            // setup a CrawlerHandler
            TestIncrementalCrawlerHandler crawlerHandler = new TestIncrementalCrawlerHandler(subCrawlerRegistry);
            crawler.setCrawlerHandler(crawlerHandler);
    
            // start Crawling
            crawler.crawl();
            
            // inspect results (we have 6 files + the compressed entries)
            assertNewModUnmodDel(crawlerHandler, 6 + numberOfEntries, 0, 0, 0);
            assertTrue(accessData.getStoredIDs().size() == 6 + numberOfEntries);
            
            crawler.crawl();
            // recursive touching, the file has been reported as unmodified
            assertTrue(crawlerHandler.getUnchangedObjects().contains(toURI(tmpFile3).toString()));
            assertNewModUnmodDel(crawlerHandler, 0, 0, 6 + numberOfEntries, 0);
            assertTrue(accessData.getStoredIDs().size() == 6 + numberOfEntries);
            
            // recursive removal
            safelySleep(1200); //for some sanity ,it seems that a fast server is able to run two crawls in the same second
            tmpFile3.delete();
            crawler.crawl();
            
            // the folder has been modified, ten resources have been deleted (zip file + 9 zip entries inside)
            assertNewModUnmodDel(crawlerHandler, 0, 1, 4, 1 + numberOfEntries);
            assertTrue(accessData.getStoredIDs().size() == 5);
    
            Model model = crawlerHandler.getModel();
    
            model.close();
            configuration.getModel().close();
        } finally {
        FileUtil.deltree(tmpDir);
        }
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
    
    protected RDFContainer subCrawl(String resourceName, SubCrawler subCrawler, TestBasicSubCrawlerHandler handler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(DOCS_PATH + resourceName, this.getClass());
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl("uri:dummyuri/" + resourceName));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
    }
    
    protected class TestBasicSubCrawlerHandler implements SubCrawlerHandler, RDFContainerFactory {
        
        protected Model model;

        protected int numberOfObjects;
        
        protected Set<String> newObjects;
        protected Set<String> changedObjects;
        protected Set<String> unchangedObjects;
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        
        /**
         * Constructs the ZipSubCrawlerHandler
         */
        public TestBasicSubCrawlerHandler() throws ModelException {
            model = RDF2Go.getModelFactory().createModel();
            model.open();
            newObjects = new HashSet<String>();
            changedObjects = new HashSet<String>();
            unchangedObjects = new HashSet<String>();
            numberOfObjects = 0;
            newObjects.clear();
            changedObjects.clear();
            unchangedObjects.clear();
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
    
    protected class CompressorSubCrawlerHandler extends TestBasicSubCrawlerHandler {
        
        private String extractedString;
        
        public CompressorSubCrawlerHandler() throws ModelException {
            super();
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
        public String getExtractedString() {
            return extractedString;
        }
    }
    
    protected class TestIncrementalCrawlerHandler implements CrawlerHandler, RDFContainerFactory {
        
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

