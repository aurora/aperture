/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openrdf.model.Statement;

public class MetadataPanel extends JPanel {

    private JPanel panelBaseInfo = null;
    private JLabel labelMimetypeL = null;
    private JLabel labelMimetype = null;
    private JScrollPane scrollTextOut = null;
    private JTextArea textOut = null;

    public MetadataPanel() {
        super();
        initialize();
    }

    public MetadataPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initialize();
    }

    public MetadataPanel(LayoutManager layout) {
        super(layout);
        initialize();
    }

    public MetadataPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
        this.add(getPanelBaseInfo(), java.awt.BorderLayout.NORTH);
        this.add(getScrollTextOut(), java.awt.BorderLayout.CENTER);
    }
    
    public void loadDataFrom(FileBrowserData data) {
        labelMimetype.setText(data.getMimetype());
        StringBuffer out = new StringBuffer();
        Collection col = data.getRDF().getRepository().extractStatements();
        for (Iterator iter = col.iterator(); iter.hasNext();)
        {
            Statement statement = (Statement) iter.next();
            out.append(statement.getPredicate().getLocalName());
            out.append(": ");
            out.append(statement.getObject().toString());
            out.append("\n");
        }          
        getTextOut().setText(out.toString());     
    }

    /**
     * This method initializes panelBaseInfo	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getPanelBaseInfo()
    {
        if (panelBaseInfo == null)
        {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.gridy = 0;
            labelMimetype = new JLabel();
            labelMimetype.setText("JLabel");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.gridy = 0;
            labelMimetypeL = new JLabel();
            labelMimetypeL.setText("mimetype: ");
            panelBaseInfo = new JPanel();
            panelBaseInfo.setLayout(new GridBagLayout());
            panelBaseInfo.add(labelMimetypeL, gridBagConstraints);
            panelBaseInfo.add(labelMimetype, gridBagConstraints1);
        }
        return panelBaseInfo;
    }

    /**
     * This method initializes scrollTextOut	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getScrollTextOut()
    {
        if (scrollTextOut == null)
        {
            scrollTextOut = new JScrollPane();
            scrollTextOut.setViewportView(getTextOut());
        }
        return scrollTextOut;
    }

    /**
     * This method initializes textOut	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getTextOut()
    {
        if (textOut == null)
        {
            textOut = new JTextArea();
        }
        return textOut;
    }

}
