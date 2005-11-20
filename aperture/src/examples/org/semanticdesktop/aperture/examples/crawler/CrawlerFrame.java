/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.crawler;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CrawlerFrame extends JFrame {

    private JPanel jContentPane = null;
    private CrawlerPanel crawlerPanel = null;

    /**
     * This is the default constructor
     */
    public CrawlerFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(561, 248);
        this.setContentPane(getJContentPane());
        this.setTitle("Aperture File System Crawler");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
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
            jContentPane.add(getCrawlerPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes crawlerPanel	
     * 	
     * @return org.semanticdesktop.aperture.examples.crawler.CrawlerPanel	
     */
    private CrawlerPanel getCrawlerPanel() {
        if (crawlerPanel == null) {
            crawlerPanel = new CrawlerPanel();
        }
        return crawlerPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CrawlerFrame frame = new CrawlerFrame();
                frame.setVisible(true);
            }
        });
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
