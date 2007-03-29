/*
 * Copyright (c) 2005 - 2007 Aduna.
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
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Set;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.imap.ImapCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * Demonstrates how to crawl an IMAP mail folder.
 * 
 * <p>
 * Warning: this implementation does not provide any form of security. This means that the provided password
 * is sent as plain text over the wire. Read the SSLNOTES.txt file delivered with the Javamail library to see
 * how to realize a secure connection or take a look at how the GUI version of the ExampleImapCrawler is
 * implemented.
 */
public class ExampleImapCrawler {

    private static final String SERVER_OPTION = "-server";

    private static final String USERNAME_OPTION = "-username";

    private static final String PASSWORD_OPTION = "-password";

    private static final String FOLDER_OPTION = "-folder";

    private static final String OUTPUT_OPTION = "-outputFile";

    private static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

    private static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    // /////////////// Settable properties /////////////////

    private String serverName;

    private String username;

    private String password;

    private String folder;

    private File outputFile;

    private boolean identifyingMimeType = false;

    private boolean extractingContents = false;

    /**
     * Flag that indicates whether a secure connection should be used.
     * 
     * <p>
     * Note that this setting is not settable on the command-line. Correct handling of a secure connection
     * requires a rather complex setup, depending on the Java version used, whether or not it's a GUI
     * application, etc. See ImapCrawler.sessionProperties and the SSLNOTES.TXT file delivered with the
     * Javamail package for more information.
     * 
     * <p>
     * The GUI-based crawler makes use of this property and also ensures that all other requirements for
     * secure operation are fulfilled.
     */
    private boolean secureConnection = false;

    // /////////////// Observable properties /////////////////

    private ImapCrawler crawler;

    private int nrObjects = 0;

    private long startTime = 0L;

    private long finishTime = 0L;

    private String currentURL;

    private ExitCode exitCode;

    public boolean isExtractingContents() {
        return extractingContents;
    }

