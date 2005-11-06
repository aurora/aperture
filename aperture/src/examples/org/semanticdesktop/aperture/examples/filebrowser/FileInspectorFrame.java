/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FileInspectorFrame extends JFrame {

    private JPanel jContentPane = null;
    private FileInspector fileInspector = null;

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
     * @return org.semanticdesktop.aperture.examples.filebrowser.FileInspector	
     */
    private FileInspector getFileInspector() {
        if (fileInspector == null) {
            fileInspector = new FileInspector();
        }
        return fileInspector;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FileInspectorFrame frame = new FileInspectorFrame();
                frame.setVisible(true);
            }
        });
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
