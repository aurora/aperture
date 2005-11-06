/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import javax.swing.JPanel;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sesame.repository.Repository;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.io.StringWriter;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;

public class StatementsPanel extends JPanel {

    private Repository repository;
    private JLabel statementsLabel = null;
    private JScrollPane statementsScrollPane = null;
    private JTextArea statementsTextArea = null;
    private JLabel formatLabel = null;
    private JComboBox formatBox = null;
    private DefaultComboBoxModel formatBoxModel = null;  //  @jve:decl-index=0:visual-constraint=""

    /**
     * This is the default constructor
     */
    public StatementsPanel() {
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
        gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 0.0D;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.gridx = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0,0,0,10);
        gridBagConstraints.gridy = 0;
        formatLabel = new JLabel();
        formatLabel.setText("Format:");
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.gridwidth = 3;
        gridBagConstraints3.insets = new java.awt.Insets(5,0,0,0);
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.gridy = 0;
        statementsLabel = new JLabel();
        statementsLabel.setText("Statements:");
        this.setLayout(new GridBagLayout());
        this.setSize(new java.awt.Dimension(543,289));
        this.add(statementsLabel, gridBagConstraints2);
        this.add(getStatementsScrollPane(), gridBagConstraints3);
        this.add(formatLabel, gridBagConstraints);
        this.add(getFormatBox(), gridBagConstraints1);
    }
    
    public void setRepository(Repository repository) {
        Repository oldRepository = this.repository;
        this.repository = repository;
        updateDisplay();
        firePropertyChange("repository", oldRepository, this.repository);
    }
    
    private void updateDisplay() {
        // determine the selected RDFFormat
        Object format = formatBoxModel.getSelectedItem();
        
        // choose a RDFWriter based on the chosen format
        RDFWriter writer = null;
        StringWriter buffer = new StringWriter(10000);
        
        
        if (RDFFormat.RDFXML.equals(format)) {
            writer = new RDFXMLWriter(buffer);
        }
        else if (RDFFormat.NTRIPLES.equals(format)) {
            writer = new NTriplesWriter(buffer);
        }
        else if (RDFFormat.N3.equals(format)) {
            writer = new N3Writer(buffer);
        }
        else if (RDFFormat.TURTLE.equals(format)) {
            writer = new TurtleWriter(buffer);
        }
        else if (RDFFormat.TRIX.equals(format)) {
            writer = new TriXWriter(buffer);
        }
        
        // export the statements to a String
        String text;
        if (writer == null) {
            text = "Unrecognized RDF format: " + format;
        }
        else {
            try {
                // wrap the writer in a utility RDFHandler that clips long literals
                // (JTextArea - or actually Swing - will become unstable with long strongs)
                RDFHandler handler = new LiteralClipper(writer);

                // export the statements
                repository.extractStatements(handler);
                text = buffer.toString();
            }
            catch (RDFHandlerException e) {
                text = "Exception while extracting statements:\n\n" + e.getMessage() +
                    "\n\nPartial contents:\n\n" + buffer.toString();
            }
        }
        
        // update UI
        statementsTextArea.setText(text);
        statementsTextArea.setCaretPosition(0);
    }

    public Repository getRepository() {
        return repository;
    }

    /**
     * This method initializes statementsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getStatementsScrollPane() {
        if (statementsScrollPane == null) {
            statementsScrollPane = new JScrollPane();
            statementsScrollPane.setViewportView(getStatementsTextArea());
        }
        return statementsScrollPane;
    }

    /**
     * This method initializes statementsTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getStatementsTextArea() {
        if (statementsTextArea == null) {
            statementsTextArea = new JTextArea();
            statementsTextArea.setEditable(false);
        }
        return statementsTextArea;
    }

    /**
     * This method initializes formatBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getFormatBox() {
        if (formatBox == null) {
            formatBox = new JComboBox();
            formatBox.setModel(getFormatBoxModel());
        }
        return formatBox;
    }

    /**
     * This method initializes formatBoxModel	
     * 	
     * @return javax.swing.DefaultComboBoxModel	
     */
    private DefaultComboBoxModel getFormatBoxModel() {
        if (formatBoxModel == null) {
            formatBoxModel = new DefaultComboBoxModel();
            formatBoxModel.addElement(RDFFormat.RDFXML);
            formatBoxModel.addElement(RDFFormat.NTRIPLES);
            formatBoxModel.addElement(RDFFormat.N3);
            formatBoxModel.addElement(RDFFormat.TURTLE);
            formatBoxModel.addElement(RDFFormat.TRIX);
            formatBoxModel.setSelectedItem(RDFFormat.RDFXML);
        }
        return formatBoxModel;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
