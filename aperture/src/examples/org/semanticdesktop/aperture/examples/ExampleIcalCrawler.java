/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
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
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

/**
 * Example class demonstrating the usage of an IcalCrawler.
 */
public class ExampleIcalCrawler {

    private File icalFile;

    private File outputFile;

    public File getIcalFile() {
        return icalFile;
    }

    public void setIcalFile(File icalFile) {
        this.icalFile = icalFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
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
            else if (crawler.getOutputFile() == null) {
                crawler.setOutputFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getIcalFile() == null || crawler.getOutputFile() == null) {
            exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleIcalCrawler.class.getName() + " icalFile outputFile");
        System.exit(-1);
    }

    public void crawl() throws ModelException {
        if (icalFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testSource");
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

        private ModelSet modelSet;

        private int nrObjects;

        public SimpleCrawlerHandler() throws ModelException {
            // create a ModelSet that will hold the RDF Models of all crawled files and folders
            ModelFactory factory = RDF2Go.getModelFactory();
            modelSet = factory.createModelSet();
        }

        public void accessingObject(Crawler crawler, String url) {}

        public void crawlStarted(Crawler crawler) {
            nrObjects = 0;
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
            nrObjects++;
            object.dispose();
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            // note: by using ModelSet.getModel, all statements added to this Model are added to the ModelSet
            // automatically, unlike ModelFactory.createModel, which creates stand-alone models.

            Model model = modelSet.getModel(uri);
            return new RDFContainerImpl(model, uri);
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(outputFile));
                modelSet.writeTo(writer, Syntax.Trix);
                writer.close();

                System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
                System.out.println("Saved RDF model to " + outputFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            modelSet.close();
        }

        private void printUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            System.err.println("encountered unexpected event (" + event + ") with non-incremental crawler");
        }
    }
}
