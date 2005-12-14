/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filecrawler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class InputPanel extends JPanel {

    private JLabel folderLabel = null;
    private JTextField folderField = null;
    private JButton browseButton = null;
    private JCheckBox determineMimeTypeBox = null;
    private JCheckBox extractContentsBox = null;
    private JFileChooser fileChooser = null;

    /**
     * This is the default constructor
     */
    public InputPanel() {
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
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.weightx = 1.0D;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.gridy = 3;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.weightx = 1.0D;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints3.insets = new java.awt.Insets(10,0,0,0);
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridy = 2;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.insets = new java.awt.Insets(0,10,0,0);
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0,0,3,0);
        gridBagConstraints.gridy = 0;
        folderLabel = new JLabel();
        folderLabel.setText("Folder to crawl (recursively):");
        this.setLayout(new GridBagLayout());
        this.setSize(520, 129);
        this.add(folderLabel, gridBagConstraints);
        this.add(getDetermineMimeTypeBox(), gridBagConstraints3);
        this.add(getExtractContentsBox(), gridBagConstraints4);
        this.add(getFolderField(), gridBagConstraints1);
        this.add(getBrowseButton(), gridBagConstraints2);
    }

    /**
     * This method initializes folderField	
     * 	
     * @return javax.swing.JTextField	
     */
    public JTextField getFolderField() {
        if (folderField == null) {
            folderField = new JTextField();
        }
        return folderField;
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
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    }
                    
                    int result = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(InputPanel.this));
                    
                    if (result == JFileChooser.APPROVE_OPTION) {
                        folderField.setText(fileChooser.getSelectedFile().getPath());
                    }
                }
            });
        }
        return browseButton;
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
