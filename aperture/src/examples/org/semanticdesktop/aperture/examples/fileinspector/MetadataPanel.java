/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MetadataPanel extends JPanel implements ChangeListener {

    private MetadataModel model;
    private JLabel mimeTypeMessageLabel = null;
    private JLabel mimeTypeValueLabel = null;
    private JTabbedPane tabbedPane = null;
    private JScrollPane fullTextScrollPane = null;
    private JTextArea fullTextArea = null;
    private StatementsPanel statementsPanel = null;
    private JPanel fullTextPanel = null;
    private JTextArea fullTextMessageArea = null;
    /**
     * This is the default constructor
     */
    public MetadataPanel() {
        super();
        initialize();
        
        model = new MetadataModel();
        model.addChangeListener(this);
        
        updateDisplay();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.insets = new java.awt.Insets(5,0,0,0);
        gridBagConstraints2.gridx = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(0,0,0,0);
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0,0,0,10);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridx = 0;
        mimeTypeValueLabel = new JLabel();
        mimeTypeValueLabel.setText("--");
        mimeTypeMessageLabel = new JLabel();
        mimeTypeMessageLabel.setText("Detected MIME Type:");
        this.setLayout(new GridBagLayout());
        this.setSize(500, 200);
        this.add(mimeTypeMessageLabel, gridBagConstraints);
        this.add(mimeTypeValueLabel, gridBagConstraints1);
        this.add(getTabbedPane(), gridBagConstraints2);
    }
    
    public MetadataModel getModel() {
        return model;
    }

    /**
     * This method initializes tabbedPane	
     * 	
     * @return javax.swing.JTabbedPane	
     */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Full-text", null, getFullTextPanel(), null);
            tabbedPane.addTab("Metadata", null, getStatementsPanel(), null);
        }
        return tabbedPane;
    }

    /**
     * This method initializes fullTextScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getFullTextScrollPane() {
        if (fullTextScrollPane == null) {
            fullTextScrollPane = new JScrollPane();
            fullTextScrollPane.setViewportView(getFullTextArea());
        }
        return fullTextScrollPane;
    }

    /**
     * This method initializes fullTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getFullTextArea() {
        if (fullTextArea == null) {
            fullTextArea = new JTextArea();
            fullTextArea.setWrapStyleWord(true);
            fullTextArea.setLineWrap(true);
        }
        return fullTextArea;
    }

    public void stateChanged(ChangeEvent e) {
        updateDisplay();
    }
    
    private void updateDisplay() {
        // update UI accordingly
        String mimeType = model.getMimeType();
        mimeTypeValueLabel.setText(mimeType == null ? "--" : mimeType);
        
        fullTextArea.setText(model.getFullText());
        fullTextArea.setCaretPosition(0);
        
        statementsPanel.setRepository(model.getRepository());
    }

    /**
     * This method initializes statementsPanel1
     * 	
     * @return org.semanticdesktop.aperture.examples.fileinspector.StatementsPanel	
     */
    private StatementsPanel getStatementsPanel() {
        if (statementsPanel == null) {
            statementsPanel = new StatementsPanel();
            statementsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
            statementsPanel.setOpaque(false);
        }
        return statementsPanel;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getFullTextPanel() {
        if (fullTextPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.weighty = 0.0D;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.insets = new java.awt.Insets(5,0,0,0);
            gridBagConstraints4.weightx = 1.0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(10,0,0,0);
            fullTextPanel = new JPanel();
            fullTextPanel.setLayout(new GridBagLayout());
            fullTextPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
            fullTextPanel.setOpaque(false);
            fullTextPanel.add(getFullTextMessageArea(), gridBagConstraints4);
            fullTextPanel.add(getFullTextScrollPane(), gridBagConstraints3);
        }
        return fullTextPanel;
    }

    /**
     * This method initializes fullTextMessageArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getFullTextMessageArea() {
        if (fullTextMessageArea == null) {
            fullTextMessageArea = new JTextArea();
            fullTextMessageArea.setText("The following full-text was extracted from the file.\nLeading and trailing white space may have been removed.");
            fullTextMessageArea.setLineWrap(true);
            fullTextMessageArea.setOpaque(false);
            fullTextMessageArea.setWrapStyleWord(true);
        }
        return fullTextMessageArea;
    }
    
    public void setTransferHandler(TransferHandler handler) {
        super.setTransferHandler(handler);
        getFullTextMessageArea().setTransferHandler(handler);
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
