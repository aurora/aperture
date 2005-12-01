/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.crawler;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class OutputPanel extends JPanel {

    private JLabel fileLabel = null;
    private JTextField fileField = null;
    private JButton browseButton = null;
    private JLabel locationLabel = null;
    private JFileChooser fileChooser = null;

    /**
     * This is the default constructor
     */
    public OutputPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.gridwidth = 2;
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.insets = new java.awt.Insets(10,0,0,0);
        gridBagConstraints7.gridy = 2;
        locationLabel = new JLabel();
        locationLabel.setText("The RDF model will be stored in TriX format.");
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints6.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0,0,3,0);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridy = 0;
        fileLabel = new JLabel();
        fileLabel.setText("Output file for RDF Model:");
        this.setLayout(new GridBagLayout());
        this.setSize(530, 145);
        this.add(fileLabel, gridBagConstraints);
        this.add(getFileField(), gridBagConstraints1);
        this.add(getBrowseButton(), gridBagConstraints6);
        this.add(locationLabel, gridBagConstraints7);
    }

    /**
     * This method initializes fileField	
     * 	
     * @return javax.swing.JTextField	
     */
    public JTextField getFileField() {
        if (fileField == null) {
            fileField = new JTextField();
            fileField.setText(new File("output.trix").getAbsolutePath());
        }
        return fileField;
    }

    /**
     * This method initializes browseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse...");
            browseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fileChooser == null) {
                        fileChooser = new JFileChooser();
                        fileChooser.setSelectedFile(new File(fileField.getText()));
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    }
                    
                    int result = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(OutputPanel.this));
                    
                    if (result == JFileChooser.APPROVE_OPTION) {
                        fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return browseButton;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
