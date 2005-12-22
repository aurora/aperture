/*
 * Copyright (c) 2005.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;

public class SummaryPanel extends JPanel {

    private JLabel label1 = null;
    private JTextArea summaryArea = null;
    private JLabel label2 = null;

    /**
     * This is the default constructor
     */
    public SummaryPanel() {
        super();
        initialize();
        Font font = getSummaryArea().getFont();
        label1.setFont(font);
        label2.setFont(font);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.weighty = 1.0D;
        gridBagConstraints2.gridy = 2;
        label2 = new JLabel();
        label2.setText("Click the Next button to start crawling.");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new java.awt.Insets(15,30,15,0);
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 0;
        label1 = new JLabel();
        label1.setText("You are about to start crawling with the following settings:");
        this.setLayout(new GridBagLayout());
        this.setSize(486, 335);
        this.add(label1, gridBagConstraints);
        this.add(getSummaryArea(), gridBagConstraints1);
        this.add(label2, gridBagConstraints2);
    }

    /**
     * This method initializes summaryArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getSummaryArea() {
        if (summaryArea == null) {
            summaryArea = new JTextArea();
            summaryArea.setEditable(false);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setLineWrap(true);
            summaryArea.setOpaque(false);
        }
        return summaryArea;
    }
    
    public void setSummary(String summary) {
        getSummaryArea().setText(summary);
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
