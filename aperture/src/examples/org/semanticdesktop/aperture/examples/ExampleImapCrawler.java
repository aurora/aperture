/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.pdfbox.util.operator.SetHorizontalTextScaling;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.imap.ImapCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.imap.IMAPDS;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.examples.handler.IMAPUrisValidatingCrawlerHandler;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
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
public class ExampleImapCrawler extends AbstractExampleCrawler {

    private static final String SERVER_OPTION = "-server";

    private static final String USERNAME_OPTION = "-username";

    private static final String PASSWORD_OPTION = "-password";

    private static final String FOLDER_OPTION = "-folder";
    
    private static final String TEST_URIS_OPTION = "-testuris";

    // /////////////// Settable properties /////////////////

    private String serverName;

    private String username;

    private String password;

    private String folder;
    
    private boolean testingUris;

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

    private ImapCrawler crawler;

    public String getFolder() {
        return folder;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasSecureConnection() {
        return secureConnection;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentURL() {
        return getHandler() != null ? getHandler().getCurrentURL() : null;
    }

    public ExitCode getExitCode() {
        return getHandler() != null ? getHandler().getExitCode() : null;
    }

    public int getNrObjects() {
        return getHandler() != null ? getHandler().getNrObjects() : -1 ;
    }

    public long getStartTime() {
        return getHandler() != null ? getHandler().getStartTime() : 0L ;
    }

    public long getFinishTime() {
        return getHandler() != null ? getHandler().getFinishTime() : 0L ;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setTestingUris(boolean testuris) {
        this.testingUris = testuris;
    }

    public void crawl() throws ModelException {
        if (serverName == null) {
            throw new IllegalArgumentException("serverName cannot be null");
        }
        if (folder == null) {
            throw new IllegalArgumentException("folder cannot be null");
        }
        
        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer config = factory.newInstance("urn:test:exampleimapsource");

        ImapDataSource dataSource = new ImapDataSource();
        dataSource.setConfiguration(config);
        
        dataSource.setHostname(serverName);
        dataSource.setBasepath(folder);

        if (username != null) {
            dataSource.setUsername(username);
        }

        if (password != null) {
            dataSource.setPassword(password);
        }

        if (secureConnection) {
            dataSource.setConnectionSecurity(ImapDataSource.ConnectionSecurity.SSL);
        }

        // create the DataSource
        

        // set up an IMAP crawler
        crawler = new ImapCrawler();
        crawler.setDataSource(dataSource);
        crawler.setCrawlerHandler(getHandler());
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

    public static void main(String[] args) throws Exception {
        // create a new ExampleImapCrawler instance
        ExampleImapCrawler crawler = new ExampleImapCrawler();

        List<String> remainingOptions = crawler.processCommonOptions(args);
        
        // parse the command line options
        Iterator<String> iterator = remainingOptions.iterator();
        while (iterator.hasNext()) {
            // fetch the option name
            String option = iterator.next();

            if (TEST_URIS_OPTION.equals(option)) {
                crawler.setHandler(
                    new IMAPUrisValidatingCrawlerHandler(
                        crawler.isIdentifyingMimeType(),
                        crawler.isExtractingContents(),
                        crawler.isVerbose(),
                        crawler.getOutputFile()));
                continue;
            }
            
            // fetch the option value
            if (!iterator.hasNext()) {
                System.err.println("missing value for option " + option);
                crawler.exitWithUsageMessage();
            }
            
            String value = iterator.next();

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
        }

        // check whether the crawler has enough information
        if (crawler.getServerName() == null || crawler.getFolder() == null) {
            crawler.exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    @Override
    protected String getSpecificExplanationPart() {
        StringBuilder builder = new StringBuilder();
        builder.append("  " + SERVER_OPTION + " - specifies the hostname of the server\n");
        builder.append("  " + USERNAME_OPTION + " - the username\n");
        builder.append("  " + PASSWORD_OPTION + " - the password\n");
        builder.append("  " + FOLDER_OPTION + " - the folder on the server where the crawling should start\n");
        builder.append("  " + TEST_URIS_OPTION + " - check if the uris generated by the crawler are compliant " +
        		       "with RFC 2192\n" +
                       "    this setting overrides " + AbstractExampleCrawler.VALIDATE_OPTION + " and " + 
                       AbstractExampleCrawler.PERFORMANCE_OPTION);
        return builder.toString();
    }

    @Override
    protected String getSpecificSyntaxPart() {
        StringBuilder builder = new StringBuilder();
        append(SERVER_OPTION, "server", false, builder);
        append(USERNAME_OPTION, "username", true, builder);
        append(PASSWORD_OPTION, "password", true, builder);
        append(TEST_URIS_OPTION, null, true, builder);
        append(FOLDER_OPTION, "folder", false, builder);
        return builder.toString();
    }
    
    private void append(String option, String var, boolean optional, StringBuilder builder) {
        builder.append(' ');
        if (optional) {
            builder.append('[');
        }
        builder.append(option);
        if (var != null) {
            builder.append(" <");
            builder.append(var);
            builder.append('>');
        }
        if (optional) {
            builder.append(']');
        }
    }
}
