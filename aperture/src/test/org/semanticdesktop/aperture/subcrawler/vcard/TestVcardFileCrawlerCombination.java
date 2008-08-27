/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import info.aduna.io.ResourceUtil;

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.TestIncrementalCrawlerHandler;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.file.FileAccessorFactory;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerRegistry;
import org.semanticdesktop.aperture.subcrawler.impl.SubCrawlerRegistryImpl;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * A test case that tests how does the vcard subcrawler works in combination with a FilesystemCrawler. i.e. if
 * it actually is able to work with an input stream returned by a crawler.
 */
public class TestVcardFileCrawlerCombination extends ApertureTestBase {

    private static final String TMP_SUBDIR = "TestVcardFileCrawlerCombination.tmpDir";

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

        tmpFile3 = File.createTempFile("file-", ".vcard", subDir);
        IOUtil.writeStream(ResourceUtil.getInputStream(DOCS_PATH + "vcard-rfc2426.vcf"), tmpFile3);

    }

    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }

    /**
     * Runs the crawler over a pre-prepared folder with some normal files and a vcard file. The desired
     * behavior is that the vcard subcrawler should be found in the registry, applied to the content stream
     * and two vcards in the file should be correctly extracted.
     * 
     * @throws ModelException
     */
    public void testCrawler() throws ModelException {
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
        subCrawlerRegistry.add(new VcardSubCrawlerFactory());
        // setup a CrawlerHandler
        TestIncrementalCrawlerHandler crawlerHandler = new TestIncrementalCrawlerHandler(subCrawlerRegistry);
        crawler.setCrawlerHandler(crawlerHandler);

        // start Crawling
        crawler.crawl();

        // inspect results
        assertNewModUnmodDel(crawlerHandler, 8, 0, 0, 0);
        assertTrue(accessData.getStoredIDs().size() == 8);

        crawler.crawl();
        // recursive touching, the file has been reported as unmodified
        assertTrue(crawlerHandler.getUnchangedObjects().contains(toURI(tmpFile3).toString()));
        assertNewModUnmodDel(crawlerHandler, 0, 0, 8, 0);
        assertTrue(accessData.getStoredIDs().size() == 8);

        // recursive removal
        safelySleep(1200); // for some sanity ,it seems that a fast server is able to run two crawls in the
        // same second
        tmpFile3.delete();
        crawler.crawl();

        // the folder has been modified, three resources have been deleted,
        assertNewModUnmodDel(crawlerHandler, 0, 1, 4, 3);
        assertTrue(accessData.getStoredIDs().size() == 5);

        Model model = crawlerHandler.getModel();

        model.close();
        configuration.getModel().close();
    }

    private URI toURI(File file) {
        return new URIImpl(file.toURI().toString());
    }
}
