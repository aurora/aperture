/*

 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.

 * All rights reserved.

 * 

 * Licensed under the Academic Free License version 3.0.

 */

package org.semanticdesktop.aperture.examples;



import java.io.BufferedWriter;

import java.io.File;

import java.io.FileWriter;

import java.io.Writer;

import java.util.logging.Level;

import java.util.logging.Logger;



import org.openrdf.model.URI;

import org.openrdf.model.impl.URIImpl;

import org.openrdf.rio.RDFFormat;

import org.openrdf.rio.RDFWriter;

import org.openrdf.rio.Rio;

import org.openrdf.sesame.repository.Repository;

import org.openrdf.sesame.sail.SailInitializationException;

import org.openrdf.sesame.sail.SailUpdateException;

import org.openrdf.sesame.sailimpl.memory.MemoryStore;

import org.semanticdesktop.aperture.accessor.DataObject;

import org.semanticdesktop.aperture.accessor.RDFContainerFactory;

import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;

import org.semanticdesktop.aperture.crawler.Crawler;

import org.semanticdesktop.aperture.crawler.CrawlerHandler;

import org.semanticdesktop.aperture.crawler.ExitCode;

import org.semanticdesktop.aperture.crawler.ical.IcalCrawler;

import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;

import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;

import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;

import org.semanticdesktop.aperture.rdf.RDFContainer;

import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

import org.semanticdesktop.aperture.vocabulary.ICALTZD;



/**

 * Example class that demostrates the usage of an IcalCrawler.

 */

public class ExampleIcalCrawler {



    private static final Logger LOGGER = Logger

            .getLogger(ExampleIcalCrawler.class.getName());



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



    public static void main(String[] args) {

        // create a new ExampleFileCrawler instance

        ExampleIcalCrawler crawler = new ExampleIcalCrawler();



        // parse the command line options

        for (int i = 0; i < args.length; i++) {

            String arg = args[i];

            if (crawler.getIcalFile() == null) {

                crawler.setIcalFile(new File(arg));

            } else if (crawler.getRepositoryFile() == null) {

                crawler.setRepositoryFile(new File(arg));

            } else {

                exitWithUsageMessage();

            }

        }



        // check that all required fields are available

        if (crawler.getIcalFile() == null

                || crawler.getRepositoryFile() == null) {

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



    public void crawl() {

        if (icalFile == null) {

            throw new IllegalArgumentException("root file cannot be null");

        }



        // create a data source configuration

        SesameRDFContainer configuration = 

                new SesameRDFContainer(new URIImpl("source:testSource"));

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



    private class SimpleCrawlerHandler implements CrawlerHandler,

            RDFContainerFactory {



        private Repository repository;



        private int nrObjects;



        public SimpleCrawlerHandler() {

            // create a Repository

            repository = new Repository(new MemoryStore());



            try {

                repository.initialize();

            } catch (SailInitializationException e) {

                // we cannot effectively continue

                throw new RuntimeException(e);

            }



            // set auto-commit off so that all additions and deletions between

            // two commits become a single transaction

            try {

                repository.setAutoCommit(false);

            } catch (SailUpdateException e) {

                // this will hurt performance but we can still continue.

                // Each add and remove will now be a separate transaction

                // (slow).

                LOGGER.log(Level.SEVERE,

                        "Exception while setting auto-commit off, " +

                        "continuing in auto-commit mode", e);

            }

        }



        public void crawlStarted(Crawler crawler) {

            nrObjects = 0;

        }



        public void crawlStopped(Crawler crawler, ExitCode exitCode) {

            try {

                Writer writer = new BufferedWriter(

                        new FileWriter(repositoryFile.getAbsolutePath()));

                RDFWriter rdfWriter = 

                        Rio.createWriter(RDFFormat.RDFXML, writer);

                repository.export(rdfWriter);

                writer.close();

                System.out.println("Crawled " + nrObjects

                        + " components (exit code: " + exitCode + ")");

                System.out.println("Saved RDF model to " + repositoryFile);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }



        public void accessingObject(Crawler crawler, String url) {

            System.out.println("Processing component " + nrObjects + ": " + url

                    + "...");

        }



        public RDFContainerFactory getRDFContainerFactory(Crawler crawler,

                String url) {

            return this;

        }



        public RDFContainer getRDFContainer(URI uri) {

            SesameRDFContainer container = 

                    new SesameRDFContainer(repository,uri);

            container.setContext(uri);

            return container;

        }



        public void objectNew(Crawler dataCrawler, DataObject object) {

            nrObjects++;



            LOGGER.fine("New object: '" + object.getID() + "'");



            // commit all generated statements

            try {

                repository.commit();

            } catch (SailUpdateException e) {

                // don't continue when this happens

                throw new RuntimeException(e);

            }



            object.dispose();

        }



        public void objectChanged(Crawler dataCrawler, DataObject object) {

            object.dispose();

            printUnexpectedEventWarning("Object '" + object.getID()

                    + "' changed");

        }



        public void objectNotModified(Crawler crawler, String url) {

            printUnexpectedEventWarning("Object '" + url + "' unmodified");

        }



        public void objectRemoved(Crawler dataCrawler, String url) {

            printUnexpectedEventWarning("Object '" + url + "' removed");

        }



        public void clearStarted(Crawler crawler) {

            printUnexpectedEventWarning("Clear Started");

        }



        public void clearingObject(Crawler crawler, String url) {

            printUnexpectedEventWarning("Clearing Object '" + url + "'");

        }



        public void clearFinished(Crawler crawler, ExitCode exitCode) {

            printUnexpectedEventWarning("Clear finished, exitCode: "

                    + exitCode.toString());

        }



        private void printUnexpectedEventWarning(String event) {

            // as we don't keep track of access data in this example code, some

            // events should never occur

            LOGGER.warning("encountered unexpected event (" + event + ") " 

                    + "with non-incremental crawler");

        }

    }

}

