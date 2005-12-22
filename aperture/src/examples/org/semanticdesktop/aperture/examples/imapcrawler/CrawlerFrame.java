/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CrawlerFrame extends JFrame {

    private JPanel jContentPane = null;
    private CrawlerWizard wizardPanel = null;

    /**
     * This is the default constructor
     */
    public CrawlerFrame() {
        super();
        initialize();
        installListeners();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(529, 390);
        this.setContentPane(getJContentPane());
        this.setTitle("Aperture IMAP Crawler");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                shutDown();
            }
        });
    }

    private void installListeners() {
        getWizardPanel().getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shutDown();
            }
        });
    }
    
    private void shutDown() {
        System.exit(0);
    }
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getWizardPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes wizardPanel	
     * 	
     * @return org.semanticdesktop.aperture.examples.imapcrawler.CrawlerWizard	
     */
    private CrawlerWizard getWizardPanel() {
        if (wizardPanel == null) {
            wizardPanel = new CrawlerWizard();
            wizardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20,20,20,20));
        }
        return wizardPanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // change look and feel
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                    try {
                        UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
                    }
                    catch (Exception e) {
                        // ignore
                    }
                }
                
                // set up a CrawlerFrame
                CrawlerFrame frame = new CrawlerFrame();
                frame.setVisible(true);
            }
        });
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
