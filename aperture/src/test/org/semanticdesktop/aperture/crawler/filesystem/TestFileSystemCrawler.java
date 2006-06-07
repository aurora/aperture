/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.io.File;
import java.io.IOException;

import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.file.FileAccessorFactory;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class TestFileSystemCrawler extends ApertureTestBase {

    private static final String TMP_SUBDIR = "TestFileSystemCrawler.tmpDir";

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
        tmpDir.mkdir();

        // put two files in it
        tmpFile1 = File.createTempFile("file-", ".txt", tmpDir);
        IOUtil.writeString("test file 1", tmpFile1);

        tmpFile2 = File.createTempFile("file-", ".txt", tmpDir);
        IOUtil.writeString("test file 2", tmpFile2);

        tmpFile4 = File.createTempFile("file-skipme-",".txt", tmpDir);
        IOUtil.writeString("test file 4",tmpFile4);
        
        // put another folder containing another file in it
        subDir = new File(tmpDir, "dir");
        subDir.mkdir();

        tmpFile3 = File.createTempFile("file-", ".txt", subDir);
        IOUtil.writeString("test file 3", tmpFile3);
        
        
    }

    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }

    public void testCrawler() {
        // create a DataSource
        RDFContainer configuration = new SesameRDFContainer(new URIImpl("urn:test:dummySource"));
        ConfigurationUtil.setRootFolder(tmpDir.getAbsolutePath(), configuration);
        ConfigurationUtil.setMaximumDepth(1, configuration);
        DomainBoundaries domain = ConfigurationUtil.getDomainBoundaries(configuration);
        domain.addExcludePattern(new SubstringPattern("skipme",SubstringCondition.CONTAINS));
        ConfigurationUtil.setDomainBoundaries(domain,configuration);

        DataSource dataSource = new FileSystemDataSource();
        dataSource.setConfiguration(configuration);
        
        // create a Crawler for this DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(dataSource);

        // setup a DataAccessorRegistry
        DataAccessorRegistryImpl registry = new DataAccessorRegistryImpl();
        registry.add(new FileAccessorFactory());
        crawler.setDataAccessorRegistry(registry);

        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);

        // start Crawling
        crawler.crawl();

        // inspect results
        assertEquals(4, crawlerHandler.getObjectCount());

        Repository repository = crawlerHandler.getRepository();

        checkStatement(toURI(tmpDir), DATA.rootFolderOf, dataSource.getID(),repository);
        
        checkStatement(toURI(tmpFile1), DATA.name, new LiteralImpl(tmpFile1.getName()), repository);
        checkStatement(toURI(tmpFile2), DATA.name, new LiteralImpl(tmpFile2.getName()), repository);
        checkStatement(toURI(subDir), DATA.name, new LiteralImpl(subDir.getName()), repository);

        // This should not be found because of maximum depth restrictions: this file should not be
        // reached. We deliberately check for a specific property rather than doing hasStatement(URI,
        // null, null) as the URI of the skipped file will still be part of the metadata of the
        // containing Folder.
        assertFalse(repository.hasStatement(toURI(tmpFile3), DATA.name, null));
        
        //This should no be found as it is excluded by the domain boundaries
        assertFalse(repository.hasStatement(toURI(tmpFile4),DATA.name,null));
    }

    private URI toURI(File file) {
        return new URIImpl(file.toURI().toString());
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private Repository repository;

        private int objectCount;

        private SesameRDFContainer lastContainer;
        
        public SimpleCrawlerHandler() {
            // create a Repository
            repository = new Repository(new MemoryStore());

            try {
                repository.initialize();
            }
            catch (SailInitializationException e) {
                // we cannot effectively continue
                throw new RuntimeException(e);
            }

            // set auto-commit off so that all additions and deletions between two commits become a
            // single transaction
            try {
                repository.setAutoCommit(false);
            }
            catch (SailUpdateException e) {
                // we could theoretically continue (although much slower), but as this is a unit test,
                // exit anyway
                throw new RuntimeException(e);
            }

            objectCount = 0;
        }

        public Repository getRepository() {
            return repository;
        }
        
        public int getObjectCount() {
            return objectCount;
        }

        public void crawlStarted(Crawler crawler) {
            // no-op
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            assertEquals(ExitCode.COMPLETED, exitCode);
        }

        public void accessingObject(Crawler crawler, String url) {
            // no-op
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            SesameRDFContainer container = new SesameRDFContainer(repository, uri);
            container.setContext(uri);
            
            lastContainer = container;
            
            return container;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            objectCount++;

            assertNotNull(object);
            assertSame(lastContainer, object.getMetadata());

            String uri = object.getID().toString();
            if (uri.equals(subDir.toURI().toString())) {
                assertTrue(object instanceof FolderDataObject);
            }
            else if (uri.indexOf("file-") != -1) {
                assertTrue(object instanceof FileDataObject);
            }
            
            object.dispose();
            
            try {
                repository.commit();
            }
            catch (SailUpdateException e) {
                fail();
            }
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            object.dispose();
            fail();
        }

        public void objectNotModified(Crawler crawler, String url) {
            fail();
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            fail();
        }

        public void clearStarted(Crawler crawler) {
            fail();
        }

        public void clearingObject(Crawler crawler, String url) {
            fail();
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            fail();
        }
    }
}
