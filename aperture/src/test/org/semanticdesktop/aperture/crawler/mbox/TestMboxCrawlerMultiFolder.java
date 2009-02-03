/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.io.File;
import java.io.IOException;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.TestIncrementalCrawlerHandler;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.vocabulary.NMO;

public class TestMboxCrawlerMultiFolder extends ApertureTestBase {

    private RDFContainer configuration = null;
    private MboxDataSource dataSource = null;
    
    private File tempMailFolder;
    private File tempListFolder;
    
    private static final String TMP_MAILFOLDER = "aperture-temp-mail-1";
    private static final String TMP_LIST_FOLDER = "lists.sbd";
    
    private static final String MAIL_SRC = DOCS_PATH + "mailtest";
    private static final String LISTS_SRC = MAIL_SRC + "/lists_sbd";
    
    public void setUp() throws IOException {
        // create a temporary folder for mails
        // unfortunately there is no File.createTempDir
        configuration = createRDFContainer("urn:dummy:source");
        tempMailFolder = new File(System.getProperty("java.io.tmpdir"), TMP_MAILFOLDER).getCanonicalFile();
        FileUtil.deltree(tempMailFolder);
        assertTrue(tempMailFolder.mkdir());
        IOUtil.writeStream(
            ResourceUtil.getInputStream(MAIL_SRC + "/lists", this.getClass()), 
            new File(tempMailFolder,"lists"));
        IOUtil.writeStream(
            ResourceUtil.getInputStream(MAIL_SRC + "/tematyczne", this.getClass()), 
            new File(tempMailFolder,"tematyczne"));
        IOUtil.writeStream(
            ResourceUtil.getInputStream(MAIL_SRC + "/testfolder", this.getClass()), 
            new File(tempMailFolder,"testfolder"));
        
        tempListFolder = new File(tempMailFolder, TMP_LIST_FOLDER).getCanonicalFile();
        assertTrue(tempListFolder.mkdir());
        IOUtil.writeStream(
            ResourceUtil.getInputStream(LISTS_SRC + "/pdfbox", this.getClass()), 
            new File(tempListFolder,"pdfbox"));
        IOUtil.writeStream(
            ResourceUtil.getInputStream(LISTS_SRC + "/protege-users", this.getClass()), 
            new File(tempListFolder,"protege-users"));
        IOUtil.writeStream(
            ResourceUtil.getInputStream(LISTS_SRC + "/sourceforge", this.getClass()), 
            new File(tempListFolder,"sourceforge"));
        IOUtil.writeStream(
            ResourceUtil.getInputStream(LISTS_SRC + "/www-rdf-calendar", this.getClass()), 
            new File(tempListFolder,"www-rdf-calendar"));
        
        dataSource = new MboxDataSource();
        dataSource.setConfiguration(configuration);
        dataSource.setMboxPath(tempMailFolder.getAbsolutePath());
        
    }
    
    public void tearDown() {
        FileUtil.deltree(tempMailFolder);
        configuration.getModel().close();
        configuration = null;
    }

    public void testMaximumDepth() throws Exception {
        dataSource.setMaximumDepth(2);
        TestIncrementalCrawlerHandler handler = crawl(null);
        Model model = handler.getModel();
        
        // let's check if the output contains anything from the three subfolders of lists.sbd
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/pdfbox/.*\"))}");
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/protege-users/.*\"))}");
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/sourceforge/.*\"))}");
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/www-rdf-calendar/.*\"))}");
        
        validate(model);
        handler.close();
        dataSource.setMaximumDepth(1);
        handler = crawl(null);
        model = handler.getModel();
        
        // right now, nothing from those folders should be present
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/pdfbox/.*\"))}");
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/protege-users/.*\"))}");
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/sourceforge/.*\"))}");
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/www-rdf-calendar/.*\"))}");
        handler.close();
    }
    
    public void testDomainBoundaries() throws Exception {
        DomainBoundaries boundaries = dataSource.getDomainBoundaries();
        boundaries.addExcludePattern(
            new SubstringPattern("/lists.sbd/sourceforge/", SubstringCondition.CONTAINS));
        boundaries.addExcludePattern(
            new SubstringPattern("/lists.sbd/protege-users/", SubstringCondition.CONTAINS));
        dataSource.setDomainBoundaries(boundaries);
        TestIncrementalCrawlerHandler handler = crawl(null);
        Model model = handler.getModel();
        
        // these two subfolders should be present
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/pdfbox/.*\"))}");
        assertSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/www-rdf-calendar/.*\"))}");
        
        // and these two should be excluded
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/protege-users/.*\"))}");
        assertNoResultSparqlQuery(model, 
            "SELECT ?x WHERE " +
            "{     ?x " + RDF.type.toSPARQL() + " " + NMO.Email.toSPARQL() + " . " +
            "      FILTER(regex(str(?x),\".*/lists.sbd/sourceforge/.*\"))}");
        
        validate(model);
        handler.close();
    }
    
    private TestIncrementalCrawlerHandler crawl(AccessData data) throws Exception {
        // create a Crawler for this DataSource
        MboxCrawler crawler = new MboxCrawler();
        crawler.setDataSource(dataSource);
        // setup a CrawlerHandler
        TestIncrementalCrawlerHandler crawlerHandler = new TestIncrementalCrawlerHandler();
        crawlerHandler.setFile(tempMailFolder);
        crawler.setCrawlerHandler(crawlerHandler);
        // start Crawling
        crawler.crawl();
        return crawlerHandler;
        
    }
    
}
