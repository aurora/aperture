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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

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
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.file.FileAccessorFactory;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class CrawlerPanel extends JPanel {

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
        gridBagConstraints3.insets = new java.awt.Insets(0,10,0,10);
        gridBagConstraints3.gridy = 1;
        progressLabel = new JLabel();
        progressLabel.setText("Status: Inactive");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new java.awt.Insets(5,0,0,0);
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.insets = new java.awt.Insets(0,10,20,10);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(528, 203);
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
        
        source.setConfiguration(new SesameRDFContainer(sourceID));
        File rootFile = new File(configurationPanel.getFileField().getText());
        source.setRootFile(rootFile);
        
        // setup a crawler
        crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setCrawlerHandler(new SimpleCrawlerHandler());
        
        DataAccessorRegistry registry = new DataAccessorRegistryImpl();
        registry.add(new FileAccessorFactory());
        crawler.setDataAccessorRegistry(registry);
        
        Thread thread = new Thread() {
            public void run() {
                crawler.crawl();
                crawler = null;
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
    
    private class SimpleCrawlerHandler implements CrawlerHandler {

        private SesameRDFContainer rdfContainer;
        
        private Repository repository;

        private int nrObjects;
        
        public SimpleCrawlerHandler() {
            // set up a repository
            rdfContainer = new SesameRDFContainer("file:dummy");
            repository = rdfContainer.getRepository();
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
                JOptionPane.showMessageDialog(CrawlerPanel.this, "Exception while saving RDF file, see stderr.", "Exception", JOptionPane.ERROR_MESSAGE);
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

        public RDFContainer getRDFContainer(Crawler crawler, String url) {
            return rdfContainer;
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            process(object);
        }

        public void objectChanged(Crawler dataCrawler, DataObject object) {
            process(object);
        }

        public void objectNotModified(Crawler crawler, String url) {
        }

        public void objectRemoved(Crawler dataCrawler, String url) {
        }

        private void process(DataObject object) {
            
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