    public String getFolder() {
        return folder;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isIdentifyingMimeType() {
        return identifyingMimeType;
    }

    public String getPassword() {
        return password;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public boolean hasSecureConnection() {
        return secureConnection;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentURL() {
        return currentURL;
    }

    public ExitCode getExitCode() {
        return exitCode;
    }

    public int getNrObjects() {
        return nrObjects;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setIdentifyingMimeType(boolean identifyingMimeType) {
        this.identifyingMimeType = identifyingMimeType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void crawl() throws ModelException {
        if (serverName == null) {
            throw new IllegalArgumentException("serverName cannot be null");
        }
        if (folder == null) {
            throw new IllegalArgumentException("folder cannot be null");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("output file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer config = factory.newInstance("urn:test:exampleimapsource");

        ConfigurationUtil.setHostname(serverName, config);
        ConfigurationUtil.setBasepath(folder, config);

        if (username != null) {
            ConfigurationUtil.setUsername(username, config);
        }

        if (password != null) {
            ConfigurationUtil.setPassword(password, config);
        }

        if (secureConnection) {
            ConfigurationUtil.setConnectionSecurity(DATASOURCE.SSL.toString(), config);
        }

        // create the DataSource
        ImapDataSource dataSource = new ImapDataSource();
        dataSource.setConfiguration(config);

        // set up an IMAP crawler
        crawler = new ImapCrawler();
        crawler.setDataSource(dataSource);
        crawler.setCrawlerHandler(new SimpleCrawlerHandler());
        crawler.crawl();
    }

    public void stop() {
        ImapCrawler crawler = this.crawler;
        if (crawler != null) {
            crawler.stop();
        }
    }

    public boolean isStopRequested() {
        ImapCrawler crawler = this.crawler;
        return crawler == null ? false : crawler.isStopRequested();
    }

    public static void main(String[] args) throws ModelException {
        // create a new ExampleImapCrawler instance
        ExampleImapCrawler crawler = new ExampleImapCrawler();

        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            // fetch the option name
            String option = args[i];

            // fetch the option value
            if (i == args.length - 1) {
                System.err.println("missing value for option " + option);
                exitWithUsageMessage();
            }
            i++;
            String value = args[i];

            if (SERVER_OPTION.equals(option)) {
                crawler.setServerName(value);
            }
            else if (USERNAME_OPTION.equals(option)) {
                // crawler.setUsername(HttpClientUtil.formUrlEncode(value));
                crawler.setUsername(value);
            }
            else if (PASSWORD_OPTION.equals(option)) {
                crawler.setPassword(value);
            }
            else if (FOLDER_OPTION.equals(option)) {
                crawler.setFolder(value);
            }
            else if (OUTPUT_OPTION.equals(option)) {
                crawler.setOutputFile(new File(value));
            }
            else if (IDENTIFY_MIME_TYPE_OPTION.equals(option)) {
                crawler.setIdentifyingMimeType(Boolean.parseBoolean(value));
            }
            else if (EXTRACT_CONTENTS_OPTION.equals(option)) {
                crawler.setExtractingContents(Boolean.parseBoolean(value));
            }
        }

        // check whether the crawler has enough information
        if (crawler.getServerName() == null || crawler.getFolder() == null || crawler.getOutputFile() == null) {
            exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        StringBuilder message = new StringBuilder(100);
        message.append("Usage: java ");
        message.append(ExampleImapCrawler.class.getName());
        append(SERVER_OPTION, "server", false, message);
        append(USERNAME_OPTION, "username", true, message);
        append(PASSWORD_OPTION, "password", true, message);
        append(FOLDER_OPTION, "folder", false, message);
        append(OUTPUT_OPTION, "file", false, message);
        append(IDENTIFY_MIME_TYPE_OPTION, "true|false", true, message);
        append(EXTRACT_CONTENTS_OPTION, "true|false", true, message);

        System.err.println(message.toString());
        System.exit(-1);
    }

    private static void append(String option, String var, boolean optional, StringBuilder message) {
        message.append(' ');
        if (optional) {
            message.append('[');
        }
        message.append(option);
        message.append(" <");
        message.append(var);
        message.append('>');
        if (optional) {
            message.append(']');
        }
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        private ModelSet modelSet;

        private MimeTypeIdentifier mimeTypeIdentifier;

        private ExtractorRegistry extractorRegistry;

        public SimpleCrawlerHandler() throws ModelException {
            // create a ModelSet that will hold the RDF Models of all crawled files and folders
            ModelFactory factory = RDF2Go.getModelFactory();
            modelSet = factory.createModelSet();
            modelSet.open();

            // create some identification and extraction components
            if (identifyingMimeType) {
                mimeTypeIdentifier = new MagicMimeTypeIdentifier();
            }
            if (extractingContents) {
                extractorRegistry = new DefaultExtractorRegistry();
            }
        }

        public void crawlStarted(Crawler crawler) {
            startTime = System.currentTimeMillis();
            nrObjects = 0;
            exitCode = null;
        }

        public void accessingObject(Crawler crawler, String url) {
            currentURL = url;
        }

        public void objectNew(Crawler crawler, DataObject object) {
            nrObjects++;

            // process the contents on an InputStream, if available
            if (object instanceof FileDataObject) {
                try {
                    process((FileDataObject) object);
                }
                catch (Exception e) {
                    System.err.println("Exception while processing " + object.getID());
                    e.printStackTrace();
                }
            }

            object.dispose();
        }

        private void process(FileDataObject object) throws IOException, ExtractorException {
            // some initialization
            URI id = object.getID();
            RDFContainer metadata = object.getMetadata();
            String mimeType = null;

            // Create a buffer around the object's stream large enough to be able to reset the stream
            // after MIME type identification has taken place. Add some extra to the minimum array
            // length required by the MimeTypeIdentifier for safety.
            int minimumArrayLength = identifyingMimeType ? mimeTypeIdentifier.getMinArrayLength() : 0;
            int bufferSize = Math.max(minimumArrayLength, 8192);
            BufferedInputStream buffer = new BufferedInputStream(object.getContent(), bufferSize);
            buffer.mark(minimumArrayLength + 10); // add some for safety

            // apply the mime type identifier if requested
            if (identifyingMimeType) {
                byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);
                mimeType = mimeTypeIdentifier.identify(bytes, null, id);

                if (mimeType != null) {
                    // Add the mime type to the FileDataObject's metadata. This overwrites the current
                    // mime type if any, taking care whether to take the content mime type or the regular
                    // mime type property.
                    if (metadata.getString(DATA.contentMimeType) != null) {
                        metadata.add(DATA.contentMimeType, mimeType);
                    }
                    else {
                        metadata.add(DATA.mimeType, mimeType);
                    }
                }
            }

            // apply the text and metadata extractor is requested
            if (extractingContents) {
                // See if a mime type has been determined above based on the magic bytes of the stream.
                // If not, see if the mail's metadata already contains a mime type.
                if (mimeType == null) {
                    mimeType = metadata.getString(DATA.contentMimeType);
                }
                if (mimeType == null) {
                    mimeType = metadata.getString(DATA.mimeType);
                }

                // if we know a MIME type, try to find a matching extractor
                if (mimeType != null) {
                    Set extractors = extractorRegistry.get(mimeType);
                    if (!extractors.isEmpty()) {
                        // reset the stream to its start
                        buffer.reset();

                        // see if we know the charset
                        Charset charset = null;
                        String charsetStr = metadata.getString(DATA.characterSet);
                        if (charsetStr != null) {
                            try {
                                charset = Charset.forName(charsetStr);
                            }
                            catch (IllegalCharsetNameException e) {
                                // ignore
                            }
                        }

                        // apply the extractor
                        ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                        Extractor extractor = factory.get();
                        extractor.extract(id, buffer, charset, mimeType, metadata);
                    }
                }
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

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");

            try {
                Writer writer = new BufferedWriter(new FileWriter(outputFile));
                modelSet.writeTo(writer, Syntax.Trix);
                writer.close();
                System.out.println("Saved RDF model to " + outputFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            modelSet.close();
            
            ExampleImapCrawler.this.exitCode = exitCode;
            finishTime = System.currentTimeMillis();

            double duration = (finishTime - startTime) / 1000.0;
            System.out.println("Required time: " + duration + " sec.");
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public RDFContainer getRDFContainer(URI uri) {
            // note: by using ModelSet.getModel, all statements added to this Model are added to the ModelSet
            // automatically, unlike ModelFactory.createModel, which creates stand-alone models.

            Model model = modelSet.getModel(uri);
            model.open();
            return new RDFContainerImpl(model, uri);
        }

        private void printUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            System.err.println("encountered unexpected event (" + event + ") with non-incremental crawler");
        }
    }
}
