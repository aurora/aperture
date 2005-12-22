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
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
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
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.imap.ImapCrawler;
import org.semanticdesktop.aperture.datasource.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * Demonstrates how to crawl an IMAP mail folder.
 * 
 * <p>
 * Warning: this implementation does not provide any form of security. This means that the provided
 * password is sent as plain text over the wire. Read the SSLNOTES.txt file delivered with the Javamail
 * library to see how to realize a secured connection or take a look at how the GUI version of the
 * ExampleImapCrawler is implemented.
 */
public class ExampleImapCrawler {

    private static final Logger LOGGER = Logger.getLogger(ExampleImapCrawler.class.getName());

    private static final String SERVER_OPTION = "-server";

    private static final String USERNAME_OPTION = "-username";

    private static final String PASSWORD_OPTION = "-password";

    private static final String FOLDER_OPTION = "-folder";

    private static final String REPOSITORY_OPTION = "-repositoryFile";

    private static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

    private static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    /* Settable properties */

    private String serverName;

    private String username;

    private String password;

    private String folder;

    private File repositoryFile;

    private boolean identifyingMimeType = false;

    private boolean extractingContents = false;

    // not settable on command-line! correct handling requires rather complex setup, dependent on Java
    // version, whether or not it's a GUI application, etc. See ImapCrawler.sessionProperties and the
    // SSLNOTES.TXT file delivered with the Javamail package.
    private boolean secureConnection = false;

    /* Observable properties - determined during crawling */

    private ImapCrawler crawler;

    private int nrObjects = 0;

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

    public File getRepositoryFile() {
        return repositoryFile;
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

    public void setRepositoryFile(File repositoryFile) {
        this.repositoryFile = repositoryFile;
    }

    public void setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void crawl() {
        if (serverName == null) {
            throw new IllegalArgumentException("serverName cannot be null");
        }
        if (folder == null) {
            throw new IllegalArgumentException("server cannot be null");
        }

        // determine the root url of the data source
        StringBuffer buffer = new StringBuffer(100);
        buffer.append("imap://");
        if (username != null) {
            buffer.append(username);
            buffer.append('@');
        }
        buffer.append(serverName);
        buffer.append('/');
        buffer.append(HttpClientUtil.formUrlEncode(folder));
        buffer.append(";TYPE=LIST");
        String rootUrl = buffer.toString();

        // create an IMAP data source
        URI sourceID = new URIImpl("urn:test:exampleimapsource");
        SesameRDFContainer config = new SesameRDFContainer(sourceID);

        ConfigurationUtil.setRootUrl(rootUrl, config);
        if (password != null) {
            ConfigurationUtil.setPassword(password, config);
        }

        if (secureConnection) {
            ConfigurationUtil.setConnectionSecurity(Vocabulary.SSL.toString(), config);
        }
        
        ImapDataSource dataSource = new ImapDataSource();
        dataSource.setID(sourceID);
        dataSource.setName("Example IMAP DataSource");
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

    public static void main(String[] args) {
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
                crawler.setUsername(HttpClientUtil.formUrlEncode(value));
            }
            else if (PASSWORD_OPTION.equals(option)) {
                crawler.setPassword(value);
            }
            else if (FOLDER_OPTION.equals(option)) {
                crawler.setFolder(value);
            }
            else if (REPOSITORY_OPTION.equals(option)) {
                crawler.setRepositoryFile(new File(value));
            }
            else if (IDENTIFY_MIME_TYPE_OPTION.equals(option)) {
                crawler.setIdentifyingMimeType(Boolean.parseBoolean(value));
            }
            else if (EXTRACT_CONTENTS_OPTION.equals(option)) {
                crawler.setExtractingContents(Boolean.parseBoolean(value));
            }
        }

        // check whether the crawler has enough information
        if (crawler.getServerName() == null || crawler.getFolder() == null
                || crawler.getRepositoryFile() == null) {
            exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        StringBuffer message = new StringBuffer(100);
        message.append("Usage: java ");
        message.append(ExampleImapCrawler.class.getName());
        append(SERVER_OPTION, "server", false, message);
        append(USERNAME_OPTION, "username", true, message);
        append(PASSWORD_OPTION, "password", true, message);
        append(FOLDER_OPTION, "folder", false, message);
        append(REPOSITORY_OPTION, "file", false, message);
        append(IDENTIFY_MIME_TYPE_OPTION, "true|false", true, message);
        append(EXTRACT_CONTENTS_OPTION, "true|false", true, message);

        System.err.println(message.toString());
        System.exit(-1);
    }

    private static void append(String option, String var, boolean optional, StringBuffer message) {
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

        private MimeTypeIdentifier mimeTypeIdentifier;

        private ExtractorRegistry extractorRegistry;

        private Repository repository;

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
            exitCode = null;
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, writer);
                repository.extractStatements(rdfWriter);
                writer.close();

                System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
                System.out.println("Saved RDF model to " + repositoryFile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ExampleImapCrawler.this.exitCode = exitCode;
        }

        public void accessingObject(Crawler crawler, String url) {
            currentURL = url;
        }

        public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
            return this;
        }

