/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filecrawler;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class ConfigurationPanel extends JPanel {

    private InputPanel inputPanel = null;
    private OutputPanel outputPanel = null;

    /**
     * This is the default constructor
     */
    public ConfigurationPanel() {
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
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(20,0,0,0);
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(530, 303);
        this.add(getInputPanel(), gridBagConstraints);
        this.add(getOutputPanel(), gridBagConstraints1);
    }

    /**
     * This method initializes inputPanel	
     * 	
     * @return org.semanticdesktop.aperture.examples.filecrawler.InputPanel	
     */
    public InputPanel getInputPanel() {
        if (inputPanel == null) {
            inputPanel = new InputPanel();
            inputPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null), javax.swing.BorderFactory.createEmptyBorder(5,10,5,10)));
        }
        return inputPanel;
    }

    /**
     * This method initializes outputPanel	
     * 	
     * @return org.semanticdesktop.aperture.examples.filecrawler.OutputPanel	
     */
    public OutputPanel getOutputPanel() {
        if (outputPanel == null) {
            outputPanel = new OutputPanel();
            outputPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null), javax.swing.BorderFactory.createEmptyBorder(5,10,5,10)));
        }
        return outputPanel;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
