/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControlPanel extends JPanel {

    private JLabel fileLabel = null;
    private JTextField fileField = null;
    private JButton browseButton = null;
    private JButton inspectButton = null;
    
    private JFileChooser fileChooser;

    /**
     * This is the default constructor
     */
    public ControlPanel() {
        super();
        initialize();
        updateEnabledState();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 3;
        gridBagConstraints2.insets = new java.awt.Insets(0,20,0,0);
        gridBagConstraints2.gridy = 0;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 2;
        gridBagConstraints11.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints11.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0,0,0,10);
        gridBagConstraints.gridy = 0;
        fileLabel = new JLabel();
        fileLabel.setText("File:");
        this.setLayout(new GridBagLayout());
        this.setSize(509, 76);
        this.add(fileLabel, gridBagConstraints);
        this.add(getFileField(), gridBagConstraints1);
        this.add(getBrowseButton(), gridBagConstraints11);
        this.add(getInspectButton(), gridBagConstraints2);
    }

    /**
     * This method initializes filefield	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getFileField() {
        if (fileField == null) {
            fileField = new JTextField();
            fileField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    updateEnabledState();
                }

                public void removeUpdate(DocumentEvent e) {
                    updateEnabledState();
                }

                public void changedUpdate(DocumentEvent e) {
                    updateEnabledState();
                }
            });
        }
        return fileField;
    }

    private void updateEnabledState() {
        inspectButton.setEnabled(!fileField.getText().equals(""));
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
                    }
                    int result = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(ControlPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        fileField.setText(file.getPath());
                    }
                }
            });
        }
        return browseButton;
    }

    /**
     * This method initializes extractButton	
     * 	
     * @return javax.swing.JButton	
     */
    public JButton getInspectButton() {
        if (inspectButton == null) {
            inspectButton = new JButton();
            inspectButton.setText("Inspect");
        }
        return inspectButton;
    }

    public File getSelectedFile() {
        return new File(fileField.getText());
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
