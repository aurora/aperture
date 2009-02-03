/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.TestIncrementalCrawlerHandler;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class TestMboxCrawler extends ApertureTestBase {

    private RDFContainer configuration = null;
    
    public void setUp() {
        configuration = createRDFContainer("urn:dummy:source");
    }
    
    public void tearDown() {
        configuration.getModel().close();
        configuration = null;
    }
    /**
     * This tests if a crawler crawls a file and what messages have been extracted from it.
     * @throws ModelException
     */
    public void testCrawler() throws Exception {
        TestIncrementalCrawlerHandler crawlerHandler = crawl("mbox-aperture-dev",null, null);
        Model model = crawlerHandler.getModel();
        assertNewModUnmodDel(crawlerHandler, 139, 0, 0, 0);
        validate(model);
        model.close();
    }
    
    public void testAddedMail() throws Exception {
        AccessData accessData = new AccessDataImpl();
        TestIncrementalCrawlerHandler handler1 = crawl("mbox-aperture-inc1",accessData, null);
        // four mails and the mailbox, everything is new
        assertNewModUnmodDel(handler1, 5, 0, 0, 0);
        TestIncrementalCrawlerHandler handler2 = crawl("mbox-aperture-inc2",accessData, handler1.getFile());
        // one new mail, the mail folder has been changed, while all other four mails are unchanged 
        assertNewModUnmodDel(handler2, 1, 1, 4, 0);
        handler1.close();
        handler2.close();
    }
    
    public void testDeletedMail() throws Exception {
        AccessData accessData = new AccessDataImpl();
        TestIncrementalCrawlerHandler handler1 = crawl("mbox-aperture-inc1",accessData, null);
        // four mails and the mailbox, everything is new
        assertNewModUnmodDel(handler1, 5, 0, 0, 0);
        handler1.close();
        TestIncrementalCrawlerHandler handler2 = crawl("mbox-aperture-inc3",accessData, handler1.getFile());
        // no new mails, the mail folder has been changed, three unchanged emails and one deleted email 
        assertNewModUnmodDel(handler2, 0, 1, 3, 1);
        handler2.close();
    }
    
    public void testModifiedMail() throws Exception {
        AccessData accessData = new AccessDataImpl();
        TestIncrementalCrawlerHandler handler1 = crawl("mbox-aperture-inc1",accessData, null);
        // four mails and the mailbox, everything is new
        assertNewModUnmodDel(handler1, 5, 0, 0, 0);
        handler1.close();
        TestIncrementalCrawlerHandler handler2 = crawl("mbox-aperture-inc4",accessData, handler1.getFile());
        // the crawler doesn't detect changes in emails, the email that has been changed is reported
        // as a new one, while the old one has been deleted
        // one new, the mailbox has been modified, 3 unchanged and 1 deleted
        assertNewModUnmodDel(handler2, 1, 1, 3, 1);
        handler2.close();
    }
    
    public void testMaximumSize() throws Exception {
        TestIncrementalCrawlerHandler handler1 = crawl("mbox-testfolder",null, null);
        // no size restriction, it should find the mailbox, two emails and two attachments
        assertNewModUnmodDel(handler1, 5, 0, 0, 0);
        TestIncrementalCrawlerHandler handler2 = crawl("mbox-testfolder",null, null, 25000);
        // this size restriction should cut out the bigger attachment, together with the email but not the smaller one
        // this behavior is due to the fact that part.getSize() in javamail returns the size of the
        // entire part, together with the content, that's the way it is...
        assertNewModUnmodDel(handler2, 3, 0, 0, 0);
        TestIncrementalCrawlerHandler handler3 = crawl("mbox-testfolder",null, null, 20);
        // only the mailbox is returned, all other four dataobjects should be filtered out
        assertNewModUnmodDel(handler3, 1, 0, 0, 0);
        handler1.close();
        handler2.close();
        handler3.close();
    }
    
    private TestIncrementalCrawlerHandler crawl(String fileName, AccessData data, File oldTempFile) throws Exception {
        return crawl(fileName, data, oldTempFile, -1);
    }
    
    private TestIncrementalCrawlerHandler crawl(String fileName, AccessData data, File oldTempFile, int maxSize) throws Exception {
        MboxDataSource dataSource = new MboxDataSource();
        dataSource.setConfiguration(configuration);
        
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + fileName, this.getClass());
        
        File newTempFile = createTempFile(stream, oldTempFile);
        
        dataSource.setMboxPath(newTempFile.getAbsolutePath());
        if (maxSize > 0) {
            dataSource.setMaximumSize((long)maxSize);
        }

        // create a Crawler for this DataSource
        MboxCrawler crawler = new MboxCrawler();
        crawler.setDataSource(dataSource);
        crawler.setAccessData(data);
        // setup a CrawlerHandler
        TestIncrementalCrawlerHandler crawlerHandler = new TestIncrementalCrawlerHandler();
        crawlerHandler.setFile(newTempFile);
        crawler.setCrawlerHandler(crawlerHandler);

        // start Crawling
        crawler.crawl();
        return crawlerHandler;
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
}
