/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.examples.ExampleImapCrawler;

public class CrawlerPanel extends JPanel {

    private JLabel folderLabel = null;

    private JLabel messageLabel = null;

    private JLabel countLabel = null;

    private JLabel folderValue = null;

    private JLabel messageValue = null;

    private JLabel countValue = null;

    private JProgressBar progressBar = null;

    private JButton stopButton = null;

    private ExampleImapCrawler crawler;

    private JTextArea messageArea = null;

    /**
     * This is the default constructor
     */
    public CrawlerPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints11.gridy = 6;
        gridBagConstraints11.weightx = 1.0;
        gridBagConstraints11.weighty = 1.0;
        gridBagConstraints11.gridwidth = 2;
        gridBagConstraints11.insets = new java.awt.Insets(30,60,0,60);
        gridBagConstraints11.gridx = 0;
        GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
        gridBagConstraints32.gridx = 0;
        gridBagConstraints32.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints32.insets = new java.awt.Insets(20, 0, 0, 0);
        gridBagConstraints32.gridwidth = 2;
        gridBagConstraints32.gridy = 5;
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridwidth = 2;
        gridBagConstraints21.insets = new java.awt.Insets(40, 60, 0, 60);
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 4;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 1;
        gridBagConstraints7.insets = new java.awt.Insets(10, 10, 0, 0);
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.gridy = 3;
        countValue = new JLabel();
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.insets = new java.awt.Insets(10, 10, 0, 0);
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints5.gridy = 1;
        messageValue = new JLabel();
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.insets = new java.awt.Insets(0, 10, 0, 0);
        gridBagConstraints4.gridy = 0;
        folderValue = new JLabel();
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints3.insets = new java.awt.Insets(10, 0, 0, 0);
        gridBagConstraints3.gridy = 3;
        countLabel = new JLabel();
        countLabel.setText("Number of processed objects:");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 0, 0, 0);
        gridBagConstraints1.gridy = 1;
        messageLabel = new JLabel();
        messageLabel.setText("Crawling message ID:");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridy = 0;
        folderLabel = new JLabel();
        folderLabel.setText("Crawling folder:");
        this.setLayout(new GridBagLayout());
        this.setSize(305, 222);
        this.setName("progress");
        this.add(folderLabel, gridBagConstraints);
        this.add(messageLabel, gridBagConstraints1);
        this.add(countLabel, gridBagConstraints3);
        this.add(folderValue, gridBagConstraints4);
        this.add(messageValue, gridBagConstraints5);
        this.add(countValue, gridBagConstraints7);
        this.add(getProgressBar(), gridBagConstraints21);
        this.add(getStopButton(), gridBagConstraints32);
        this.add(getMessageArea(), gridBagConstraints11);
    }

    /**
     * This method initializes progressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar();
        }
        return progressBar;
    }

    /**
     * This method initializes stopButton
     * 
     * @return javax.swing.JButton
     */
    public JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setText("Stop");
            stopButton.setEnabled(false);
        }
        return stopButton;
    }

    /**
     * This method initializes messageArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getMessageArea() {
        if (messageArea == null) {
            messageArea = new JTextArea();
            messageArea.setOpaque(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setEditable(false);
        }
        return messageArea;
    }

    public void setCrawler(ExampleImapCrawler crawler) {
        this.crawler = crawler;
        updateInfo();
    }

    public ExampleImapCrawler getCrawler() {
        return crawler;
    }

    public void updateInfo() {
        ExitCode exitCode = crawler.getExitCode();
        boolean running = exitCode == null;

        String messageID = crawler.getCurrentURL();
        if (running) {
            if (messageID == null) {
                messageID = "--";
            }
            else {
                int index = messageID.lastIndexOf('/');
                if (index >= 0 && index < messageID.length() - 1) {
                    messageID = messageID.substring(index + 1);
                }
            }
        }
        else {
            messageID = "--";
        }

        String completionMessage = running ? null : "Finished crawling the IMAP folder (exit code: "
                + exitCode + ").\nThe RDF model is stored in "
                + crawler.getOutputFile().getAbsolutePath();

        folderValue.setText(crawler.getFolder());
        messageValue.setText(messageID);
        countValue.setText(String.valueOf(crawler.getNrObjects()));
        progressBar.setIndeterminate(running);
        progressBar.setEnabled(running);
        stopButton.setEnabled(running && !crawler.isStopRequested());
        messageArea.setText(completionMessage);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
