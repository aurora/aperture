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
import javax.swing.JCheckBox;

public class ConfigurationPanel extends JPanel {

    private JLabel rootFileLabel = null;
    private JTextField rootFileField = null;
    private JButton rootFileButton = null;
    private JLabel repositoryLabel = null;
    private JTextField repositoryField = null;
    private JButton repositoryButton = null;
    
    private JFileChooser rootFileChooser;
    private JFileChooser repositoryChooser;
    private JCheckBox determineMimeTypeBox = null;
    private JCheckBox extractContentsBox = null;

    /**
     * This is the default constructor
     */
    public ConfigurationPanel() {
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
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.gridwidth = 2;
        gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints21.gridy = 3;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.gridwidth = 2;
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints11.insets = new java.awt.Insets(10,0,0,0);
        gridBagConstraints11.gridy = 2;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.insets = new java.awt.Insets(0,20,0,0);
        gridBagConstraints5.gridy = 5;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridy = 5;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.gridx = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints3.gridwidth = 21;
        gridBagConstraints3.insets = new java.awt.Insets(40,0,5,0);
        gridBagConstraints3.gridy = 4;
        repositoryLabel = new JLabel();
        repositoryLabel.setText("Output file (TriX format):");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.insets = new java.awt.Insets(0,20,0,0);
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0,0,5,0);
        gridBagConstraints.gridy = 0;
        rootFileLabel = new JLabel();
        rootFileLabel.setText("Directory to crawl (recursively!):");
        this.setLayout(new GridBagLayout());
        this.setSize(463, 200);
        this.add(rootFileLabel, gridBagConstraints);
        this.add(getFileField(), gridBagConstraints1);
        this.add(getFileButton(), gridBagConstraints2);
        this.add(repositoryLabel, gridBagConstraints3);
        this.add(getRepositoryField(), gridBagConstraints4);
        this.add(getRepositoryButton(), gridBagConstraints5);
        this.add(getDetermineMimeTypeBox(), gridBagConstraints11);
        this.add(getExtractContentsBox(), gridBagConstraints21);
    }

    /**
     * This method initializes rootFileField	
     * 	
     * @return javax.swing.JTextField	
     */
    public JTextField getFileField() {
        if (rootFileField == null) {
            rootFileField = new JTextField();
        }
        return rootFileField;
    }

    /**
     * This method initializes rootFileButton
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getFileButton() {
        if (rootFileButton == null) {
            rootFileButton = new JButton();
            rootFileButton.setText("Browse...");
            rootFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (rootFileChooser == null) {
                        rootFileChooser = new JFileChooser();
                        rootFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    }
                    
                    int result = rootFileChooser.showOpenDialog(SwingUtilities.windowForComponent(ConfigurationPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        rootFileField.setText(rootFileChooser.getSelectedFile().getPath());
                    }
                }
            });
        }
        return rootFileButton;
    }

    /**
     * This method initializes repositoryField	
     * 	
     * @return javax.swing.JTextField	
     */
    public JTextField getRepositoryField() {
        if (repositoryField == null) {
            repositoryField = new JTextField();
            repositoryField.setText(new File("output.trix").getAbsolutePath());
        }
        return repositoryField;
    }

    /**
     * This method initializes repositoryButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRepositoryButton() {
        if (repositoryButton == null) {
            repositoryButton = new JButton();
            repositoryButton.setText("Browse...");
            repositoryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (repositoryChooser == null) {
                        repositoryChooser = new JFileChooser();
                        repositoryChooser.setSelectedFile(new File(getRepositoryField().getText()));
                        repositoryChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    }
                    
                    int result = repositoryChooser.showOpenDialog(SwingUtilities.windowForComponent(ConfigurationPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        repositoryField.setText(repositoryChooser.getSelectedFile().getPath());
                    }
                }
            });
        }
        return repositoryButton;
    }

    /**
     * This method initializes determineMimeTypeBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    public JCheckBox getDetermineMimeTypeBox() {
        if (determineMimeTypeBox == null) {
            determineMimeTypeBox = new JCheckBox();
            determineMimeTypeBox.setText("Determine MIME type");
            determineMimeTypeBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateEnabledState();
                }
            });
        }
        return determineMimeTypeBox;
    }

    private void updateEnabledState() {
        getExtractContentsBox().setEnabled(getDetermineMimeTypeBox().isSelected());
    }

    /**
     * This method initializes extractContentsBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    public JCheckBox getExtractContentsBox() {
        if (extractContentsBox == null) {
            extractContentsBox = new JCheckBox();
            extractContentsBox.setText("Extract document text and metadata");
        }
        return extractContentsBox;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