        public void objectNew(Crawler crawler, DataObject object) {
            nrObjects++;

            // Commit the changes to the repository. This needs to happen before processing of the
            // FileDataObjects as that operation will query for certain statements (mimetypes etc).
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
        }

        private void commit() {
            try {
                // commit if any changes have been made
                if (repository.isActive()) {
                    repository.commit();
                }
            }
            catch (SailUpdateException e) {
                // don't continue when this happens
                throw new RuntimeException(e);
            }
        }

        public void objectChanged(Crawler crawler, DataObject object) {
            printUnexpectedEventWarning("changed");
        }

        public void objectNotModified(Crawler crawler, String url) {
            printUnexpectedEventWarning("unmodified");
        }

        public void objectRemoved(Crawler crawler, String url) {
            printUnexpectedEventWarning("removed");
        }

        public void clearStarted(Crawler crawler) {
            printUnexpectedEventWarning("clearStarted");
        }

        public void clearingObject(Crawler crawler, String url) {
            printUnexpectedEventWarning("clearingObject");
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            printUnexpectedEventWarning("clearFinished");
        }

        public RDFContainer getRDFContainer(URI uri) {
            SesameRDFContainer container = new SesameRDFContainer(repository, uri);
            container.setContext(uri);
            return container;
        }

        private void process(FileDataObject object) throws IOException, ExtractorException {
            // some initialization
            URI id = object.getID();
            RDFContainer metadata = object.getMetadata();
            String mimeType = null;

            // fetch some constants to make sure the code remains readable
            final URI mimeTypeProperty = org.semanticdesktop.aperture.accessor.Vocabulary.MIME_TYPE;
            final URI contentMimeTypeProperty = org.semanticdesktop.aperture.accessor.Vocabulary.CONTENT_MIME_TYPE;
            final URI charsetProperty = org.semanticdesktop.aperture.accessor.Vocabulary.CHARACTER_SET;

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
                    if (metadata.getString(contentMimeTypeProperty) != null) {
                        metadata.put(contentMimeTypeProperty, mimeType);
                    }
                    else {
                        metadata.put(mimeTypeProperty, mimeType);
                    }
                }
            }

            // apply the text and metadata extractor is requested
            if (extractingContents) {
                // See if a mime type has been determined above based on the magic bytes of the stream.
                // If not, see if the mail's metadata already contains a mime type.
                if (mimeType == null) {
                    mimeType = metadata.getString(contentMimeTypeProperty);
                }
                if (mimeType == null) {
                    mimeType = metadata.getString(mimeTypeProperty);
                }

                // if we know a MIME type, try to find a matching extractor
                if (mimeType != null) {
                    Set extractors = extractorRegistry.get(mimeType);
                    if (!extractors.isEmpty()) {
                        // reset the stream to its start
                        buffer.reset();

                        // see if we know the charset
                        Charset charset = null;
                        String charsetStr = metadata.getString(charsetProperty);
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

        private void printUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
        }
    }
}
