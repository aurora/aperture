/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.crawler;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailUpdateException;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.ConfigurationUtil;
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

    private JPanel buttonPanel = null;

    private JButton crawlButton = null;

    private JButton stopButton = null;

    private JLabel progressLabel = null;

    private FileSystemCrawler crawler;

    /**
     * This is the default constructor
     */
    public CrawlerPanel() {
        super();
        initialize();
        installListeners();
        updateEnabledStates();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints3.insets = new java.awt.Insets(0, 10, 0, 10);
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 1;
        progressLabel = new JLabel();
        progressLabel.setText("Status: Inactive");
        progressLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(2,5,2,5)));
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new java.awt.Insets(5, 0, 0, 0);
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 10);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(534, 293);
        this.add(getConfigurationPanel(), gridBagConstraints);
        this.add(getButtonPanel(), gridBagConstraints2);
        this.add(progressLabel, gridBagConstraints3);
    }

    private void installListeners() {
        DocumentListener listener = new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateEnabledStates();
            }

            public void removeUpdate(DocumentEvent e) {
                updateEnabledStates();
            }

            public void changedUpdate(DocumentEvent e) {
                updateEnabledStates();
            }
        };
        configurationPanel.getFileField().getDocument().addDocumentListener(listener);
        configurationPanel.getRepositoryField().getDocument().addDocumentListener(listener);
    }

    private void updateEnabledStates() {
        crawlButton.setEnabled(!configurationPanel.getFileField().getText().equals("")
                && !configurationPanel.getRepositoryField().getText().equals(""));
    }

    /**
     * This method initializes configurationPanel
     * 
     * @return org.semanticdesktop.aperture.examples.crawler.ConfigurationPanel
     */
    private ConfigurationPanel getConfigurationPanel() {
        if (configurationPanel == null) {
            configurationPanel = new ConfigurationPanel();
        }
        return configurationPanel;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(20);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getCrawlButton(), null);
            buttonPanel.add(getStopButton(), null);
        }
        return buttonPanel;
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

    private void crawl() {
        // change enabled states of some buttons
        getCrawlButton().setEnabled(false);
        getStopButton().setEnabled(true);

        // create a FileSystemDataSource
        FileSystemDataSource source = new FileSystemDataSource();
        URI sourceID = new URIImpl("source:testSource");
        source.setID(sourceID);

        // configure the directory tree to crawl
        File rootFile = new File(configurationPanel.getFileField().getText());
        SesameRDFContainer configuration = new SesameRDFContainer(sourceID);
        ConfigurationUtil.setRootUrl(rootFile.toURI().toString(), configuration);
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        
        // setup a CrawlerHandler
        SimpleCrawlerHandler crawlerHandler = new SimpleCrawlerHandler();
        crawler.setCrawlerHandler(crawlerHandler);
        
        boolean identifyMimeType = getConfigurationPanel().getDetermineMimeTypeBox().isSelected();
        boolean extractContents = identifyMimeType && getConfigurationPanel().getExtractContentsBox().isSelected();
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

        private SesameRDFContainer rdfContainer;

        private Repository repository;

        private int nrObjects;

        private MimeTypeIdentifier mimeTypeIdentifier;

        private ExtractorRegistry extractorRegistry;
        
        private boolean identifyMimeType;
        
        private boolean extractContents;

        public SimpleCrawlerHandler() {
            // set up a repository
            rdfContainer = new SesameRDFContainer("file:dummy");
            repository = rdfContainer.getRepository();

            // if we set auto-commit to false, we don't have to hassle with Transaction instances.
            // Statements are now only "really" added after each commit.
            try {
                repository.setAutoCommit(false);
            }
            catch (SailUpdateException e) {
                // will hurt performance but we can still continue. Each add and remove will now be a
                // separate transaction.
                e.printStackTrace();
            }

            // create components for determining file contents
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
                File repositoryFile = new File(configurationPanel.getRepositoryField().getText());
                Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
                RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TRIX, writer);
                repository.extractStatements(rdfWriter);
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
                    crawlButton.setEnabled(true);
                    stopButton.setEnabled(false);
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
            rdfContainer.setDescribedUri(uri);
            rdfContainer.setContext(uri);
            return rdfContainer;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            if (object instanceof FileDataObject) {
                process((FileDataObject) object);
            }
            commit();
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            displayUnexpectedEventWarning("changed");
            commit();
        }

        public void objectNotModified(Crawler crawler, String url) {
            displayUnexpectedEventWarning("unmodified");
            commit();
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
            displayUnexpectedEventWarning("removed");
            commit();
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
                    metadata.put(Vocabulary.MIME_TYPE, mimeType);
                    
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

        private void commit() {
            try {
                repository.commit();
            }
            catch (SailUpdateException e) {
                // don't continue when this happens
                throw new RuntimeException(e);
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
                    CrawlerPanel.this.progressLabel.setText(message);
                }
            });
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
