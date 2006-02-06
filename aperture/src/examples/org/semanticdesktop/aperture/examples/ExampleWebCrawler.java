/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

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
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.web.WebCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.RegExpPattern;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.datasource.web.WebDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.hypertext.linkextractor.impl.DefaultLinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

/**
 * Example class that crawls a web site or locally stored hypertext graph and puts all extracted metadata
 * in a repository.
 * 
 * <p>
 * The include and exclude pattern options use Java regular expressions. When no include and exclude
 * patterns are specified, then the domain is automatically bounded to those URLs that have the same host
 * and path name as the start URL.
 * 
 * <p>
 * Unlike the other example crawler classes, this class has no switch for setting on MIME type detection.
 * This is because MIME type detection is already a crucial part of the WebCrawler's operation (needed
 * for determining an appropriate LinkExtractor), there is no need to redo it.
 */
public class ExampleWebCrawler {

    private static final Logger LOGGER = Logger.getLogger(ExampleWebCrawler.class.getName());

    public static final String DEPTH_OPTION = "-depth";

    public static final String INCLUDE_OPTION = "-include";

    public static final String EXCLUDE_OPTION = "-exclude";

    public static final String INCLUDE_EMBEDDED_RESOURCES_OPTION = "-includeEmbeddedResources";

    public static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    public static final String VERBOSE_OPTION = "-verbose";

    private String startUrl;

    private File repositoryFile;

    private int depth = -1;

    private DomainBoundaries boundaries;

    private boolean includeEmbeddedResources = false;

    private boolean extractingContents = false;

    private boolean verbose = false;

