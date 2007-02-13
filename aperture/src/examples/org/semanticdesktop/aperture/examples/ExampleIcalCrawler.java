/*
 * Copyright (c) 2005 -2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.ical.IcalCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

/**
 * Example class that demostrates the usage of an IcalCrawler.
 */
public class ExampleIcalCrawler {

    private static final Logger LOGGER = Logger.getLogger(ExampleIcalCrawler.class.getName());

    private File icalFile;

    private File repositoryFile;

    public File getIcalFile() {
        return icalFile;
    }

    public void setIcalFile(File icalFile) {
        this.icalFile = icalFile;
    }

    public File getRepositoryFile() {
        return repositoryFile;
    }

    public void setRepositoryFile(File file) {
        this.repositoryFile = file;
    }

    public static void main(String[] args) throws ModelException {
        // create a new ExampleFileCrawler instance
        ExampleIcalCrawler crawler = new ExampleIcalCrawler();

        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (crawler.getIcalFile() == null) {
                crawler.setIcalFile(new File(arg));
            }
            else if (crawler.getRepositoryFile() == null) {
                crawler.setRepositoryFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getIcalFile() == null || crawler.getRepositoryFile() == null) {
            exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleIcalCrawler.class.getName()
                + " icalFilePath repositoryFilePath");
        System.exit(-1);
    }

    public void crawl() throws ModelException {
        if (icalFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        Model model = null;
        try {
            model = new RepositoryModel(false);
        }
        catch (ModelException me) {
            throw new RuntimeException(me);
        }
        RDF2GoRDFContainer configuration = new RDF2GoRDFContainer(model, URIImpl
                .createURIWithoutChecking("source:testSource"));
        ConfigurationUtil.setRootUrl(icalFile.getAbsolutePath(), configuration);
        configuration.put(ICALTZD.realBlankNodes, true);

        // create the data source
        IcalDataSource source = new IcalDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        IcalCrawler crawler = new IcalCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new SimpleCrawlerHandler());

        // start crawling
        crawler.crawl();
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        protected Model model;

        protected int nrObjects;

        public SimpleCrawlerHandler() {
            try {
                model = new RepositoryModel(false);
            }
            catch (ModelException me) {
                throw new RuntimeException(me);
            }
        }

        public void accessingObject(Crawler crawler, String url) {
        // do nothing by default
        }

        public void crawlStarted(Crawler crawler) {
            nrObjects = 0;
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                model.writeTo(writer, Syntax.RdfXml);
                writer.close();

                System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
                System.out.println("Saved RDF model to " + repositoryFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            object.dispose();
            printUnexpectedEventWarning("changed");
        }

        public void objectNotModified(Crawler crawler, String url) {
            printUnexpectedEventWarning("unmodified");
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            printUnexpectedEventWarning("removed");
        }

        public void clearStarted(Crawler crawler) {
            printUnexpectedEventWarning("clearStarted");
        }

        public void clearingObject(Crawler crawler, String url) {
            printUnexpectedEventWarning("clearingObject");
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            printUnexpectedEventWarning("clear finished");
        }

        public void objectNew(Crawler crawler, DataObject object) {
            object.dispose();
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            Model contextModel = null;
            try {
                contextModel = new RepositoryModel(uri, (Repository) model.getUnderlyingModelImplementation());
            }
            catch (ModelException me) {
                throw new RuntimeException(me);
            }
            RDF2GoRDFContainer container = new RDF2GoRDFContainer(contextModel, uri);
            return container;
        }

        protected void printUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
        }
    }
}
