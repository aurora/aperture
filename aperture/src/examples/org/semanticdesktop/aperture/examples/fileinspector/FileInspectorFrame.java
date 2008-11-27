/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FileInspectorFrame extends JFrame {

    private JPanel jContentPane = null;
    private FileInspectorPanel fileInspector = null;

    /**
     * This is the default constructor
     */
    public FileInspectorFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(700, 500);
        this.setContentPane(getJContentPane());
        this.setTitle("Aperture File Inspector");
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
            jContentPane.add(getFileInspector(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }
    
    /**
     * This method initializes fileInspector	
     * 	
     * @return org.semanticdesktop.aperture.examples.fileinspector.FileInspectorPanel	
     */
    public FileInspectorPanel getFileInspector() {
        if (fileInspector == null) {
            fileInspector = new FileInspectorPanel(this);
        }
        return fileInspector;
    }

    public static void main(final String[] args) {
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
                
                // open the frame
                FileInspectorFrame frame = new FileInspectorFrame();
                frame.setVisible(true);
                
                if (args.length > 0) {
                    frame.getFileInspector().setFile(new File(args[0]));
                }
            }
        });
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
