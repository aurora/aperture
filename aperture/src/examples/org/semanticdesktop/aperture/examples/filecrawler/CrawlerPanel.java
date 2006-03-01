/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filecrawler;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

public class CrawlerPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(CrawlerPanel.class.getName());
    
    private ConfigurationPanel configurationPanel = null;

    private JLabel statusLabel = null;

    private JButton crawlButton = null;

    private JButton stopButton = null;

    private JButton exitButton = null;

    private FileSystemCrawler crawler;

    /**
     * This is the default constructor
     */
    public CrawlerPanel() {
        super();
        initialize();
        installListeners();
        updateEnabledState();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 2;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints4.weighty = 1.0D;
        gridBagConstraints4.gridy = 2;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 80);
        gridBagConstraints3.weighty = 1.0D;
        gridBagConstraints3.gridy = 2;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 20);
        gridBagConstraints2.weighty = 1.0D;
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(20, 0, 0, 0);
        gridBagConstraints1.gridwidth = 3;
        gridBagConstraints1.gridy = 1;
        statusLabel = new JLabel();
        statusLabel.setText("Status: inactive");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(540, 378);
        this.add(getConfigurationPanel(), gridBagConstraints);
        this.add(statusLabel, gridBagConstraints1);
        this.add(getCrawlButton(), gridBagConstraints2);
        this.add(getStopButton(), gridBagConstraints3);
        this.add(getExitButton(), gridBagConstraints4);
    }

    private void installListeners() {
        DocumentListener listener = new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateEnabledState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateEnabledState();
            }

            public void changedUpdate(DocumentEvent e) {
                updateEnabledState();
            }
        };

        getConfigurationPanel().getInputPanel().getFolderField().getDocument().addDocumentListener(listener);
        getConfigurationPanel().getOutputPanel().getFileField().getDocument().addDocumentListener(listener);
    }

    private void updateEnabledState() {
        InputPanel inputPanel = getConfigurationPanel().getInputPanel();
        OutputPanel outputPanel = getConfigurationPanel().getOutputPanel();
        crawlButton.setEnabled(hasText(inputPanel.getFolderField()) && hasText(outputPanel.getFileField()));
    }

    private boolean hasText(JTextField textField) {
        return !textField.getText().equals("");
    }

    /**
     * This method initializes configurationPanel
     * 
     * @return org.semanticdesktop.aperture.examples.filecrawler.ConfigurationPanel
     */
    private ConfigurationPanel getConfigurationPanel() {
        if (configurationPanel == null) {
            configurationPanel = new ConfigurationPanel();
        }
        return configurationPanel;
    }

    /**
     * This method initializes crawlButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCrawlButton() {
        if (crawlButton == null) {
            crawlButton = new JButton();
            crawlButton.setText("Crawl");
            crawlButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    crawl();
                }
            });
        }
        return crawlButton;
    }

    /**
     * This method initializes stopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setText("Stop");
            stopButton.setEnabled(false);
            stopButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Crawler crawler = CrawlerPanel.this.crawler;
                    if (crawler != null) {
                        crawler.stop();
                    }
                }
            });
        }
        return stopButton;
    }

    /**
     * This method initializes exitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return exitButton;
    }

    private void crawl() {
        // change enabled states of some buttons
        getCrawlButton().setEnabled(false);
        getStopButton().setEnabled(true);
        getExitButton().setEnabled(false);

        // create a data source configuration
        InputPanel inputPanel = getConfigurationPanel().getInputPanel();
        File rootFile = new File(inputPanel.getFolderField().getText());
        SesameRDFContainer configuration = new SesameRDFContainer(new URIImpl("source:testSource"));
        ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        
        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);
        
        boolean identifyMimeType = inputPanel.getDetermineMimeTypeBox().isSelected();
        boolean extractContents = identifyMimeType && inputPanel.getExtractContentsBox().isSelected();
        crawlerHandler.setIdentifyMimeType(identifyMimeType);
        crawlerHandler.setExtractContents(extractContents);
        
        // start a Thread that performs the crawling
        Thread thread = new Thread() {

            public void run() {
                crawler.crawl();
                crawler = null;
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

        // the Repository to store all metadata in
        private Repository repository;

        // the number of objects accessed thus far
        private int nrObjects;

        // the object responsible for determining a file's MIME type
        private MimeTypeIdentifier mimeTypeIdentifier;

        // the registry holding the ExtractorFactories
        private ExtractorRegistry extractorRegistry;

        // switch to enable MIME type identification
        private boolean identifyMimeType;

        // switch to enable full-text and metadata extraction, implies MIME type identification
        private boolean extractContents;

        public SimpleCrawlerHandler() {
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

            // create components for processing file contents
            mimeTypeIdentifier = new MagicMimeTypeIdentifier();
            extractorRegistry = new DefaultExtractorRegistry();
        }

        public void setIdentifyMimeType(boolean identifyMimeType) {
            this.identifyMimeType = identifyMimeType;
        }

        public void setExtractContents(boolean extractContents) {
            this.extractContents = extractContents;
        }

        public void crawlStarted(Crawler crawler) {
            displayMessage("Crawling started...");
            nrObjects = 0;
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            displayMessage("Crawling completed, saving results...");

            try {
                String text = getConfigurationPanel().getOutputPanel().getFileField().getText();
                File repositoryFile = new File(text);
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, writer);
                repository.export(rdfWriter);
                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(CrawlerPanel.this,
                        "Exception while saving RDF file, see stderr.", "Exception",
                        JOptionPane.ERROR_MESSAGE);
            }

            displayMessage("Crawled " + nrObjects + " files (exit code: " + exitCode + ")");
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    getCrawlButton().setEnabled(true);
                    getStopButton().setEnabled(false);
                    getExitButton().setEnabled(true);
                }
            });
        }

        public void accessingObject(Crawler crawler, String url) {
            nrObjects++;
            displayMessage("Processing file " + nrObjects + ": " + url + "...");
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
            if (object instanceof FileDataObject) {
                process((FileDataObject) object);
            }

            try {
                repository.commit();
            }
            catch (SailUpdateException e) {
                // don't continue when this happens
                throw new RuntimeException(e);
            }
            
            object.dispose();
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            displayUnexpectedEventWarning("changed");
            object.dispose();
        }

        public void objectNotModified(Crawler crawler, String url) {
            displayUnexpectedEventWarning("unmodified");
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            displayUnexpectedEventWarning("removed");
        }

        private void process(FileDataObject object) {
            if (!identifyMimeType) {
                return;
            }

            URI id = object.getID();

            try {
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
                    metadata.add(AccessVocabulary.MIME_TYPE, mimeType);

                    // apply an Extractor if available
                    if (extractContents) {
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
            catch (IOException e) {
                LOGGER.log(Level.WARNING, "IOException while processing " + id, e);
            }
            catch (ExtractorException e) {
                LOGGER.log(Level.WARNING, "ExtractorException while processing " + id, e);
            }
        }

        private void displayUnexpectedEventWarning(String event) {
            // as we don't keep track of access data in this example code, some events should never occur
            System.err.println("WARNING: encountered unexpected event (" + event
                    + ") with non-incremental crawler");
        }

        public void clearStarted(Crawler crawler) {
            // no-op
        }

        public void clearingObject(Crawler crawler, String url) {
            // no-op
        }

        public void clearFinished(Crawler crawler, ExitCode exitCode) {
            // no-op
        }

        private void displayMessage(final String message) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    statusLabel.setText(message);
                }
            });
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