    public DomainBoundaries getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(DomainBoundaries boundaries) {
        this.boundaries = boundaries;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isExtractingContents() {
        return extractingContents;
    }

    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

    public boolean isIncludeEmbeddedResources() {
        return includeEmbeddedResources;
    }

    public void setIncludeEmbeddedResources(boolean includeEmbeddedResources) {
        this.includeEmbeddedResources = includeEmbeddedResources;
    }

    public File getRepositoryFile() {
        return repositoryFile;
    }

    public void setRepositoryFile(File repositoryFile) {
        this.repositoryFile = repositoryFile;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void crawl() {
        if (startUrl == null) {
            throw new IllegalArgumentException("start URL cannot be null");
        }
        if (repositoryFile == null) {
            throw new IllegalArgumentException("repository file cannot be null");
        }

        // create a WebDataSource
        WebDataSource source = new WebDataSource();
        URI sourceID = new URIImpl("source:testSource");
        source.setID(sourceID);
        source.setName("Example web source");

        // configure the URL to crawl
        SesameRDFContainer configuration = new SesameRDFContainer(sourceID);

        ConfigurationUtil.setRootUrl(startUrl, configuration);
        ConfigurationUtil.setIncludeEmbeddedResources(includeEmbeddedResources, configuration);
        if (depth >= 0) {
            ConfigurationUtil.setMaximumDepth(depth, configuration);
        }
        if (boundaries != null) {
            ConfigurationUtil.setDomainBoundaries(boundaries, configuration);
        }

        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        WebCrawler crawler = new WebCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setMimeTypeIdentifier(new MagicMimeTypeIdentifier());
        crawler.setLinkExtractorRegistry(new DefaultLinkExtractorRegistry());
        crawler.setCrawlerHandler(new SimpleCrawlerHandler());

        // start crawling
        crawler.crawl();
    }

    public static void main(String[] args) {
        // create a new ExampleWebCrawler instance
        ExampleWebCrawler crawler = new ExampleWebCrawler();

        // parse the command line options
        parseArgs(args, crawler);

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void parseArgs(String[] args, ExampleWebCrawler crawler) {
        // create an empty DomainBoundaries
        DomainBoundaries boundaries = new DomainBoundaries();

        // parse the command-line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg = i < args.length - 1 ? args[i + 1] : null;

            if (EXTRACT_CONTENTS_OPTION.equals(arg)) {
                crawler.setExtractingContents(true);
            }
            else if (INCLUDE_EMBEDDED_RESOURCES_OPTION.equals(arg)) {
                crawler.setIncludeEmbeddedResources(true);
            }
            else if (VERBOSE_OPTION.equals(arg)) {
                crawler.setVerbose(true);
            }
            else if (DEPTH_OPTION.equals(arg)) {
                try {
                    if (nextArg == null) {
                        System.err.println("missing value for option " + DEPTH_OPTION);
                        exitWithUsageMessage();
                    }
                    else {
                    	i++;
                        crawler.setDepth(Integer.parseInt(nextArg));
                    }
                }
                catch (NumberFormatException e) {
                    System.err.println("illegal depth: " + nextArg);
                    exitWithUsageMessage();
                }
            }
            else if (INCLUDE_OPTION.endsWith(arg)) {
                if (nextArg == null) {
                    System.err.println("missing value for option " + INCLUDE_OPTION);
                    exitWithUsageMessage();
                }
                else {
                    try {
                    	i++;
                        boundaries.addIncludePattern(new RegExpPattern(nextArg));
                    }
                    catch (PatternSyntaxException e) {
                        System.err.println("illegal regular expression: " + nextArg);
                        exitWithUsageMessage();
                    }
                }
            }
            else if (EXCLUDE_OPTION.endsWith(arg)) {
                if (nextArg == null) {
                    System.err.println("missing value for option " + EXCLUDE_OPTION);
                    exitWithUsageMessage();
                }
                else {
                    try {
                    	i++;
                        boundaries.addExcludePattern(new RegExpPattern(nextArg));
                    }
                    catch (PatternSyntaxException e) {
                        System.err.println("illegal regular expression: " + nextArg);
                        exitWithUsageMessage();
                    }
                }
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                exitWithUsageMessage();
            }
            else if (crawler.getStartUrl() == null) {
                crawler.setStartUrl(arg);
            }
            else if (crawler.getRepositoryFile() == null) {
                crawler.setRepositoryFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // make sure that the required properties have been set
        if (crawler.getStartUrl() == null) {
            System.err.println("Missing start URL");
            exitWithUsageMessage();
        }
        if (crawler.getRepositoryFile() == null) {
            System.err.println("Missing repository file");
            exitWithUsageMessage();
        }

        // if the domain boundaries is still empty, initialize it with the start URL's path to prevent
        // accidental infinite crawling
        if (boundaries.getIncludePatterns().isEmpty() && boundaries.getExcludePatterns().isEmpty()) {
            String includePath = crawler.getStartUrl();

            if (includePath.startsWith("http") || includePath.startsWith("https")
                    || includePath.startsWith("file:")) {
                try {
                    URL url = new URL(includePath);

                    String path = url.getPath();
                    int lastPathSep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
                    if (lastPathSep >= 0) {
                        path = path.substring(0, lastPathSep + 1);
                    }

                    includePath = new URL(url, path).toExternalForm();
                }
                catch (MalformedURLException e) {
                    System.err.println("invalid URL: " + includePath);
                    exitWithUsageMessage();
                }
            }

            boundaries.addIncludePattern(new SubstringPattern(includePath, SubstringCondition.STARTS_WITH));
        }

        // now set it on the crawler
        crawler.setBoundaries(boundaries);
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleWebCrawler.class.getName() + " startURL repositoryFile ["
                + DEPTH_OPTION + " depth] [" + INCLUDE_OPTION + " includePattern] [" + EXCLUDE_OPTION
                + " exludePattern] [" + INCLUDE_EMBEDDED_RESOURCES_OPTION + "] [" + EXTRACT_CONTENTS_OPTION
                + "] [" + VERBOSE_OPTION + "]");
        System.exit(-1);
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private ExtractorRegistry extractorRegistry;

        private Repository repository;

        private int nrUrls;

        public SimpleCrawlerHandler() {
            // create some identification and extraction components
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
            nrUrls = 0;
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, writer);
                repository.export(rdfWriter);
                writer.close();

                System.out.println("Crawled " + nrUrls + " URLs (exit code: " + exitCode + ")");
                System.out.println("Saved RDF model to " + repositoryFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void accessingObject(Crawler crawler, String url) {
            if (verbose) {
                System.out.println("Processing URL " + ++nrUrls + ": " + url + "...");
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
            URI id = object.getID();
            String mimeType = object.getMetadata().getString(AccessVocabulary.MIME_TYPE);
            if (mimeType != null) {
                if (extractingContents) {
                    Set extractors = extractorRegistry.get(mimeType);
                    if (!extractors.isEmpty()) {
                        ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                        Extractor extractor = factory.get();
                        extractor.extract(id, object.getContent(), null, mimeType, object.getMetadata());
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
