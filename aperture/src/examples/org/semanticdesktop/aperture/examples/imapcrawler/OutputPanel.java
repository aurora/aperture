/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class OutputPanel extends JPanel {

    private JPanel contentPanel = null;

    private JCheckBox mimeTypeBox = null;

    private JCheckBox extractorBox = null;

    private JPanel filePanel = null;

    private JLabel outputExplanationLabel = null;

    private JTextField fileField = null;

    private JButton browseButton = null;

    private JLabel formatLabel = null;

    private JFileChooser fileChooser = null;

    private JTextArea typeExplanationArea = null;

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
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.insets = new java.awt.Insets(20, 0, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(541, 267);
        this.setName("output");
        this.add(getContentPanel(), gridBagConstraints);
        this.add(getFilePanel(), gridBagConstraints1);
    }

    /**
     * This method initializes contentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 1.0;
            gridBagConstraints8.insets = new java.awt.Insets(10,0,0,0);
            gridBagConstraints8.gridx = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.gridx = 0;
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory
                    .createTitledBorder(null, "Attachments",
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null),
                    javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5)));
            contentPanel.add(getMimeTypeBox(), gridBagConstraints2);
            contentPanel.add(getExtractorBox(), gridBagConstraints3);
            contentPanel.add(getTypeExplanationArea(), gridBagConstraints8);
        }
        return contentPanel;
    }

    /**
     * This method initializes mimeTypeBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getMimeTypeBox() {
        if (mimeTypeBox == null) {
            mimeTypeBox = new JCheckBox();
            mimeTypeBox.setText("Determine MIME type");
            mimeTypeBox.setSelected(true);
        }
        return mimeTypeBox;
    }

    /**
     * This method initializes extractorBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getExtractorBox() {
        if (extractorBox == null) {
            extractorBox = new JCheckBox();
            extractorBox.setText("Extract contents");
            extractorBox.setSelected(true);
        }
        return extractorBox;
    }

    /**
     * This method initializes filePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilePanel() {
        if (filePanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.gridwidth = 2;
            gridBagConstraints7.insets = new java.awt.Insets(15,0,0,0);
            gridBagConstraints7.gridy = 2;
            formatLabel = new JLabel();
            formatLabel.setText("The RDF model will be stored in TriX format.");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.insets = new java.awt.Insets(10,10,0,0);
            gridBagConstraints6.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.insets = new java.awt.Insets(10,0,0,0);
            gridBagConstraints5.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridx = 0;
            outputExplanationLabel = new JLabel();
            outputExplanationLabel.setText("Output file for RDF model:");
            filePanel = new JPanel();
            filePanel.setLayout(new GridBagLayout());
            filePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createTitledBorder(null, "Output",
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null),
                    javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5)));
            filePanel.add(outputExplanationLabel, gridBagConstraints4);
            filePanel.add(getFileField(), gridBagConstraints5);
            filePanel.add(getBrowseButton(), gridBagConstraints6);
            filePanel.add(formatLabel, gridBagConstraints7);
        }
        return filePanel;
    }

    /**
     * This method initializes fileField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFileField() {
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
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    }

                    fileChooser.setSelectedFile(new File(getFileField().getText()));

                    int result = fileChooser.showOpenDialog(SwingUtilities
                            .windowForComponent(OutputPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        getFileField().setText(fileChooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        }
        return browseButton;
    }

    public boolean checkInputComplete() {
        String text = getFileField().getText().trim();
        if (text.equals("")) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this),
                    "Please enter an output file.", "Missing input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
            return true;
        }
    }
    
    public boolean isMimeTypeSelected() {
        return getMimeTypeBox().isSelected();
    }
    
    public boolean isExtractionSelected() {
        return getMimeTypeBox().isSelected() && getExtractorBox().isSelected();
    }

    public File getOutputFile() {
        return new File(getFileField().getText().trim());
    }

    /**
     * This method initializes typeExplanationArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getTypeExplanationArea() {
        if (typeExplanationArea == null) {
            typeExplanationArea = new JTextArea();
            typeExplanationArea.setText("When MIME type detection is disabled, content extraction will fall back on the MIME type as mentioned in the mail.");
            typeExplanationArea.setWrapStyleWord(true);
            typeExplanationArea.setLineWrap(true);
            typeExplanationArea.setOpaque(false);
        }
        return typeExplanationArea;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
