/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
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
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * Example class that crawls a file system and puts all extracted metadata in a repository.
 */
public class ExampleFileCrawler {

    private static final Logger LOGGER = Logger.getLogger(ExampleFileCrawler.class.getName());

    public static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

    public static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    public static final String VERBOSE_OPTION = "-verbose";

    private File rootFile;

    private File repositoryFile;

    private boolean identifyingMimeType = false;

    private boolean extractingContents = false;

    private boolean verbose = false;

    public boolean isExtractingContents() {
        return extractingContents;
    }

    public boolean isIdentifyingMimeType() {
        return identifyingMimeType;
    }

    public File getRepositoryFile() {
        return repositoryFile;
    }

    public File getRootFile() {
        return rootFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

    public void setIdentifyingMimeType(boolean identifyingMimeType) {
        this.identifyingMimeType = identifyingMimeType;
    }

    public void setRepositoryFile(File repositoryFile) {
        this.repositoryFile = repositoryFile;
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void crawl() {
        if (rootFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }
        if (repositoryFile == null) {
            throw new IllegalArgumentException("repository file cannot be null");
        }

        // create a FileSystemDataSource
        FileSystemDataSource source = new FileSystemDataSource();
        URI sourceID = new URIImpl("source:testSource");
        source.setID(sourceID);
        source.setName("Example file system source");

        // configure the directory tree to crawl
        SesameRDFContainer configuration = new SesameRDFContainer(sourceID);
        ConfigurationUtil.setRootUrl(rootFile.toURI().toString(), configuration);
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new SimpleCrawlerHandler());

        // start crawling
        crawler.crawl();
    }

    public static void main(String[] args) {
        // create a new ExampleFileCrawler instance
        ExampleFileCrawler crawler = new ExampleFileCrawler();
        
        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (IDENTIFY_MIME_TYPE_OPTION.equals(arg)) {
                crawler.setIdentifyingMimeType(true);
            }
            else if (EXTRACT_CONTENTS_OPTION.equals(arg)) {
                crawler.setExtractingContents(true);
            }
            else if (VERBOSE_OPTION.equals(arg)) {
                crawler.setVerbose(true);
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                exitWithUsageMessage();
            }
            else if (crawler.getRootFile() == null) {
                crawler.setRootFile(new File(arg));
            }
            else if (crawler.getRepositoryFile() == null) {
                crawler.setRepositoryFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleFileCrawler.class.getName() + " [" + IDENTIFY_MIME_TYPE_OPTION
                + "] [" + EXTRACT_CONTENTS_OPTION + "] [" + VERBOSE_OPTION + "] rootDirectory repositoryFile");
        System.exit(-1);
    }
    
    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private MimeTypeIdentifier mimeTypeIdentifier;

        private ExtractorRegistry extractorRegistry;

        private Repository repository;

        private int nrObjects;

        public SimpleCrawlerHandler() {
            // create some identification and extraction components
            if (identifyingMimeType) {
                mimeTypeIdentifier = new MagicMimeTypeIdentifier();
            }
            if (extractingContents) {
                extractorRegistry = new DefaultExtractorRegistry();
            }

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
                // this will hurt performance but we can still continue.
                // Each add and remove will now be a separate transaction (slow).
                LOGGER.log(Level.SEVERE,
                        "Exception while setting auto-commit off, continuing in auto-commit mode", e);
            }
        }

        public void crawlStarted(Crawler crawler) {
            nrObjects = 0;
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, writer);
                repository.extractStatements(rdfWriter);
                writer.close();

                System.out.println("Crawled " + nrObjects + " files (exit code: " + exitCode + ")");
                System.out.println("Saved RDF model to " + repositoryFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void accessingObject(Crawler crawler, String url) {
            if (verbose) {
                System.out.println("Processing file " + nrObjects + ": " + url + "...");
            }
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            SesameRDFContainer container = new SesameRDFContainer(repository, uri);
            container.setContext(uri);
            return container;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            nrObjects++;

            // Commit the changes to the repository. This needs to happen before processing of the
            // FileDataObjects as that operation may one day query for certain statements (mimetypes
            // etc).
            commit();

            // process the contents on an InputStream, if available
            if (object instanceof FileDataObject) {
                try {
                    process((FileDataObject) object);
                    commit();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, "IOException while processing " + object.getID(), e);
                }
                catch (ExtractorException e) {
                    LOGGER.log(Level.WARNING, "ExtractorException while processing " + object.getID(), e);
                }
            }
            
            object.dispose();
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

        private void process(FileDataObject object) throws IOException, ExtractorException {
            // we cannot do anything when MIME type identification is disabled
            if (!identifyingMimeType) {
                return;
            }

            URI id = object.getID();

            // Create a buffer around the object's stream large enough to be able to reset the stream
            // after MIME type identification has taken place. Add some extra to the minimum array
            // length required by the MimeTypeIdentifier for safety.
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            int bufferSize = Math.max(minimumArrayLength, 8192);
            BufferedInputStream buffer = new BufferedInputStream(object.getContent(), bufferSize);
            buffer.mark(minimumArrayLength + 10); // add some for safety

            // apply the MimeTypeIdentifier
            byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);

            if (mimeType != null) {
                // add the mime type to the metadata
                RDFContainer metadata = object.getMetadata();
                metadata.put(Vocabulary.MIME_TYPE, mimeType);

                // apply an Extractor if available
                if (extractingContents) {
                    buffer.reset();

                    Set extractors = extractorRegistry.get(mimeType);
                    if (!extractors.isEmpty()) {
                        ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                        Extractor extractor = factory.get();
                        extractor.extract(id, buffer, null, mimeType, metadata);
                    }
                }
            }
        }

        private void commit() {
            try {
                if (repository.isActive()) {
                    repository.commit();
                }
            }
            catch (SailUpdateException e) {
                // don't continue when this happens
                throw new RuntimeException(e);
            }
        }

        private void printUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
        }
    }
}
