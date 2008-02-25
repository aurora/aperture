/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
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
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testers.DataObjectTreeModelTester;

public class TestMboxCrawler extends ApertureTestBase {

    /**
     * This tests if a crawler crawls a file and what messages have been extracted from it.
     * @throws ModelException
     */
    public void testCrawler() throws Exception {
        // create a DataSource
        RDFContainer configuration = createRDFContainer("urn:test:dummySource");
        MboxDataSource dataSource = new MboxDataSource();
        dataSource.setConfiguration(configuration);
        
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + 
            "mbox-aperture-dev", this.getClass());
        
        File file = createTempFile(stream, null);
        
        
        dataSource.setMboxPath(file.getAbsolutePath());

        // configuration.getModel().writeTo(new PrintWriter(System.out),Syntax.Trix);

        // create a Crawler for this DataSource
        MboxCrawler crawler = new MboxCrawler();
        crawler.setDataSource(dataSource);

        // setup a DataAccessorRegistry
        // DataAccessorRegistryImpl registry = new DataAccessorRegistryImpl();
        // registry.add(new FileAccessorFactory());
        // crawler.setDataAccessorRegistry(registry);

        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);

        // start Crawling
        crawler.crawl();

        Model model = crawlerHandler.getModel();
        model.writeTo(System.out);
        validate(model,true,configuration.getDescribedUri(),(ModelTester)null);
        model.close();
        configuration.getModel().close();
    }
  
    /**
     * This tests if the folder exclusion (domain boundaries) works.
     */
    public void testFolderExclusion() throws ModelException {
//        // create a DataSource
//        FileSystemDataSource dataSource = new FileSystemDataSource();
//        RDFContainer configuration = createRDFContainer("urn:test:dummySource");
//        dataSource.setConfiguration(configuration);
//        
//        dataSource.setRootFolder(tmpDir.getAbsolutePath());
//        DomainBoundaries domain = ConfigurationUtil.getDomainBoundaries(configuration);
//        domain.addExcludePattern(new SubstringPattern("subdir", SubstringCondition.CONTAINS));
//        ConfigurationUtil.setDomainBoundaries(domain, configuration);
//
//        // configuration.getModel().writeTo(new PrintWriter(System.out),Syntax.Trix);
//
//        
//        
//
//        // create a Crawler for this DataSource
//        FileSystemCrawler crawler = new FileSystemCrawler();
//        crawler.setDataSource(dataSource);
//
//        // setup a DataAccessorRegistry
//        DataAccessorRegistryImpl registry = new DataAccessorRegistryImpl();
//        registry.add(new FileAccessorFactory());
//        crawler.setDataAccessorRegistry(registry);
//
//        // setup a CrawlerHandler
//        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
//        crawler.setCrawlerHandler(crawlerHandler);
//
//        // start Crawling
//        crawler.crawl();
//
//        // inspect results
//        assertEquals(4, crawlerHandler.getObjectCount());
//
//        Model model = crawlerHandler.getModel();
//
//        // model.writeTo(new PrintWriter(System.out),Syntax.Trix);
//
//        checkStatement(toURI(tmpDir), NIE.rootElementOf, dataSource.getID(), model);
//
//        checkStatement(toURI(tmpFile1), NFO.fileName, ModelUtil.createLiteral(model, tmpFile1.getName()), model);
//        checkStatement(toURI(tmpFile2), NFO.fileName, ModelUtil.createLiteral(model, tmpFile2.getName()), model);
//
//        // This should not be found because of the domain boundaries restrictions
//        // We deliberately check for a specific property rather than doing hasStatement(URI,
//        // null, null) as the URI of the skipped file will still be part of the metadata of the
//        // containing Folder.
//        assertFalse(ModelUtil.hasStatement(model, toURI(tmpFile3), NFO.fileName, null));
//        assertFalse(ModelUtil.hasStatement(model, toURI(subDir), NFO.fileName, null));
//        validate(model,true,configuration.getDescribedUri(),new DataObjectTreeModelTester());
//        model.close();
//        configuration.getModel().close();
    }
    
    public File createTempFile(InputStream fis, File file) throws Exception {
        File outFile = null;
        if (file == null) {
            outFile = File.createTempFile("temp", ".mbox");
        }
        else {
            outFile = file;
        }
        outFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
        return outFile;
    }
    

    private URI toURI(File file) {
        return URIImpl.createURIWithoutChecking(file.toURI().toString());
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private Model model;

        private int objectCount;

        private RDFContainer lastContainer;

        public SimpleCrawlerHandler() throws ModelException {
            model = createModel();
            objectCount = 0;
        }

        public Model getModel() {
            return model;
        }

        public int getObjectCount() {
            return objectCount;
        }

        public void crawlStarted(Crawler crawler) {
        // no-op
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            assertEquals(ExitCode.COMPLETED, exitCode);
            // note: Model closed externally, not here
        }

        public void accessingObject(Crawler crawler, String url) {
        // no-op
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            RDFContainer container = new RDFContainerImpl(model, uri, true);
            lastContainer = container;
            return container;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            objectCount++;

            assertNotNull(object);
            //assertSame(lastContainer, object.getMetadata());

            object.dispose();
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
