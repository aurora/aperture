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
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.TestIncrementalCrawlerHandler;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.file.FileAccessorFactory;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
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
     * @param resourceName 
     * @throws Exception
     */
    protected void doBasicArchiverTests(Model model, String resourceName, String prefix) throws Exception {
        URI archiveUri = model.createURI("uri:dummyuri/" + resourceName);
        assertTrue(model.contains(archiveUri, RDF.type, NFO.Archive));
        // everything that is directly linked with an isPartOf link to the archive itself
        Resource testfolder = findSingleSubjectResource(model, NFO.belongsToContainer, archiveUri);
        String folderPath = testfolder.toString();
        checkStatement(testfolder.asURI(), RDF.type, NFO.Folder, model);
        assertEquals(prefix + ":" + archiveUri.toString() + "!/zip-test/", folderPath);
        Set<Resource> testContent = findSubjectResourceSet(model, NFO.belongsToContainer, testfolder);        
        
        assertTrue(testContent.contains(new URIImpl(folderPath + "test1.txt")));
        assertTrue(testContent.contains(new URIImpl(folderPath + "test2.txt")));
        assertTrue(testContent.contains(new URIImpl(folderPath + "test3.txt")));
        assertTrue(testContent.contains(new URIImpl(folderPath + "subfolder/")));
        assertTrue(testContent.contains(new URIImpl(folderPath + "microsoft-word-2000.doc")));
        
        assertEquals(5, testContent.size());
        URI subfolderUri = new URIImpl(folderPath + "subfolder/");
        checkStatement(subfolderUri, RDF.type, NFO.Folder, model);
        
        Set<Resource> subfolderContent = findSubjectResourceSet(model, NFO.belongsToContainer, subfolderUri);        
        
        assertTrue(subfolderContent.contains(new URIImpl(folderPath + "subfolder/test4.txt")));
        assertTrue(subfolderContent.contains(new URIImpl(folderPath + "subfolder/test5.txt")));
        assertTrue(subfolderContent.contains(new URIImpl(folderPath + "subfolder/pdf-manyauthors.pdf")));
        
        assertEquals(3, subfolderContent.size());
        
        // file names
        assertSingleValueProperty(model, testfolder, NFO.fileName, "zip-test");
        assertSingleValueProperty(model, new URIImpl(folderPath + "test1.txt"), NFO.fileName, "test1.txt");
        assertSingleValueProperty(model, new URIImpl(folderPath + "test2.txt"), NFO.fileName, "test2.txt");
        assertSingleValueProperty(model, new URIImpl(folderPath + "test3.txt"), NFO.fileName, "test3.txt");
        assertSingleValueProperty(model, new URIImpl(folderPath + "subfolder/"), NFO.fileName, "subfolder");
        assertSingleValueProperty(model, new URIImpl(folderPath + "subfolder/test4.txt"), NFO.fileName, "test4.txt");
        assertSingleValueProperty(model, new URIImpl(folderPath + "subfolder/test5.txt"), NFO.fileName, "test5.txt");
        assertSingleValueProperty(model, new URIImpl(folderPath + "subfolder/pdf-manyauthors.pdf"), NFO.fileName, "pdf-manyauthors.pdf");
        assertSingleValueProperty(model, new URIImpl(folderPath + "microsoft-word-2000.doc"), NFO.fileName, "microsoft-word-2000.doc");
    }
    
    /**
     * A basic test if the extraction actually works
     * @param model 
     * @param resourceName 
     * @param contentFileName 
     * @throws Exception
     */
    public void doBasicCompressorTest(Model model, String resourceName, String contentFileName, String prefix) throws Exception {
        String compressedFileUri = "uri:dummyuri/" + resourceName;
        String contentUri = prefix + ":uri:dummyuri/" + resourceName + "!/" + contentFileName;
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
    
    /**
     * Tests the incremental crawling
     * @param factory
     * @param subdirName
     * @param resourceName
     * @param fileExtension
     * @param numberOfEntries
     * @throws Exception
     */
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
    
    private URI toURI(File file) {
        return new URIImpl(file.toURI().toString());
    }
    
    protected RDFContainer subCrawl(String resourceName, SubCrawler subCrawler, TestBasicSubCrawlerHandler handler) throws Exception {
        InputStream stream = org.semanticdesktop.aperture.util.ResourceUtil.getInputStream(DOCS_PATH + resourceName, this.getClass());
        RDFContainer parentMetadata = new RDFContainerImpl(handler.getModel(),new URIImpl("uri:dummyuri/" + resourceName));
        subCrawler.subCrawl(null, stream, handler, null, null, null, null, parentMetadata);
        return parentMetadata;
    }
    
    /**
     * Asserts that the incremental crawling results gathered by the given {@link TestIncrementalCrawlerHandler}
     * are correct.
     * @param handler the handler to check
     * @param newObjects the desired number of new objects
     * @param changedObjects the desired number of changed objects
     * @param unchangedObjects the desired number of unchanged objects
     * @param deletedObjects the desired number of deleted objects
     */
    public void assertNewModUnmod(TestBasicSubCrawlerHandler handler, int newObjects,
            int changedObjects, int unchangedObjects, int deletedObjects) {
        assertEquals(handler.getNewObjects().size(), newObjects);
        assertEquals(handler.getChangedObjects().size(), changedObjects);
        assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
    }
    
    protected class TestBasicSubCrawlerHandler implements SubCrawlerHandler, RDFContainerFactory {
        
        protected Model model;

        protected int numberOfObjects;
        
        protected Set<String> newObjects;
        protected Set<String> changedObjects;
        protected Set<String> unchangedObjects;
        
        protected ExtractorRegistry extractorRegistry;
        
        protected MimeTypeIdentifier mimeTypeIdentifier;
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////// CONSTRUCTOR /////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        
        /**
         * Constructs the TestBasicSubCrawlerHandler
         */
        public TestBasicSubCrawlerHandler() {
            initialize(null);
        }
        
        /**
         * Constructs the TestBasicSubCrawlerHandler
         */
        public TestBasicSubCrawlerHandler(ExtractorRegistry registry){
            initialize(registry);
        }
        
        private void initialize(ExtractorRegistry registry) {
            model = RDF2Go.getModelFactory().createModel();
            model.open();
            newObjects = new HashSet<String>();
            changedObjects = new HashSet<String>();
            unchangedObjects = new HashSet<String>();
            numberOfObjects = 0;
            newObjects.clear();
            changedObjects.clear();
            unchangedObjects.clear();
            this.extractorRegistry = registry;
            this.mimeTypeIdentifier = ((registry != null) ? new MagicMimeTypeIdentifier() : null);
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
            
            if (object instanceof FileDataObject && extractorRegistry != null) {
                try {
                    process((FileDataObject)object);
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (ExtractorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            // free any resources contained by this DataObject
            object.dispose();
        }
        
        private void process(FileDataObject object) throws IOException, ExtractorException {
            URI id = object.getID();
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            InputStream contentStream = object.getContent();
            contentStream.mark(minimumArrayLength + 10); // add some for safety
            byte[] bytes = IOUtil.readBytes(contentStream, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);
            if (mimeType == null) {return;}
            contentStream.reset();
            applyExtractor(object.getID(), contentStream, mimeType, object);
        }
        
        private boolean applyExtractor(URI id, InputStream contentStream, String mimeType,
                DataObject object) throws ExtractorException {
            Set extractors = extractorRegistry.getExtractorFactories(mimeType);
            if (!extractors.isEmpty()) {
                ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                Extractor extractor = factory.get();
                extractor.extract(id, contentStream, null, mimeType, object.getMetadata());
                return true;
            }
            else {
                return false;
            }
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
            super(null);
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
}

