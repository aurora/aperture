/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.mail.Store;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.semanticdesktop.aperture.examples.ExampleImapCrawler;

public class CrawlerWizard extends JPanel {

    private JPanel sheetsPanel = null;

    private JButton backButton = null;

    private JButton nextButton = null;

    private JButton cancelButton = null;

    private ConnectionPanel connectionPanel = null;

    private FolderPanel folderPanel = null;

    private OutputPanel outputPanel = null;

    private CrawlerPanel crawlerPanel = null;

    private Component currentPanel;

    private SummaryPanel summaryPanel = null;

    private ExampleImapCrawler crawler;

    private Timer timer;

    /**
     * This is the default constructor
     */
    public CrawlerWizard() {
        super();
        initialize();

        final JButton stopButton = getCrawlerPanel().getStopButton();
        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ExampleImapCrawler crawler = CrawlerWizard.this.crawler;
                if (crawler != null) {
                    crawler.stop();
                    stopButton.setEnabled(false);
                }
            }
        });

        currentPanel = getConnectionPanel();
        updateButtons();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.insets = new java.awt.Insets(0, 20, 0, 10);
        gridBagConstraints3.gridy = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.insets = new java.awt.Insets(0, 3, 0, 0);
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(440, 296);
        this.add(getSheetsPanel(), gridBagConstraints);
        this.add(getBackButton(), gridBagConstraints1);
        this.add(getNextButton(), gridBagConstraints2);
        this.add(getCancelButton(), gridBagConstraints3);
    }

    /**
     * This method initializes sheetsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSheetsPanel() {
        if (sheetsPanel == null) {
            sheetsPanel = new JPanel();
            sheetsPanel.setLayout(new CardLayout());
            sheetsPanel.add(getConnectionPanel(), getConnectionPanel().getName());
            sheetsPanel.add(getFolderPanel(), getFolderPanel().getName());
            sheetsPanel.add(getOutputPanel(), getOutputPanel().getName());
            sheetsPanel.add(getSummaryPanel(), getSummaryPanel().getName());
            sheetsPanel.add(getCrawlerPanel(), getCrawlerPanel().getName());
        }
        return sheetsPanel;
    }

    /**
     * This method initializes backButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBackButton() {
        if (backButton == null) {
            backButton = new JButton();
            backButton.setText("< Back");
            backButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    goBack();
                }
            });
        }
        return backButton;
    }

    /**
     * This method initializes nextButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton();
            nextButton.setText("Next >");
            nextButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    goNext();
                }
            });
        }
        return nextButton;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
        }
        return cancelButton;
    }

    /**
     * This method initializes connectionPanel
     * 
     * @return org.semanticdesktop.aperture.examples.imapcrawler.ConnectionPanel
     */
    private ConnectionPanel getConnectionPanel() {
        if (connectionPanel == null) {
            connectionPanel = new ConnectionPanel();
            connectionPanel.setName("connectionPanel");
        }
        return connectionPanel;
    }

    /**
     * This method initializes folderPanel
     * 
     * @return org.semanticdesktop.aperture.examples.imapcrawler.FolderPanel
     */
    private FolderPanel getFolderPanel() {
        if (folderPanel == null) {
            folderPanel = new FolderPanel();
            folderPanel.setName("folderPanel");
        }
        return folderPanel;
    }

    /**
     * This method initializes outputPanel
     * 
     * @return org.semanticdesktop.aperture.examples.imapcrawler.OutputPanel
     */
    private OutputPanel getOutputPanel() {
        if (outputPanel == null) {
            outputPanel = new OutputPanel();
            outputPanel.setName("outputPanel");
        }
        return outputPanel;
    }

    /**
     * This method initializes crawlerPanel
     * 
     * @return org.semanticdesktop.aperture.examples.imapcrawler.CrawlerPanel
     */
    private CrawlerPanel getCrawlerPanel() {
        if (crawlerPanel == null) {
            crawlerPanel = new CrawlerPanel();
            crawlerPanel.setName("crawlerPanel");
        }
        return crawlerPanel;
    }

    private void updateButtons() {
        ExampleImapCrawler crawler = this.crawler;
        boolean finishedCrawling = crawler != null && crawler.getExitCode() != null;

        if (finishedCrawling) {
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
            cancelButton.setEnabled(true);
            cancelButton.setText("Finish");
        }
        else {
            backButton.setEnabled(currentPanel != connectionPanel && currentPanel != crawlerPanel);
            nextButton.setEnabled(currentPanel != crawlerPanel);
            cancelButton.setEnabled(currentPanel != crawlerPanel);
        }
    }

    private void goBack() {
        CardLayout layout = (CardLayout) sheetsPanel.getLayout();
        layout.previous(sheetsPanel);
        currentPanel = lookupCurrentSheet();
        updateButtons();
    }

    private void goNext() {
        boolean proceed = true;

        if (currentPanel == connectionPanel) {
            proceed = handleConnection();
        }
        else if (currentPanel == folderPanel) {
            proceed = handleFolder();
        }
        else if (currentPanel == outputPanel) {
            proceed = handleOutput();
        }
        else if (currentPanel == summaryPanel) {
            proceed = handleSummary();
        }
        else if (currentPanel == crawlerPanel) {
            proceed = handleCrawling();
        }

        if (proceed) {
            CardLayout layout = (CardLayout) sheetsPanel.getLayout();
            layout.next(getSheetsPanel());
            currentPanel = lookupCurrentSheet();
            updateButtons();
        }
    }

    private Component lookupCurrentSheet() {
        Component[] components = sheetsPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].isShowing()) {
                return components[i];
            }
        }
        return null;
    }

    private boolean handleConnection() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        boolean result = false;

        if (connectionPanel.checkInputComplete()) {
            Store store = connectionPanel.getConnectedStore();
            if (store == null) {
                result = false;
            }
            else {
                folderPanel.setStore(store);
                result = true;
            }
        }
        else {
            result = false;
        }

        setCursor(null);
        return result;
    }

    private boolean handleFolder() {
        return folderPanel.checkInputComplete();
    }

    private boolean handleOutput() {
        // only continue when enough information is available
        if (!outputPanel.checkInputComplete()) {
            return false;
        }

        // fetch all properties
        String server = connectionPanel.getServer();
        String username = connectionPanel.getUsername();
        String password = connectionPanel.getPassword();
        boolean sslSelected = connectionPanel.isSslSelected();

        String folder = folderPanel.getFolder();

        boolean identifyMimeType = outputPanel.isMimeTypeSelected();
        boolean extractContents = outputPanel.isExtractionSelected();
        File repositoryFile = outputPanel.getRepositoryFile();

        // set a summary text in the summary panel
        StringBuffer buffer = new StringBuffer(300);

        buffer.append("Mail server:  ");
        buffer.append(server);

        if (username != null) {
            buffer.append("\nUser name:  ");
            buffer.append(username);
            if (password == null) {
                buffer.append("\nNo password specified");
            }
        }

        buffer.append("\nSecure connection:  ");
        buffer.append(sslSelected ? "yes (SSL)" : "no");

        buffer.append("\n\nMail folder:  ");
        buffer.append(folder);

        buffer.append("\n\nMIME type detection:  ");
        buffer.append(identifyMimeType ? "on" : "off");
        buffer.append("\nExtract contents:  ");
        buffer.append(extractContents ? "on" : "off");

        buffer.append("\n\nRepository file:  ");
        buffer.append(repositoryFile.getAbsolutePath());

        summaryPanel.setSummary(buffer.toString());

        // create an ExampleImapCrawler configured with these settings
        crawler = new ExampleImapCrawler();
        crawler.setServerName(server);
        crawler.setUsername(username);
        crawler.setPassword(password);
        crawler.setFolder(folder);
        crawler.setRepositoryFile(repositoryFile);
        crawler.setIdentifyingMimeType(identifyMimeType);
        crawler.setExtractingContents(extractContents);

        return true;
    }

    private boolean handleSummary() {
        // let the CrawlerPanel know where to fetch its status information
        crawlerPanel.setCrawler(crawler);

        // start crawling in a separate thread
        new CrawlingThread().start();

        // start a timer that periodically updates the crawling panel and the buttons
        timer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                crawlerPanel.updateInfo();
                updateButtons();

                if (crawler.getExitCode() != null) {
                    timer.stop();
                    crawlerPanel.updateInfo();
                }
            }
        });
        timer.setRepeats(true);
        timer.start();

        return true;
    }

    private boolean handleCrawling() {
        return false;
    }

    /**
     * This method initializes summaryPanel
     * 
     * @return org.semanticdesktop.aperture.examples.imapcrawler.SummaryPanel
     */
    private SummaryPanel getSummaryPanel() {
        if (summaryPanel == null) {
            summaryPanel = new SummaryPanel();
            summaryPanel.setName("summaryPanel");
        }
        return summaryPanel;
    }

    private class CrawlingThread extends Thread {

        public CrawlingThread() {
            setPriority(MIN_PRIORITY);
        }

        public void run() {
            crawler.crawl();
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
