/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class StatementsPanel extends JPanel {

    private Repository repository;

    private JLabel statementsLabel = null;

    private JScrollPane statementsScrollPane = null;

    private JTextArea statementsTextArea = null;

    private JLabel formatLabel = null;

    private JComboBox formatBox = null;

    private DefaultComboBoxModel formatBoxModel = null; // @jve:decl-index=0:visual-constraint=""

    private JLabel warningLabel = null;

	private JButton buttonSaveAs = null;

    protected JFileChooser fileChooser;

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
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 1;
        gridBagConstraints12.gridwidth = 2;
        gridBagConstraints12.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints12.gridy = 2;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints11.gridwidth = 3;
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.insets = new java.awt.Insets(0, 0, 10, 0);
        gridBagConstraints11.gridy = 0;
        warningLabel = new JLabel();
        warningLabel.setText("Note: partial RDF shown, literal values are clipped to "
                + LiteralClipper.DEFAULT_MAX_LENGTH + " characters.");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 0.0D;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.insets = new java.awt.Insets(0, 0, 0, 30);
        gridBagConstraints1.gridx = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
        gridBagConstraints.gridy = 1;
        formatLabel = new JLabel();
        formatLabel.setText("Serialization Format:");
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.gridy = 3;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.gridwidth = 3;
        gridBagConstraints3.insets = new java.awt.Insets(5, 0, 0, 0);
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.gridy = 1;
        statementsLabel = new JLabel();
        statementsLabel.setText("Statements:");
        this.setLayout(new GridBagLayout());
        this.setSize(new java.awt.Dimension(543, 289));
        this.add(statementsLabel, gridBagConstraints2);
        this.add(getStatementsScrollPane(), gridBagConstraints3);
        this.add(formatLabel, gridBagConstraints);
        this.add(getFormatBox(), gridBagConstraints1);
        this.add(warningLabel, gridBagConstraints11);
        this.add(getButtonSaveAs(), gridBagConstraints12);
    }

    public void setRepository(Repository repository) {
        Repository oldRepository = this.repository;
        this.repository = repository;
        updateDisplay();
        firePropertyChange("repository", oldRepository, this.repository);
    }

    private void updateDisplay() {
        String text = null;
        RepositoryConnection connection = null;
        RDFWriter writer = null;
        StringWriter buffer = null;

        if (repository != null) {
            try {
                // determine the selected RDFFormat
                RDFFormat format = (RDFFormat) formatBoxModel.getSelectedItem();

                // create an RDFWriter based on the chosen format
                buffer = new StringWriter(10000);
                writer = Rio.createWriter(format, buffer);

                // export the statements to a String

                // wrap the writer in a utility RDFHandler that clips long literals
                // (JTextArea - or actually Swing - will become unstable with long strongs)
                RDFHandler handler = new LiteralClipper(writer);

                // export the statements
                connection = repository.getConnection();
                connection.export(handler);
                text = buffer.toString();
            }
            catch (Exception e) {
                text = "Exception while extracting statements:\n\n" + e.getMessage()
                        + "\n\nPartial contents:\n\n" + buffer.toString();
            }
            finally {
                closeConnection(connection);
            }

            text = text.trim();
        }

        // update UI
        statementsTextArea.setText(text);
        statementsTextArea.setCaretPosition(0);
    }

    private void closeConnection(RepositoryConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
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
            formatBox.setRenderer(new FormatRenderer());
            formatBox.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    updateDisplay();
                }
            });
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

    private static class FormatRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

            String text = renderer.getText();
            if (RDFFormat.RDFXML.equals(value)) {
                text = "XML-Encoded RDF";
            }
            else if (RDFFormat.NTRIPLES.equals(value)) {
                text = "N-Triples";
            }
            else if (RDFFormat.N3.equals(value)) {
                text = "N3/Notation3";
            }
            else if (RDFFormat.TURTLE.equals(value)) {
                text = "Turtle";
            }
            else if (RDFFormat.TRIX.equals(value)) {
                text = "TriX";
            }
            renderer.setText(text);

            return renderer;
        }
    }

	/**
	 * This method initializes buttonSaveAs	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonSaveAs() {
	    if (buttonSaveAs == null) {
	        buttonSaveAs = new JButton();
	        buttonSaveAs.setText("save as...");
	        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent event) {
                    if (fileChooser == null) {
                        fileChooser = new JFileChooser();
                    }
                    int result = fileChooser.showSaveDialog(SwingUtilities.windowForComponent(StatementsPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        // save
                        File f = fileChooser.getSelectedFile();
                        if (f==null)
                            return;
                        RepositoryConnection connection = null;
                        RDFWriter writer = null;
                        FileWriter out = null;

                        if (repository != null) {
                            try {
                                // determine the selected RDFFormat
                                RDFFormat format = (RDFFormat) formatBoxModel.getSelectedItem();

                                // create an RDFWriter based on the chosen format
                                out = new FileWriter(f);
                                try {
                                    writer = Rio.createWriter(format, out);
    
                                    // export the statements
                                    connection = repository.getConnection();
                                    connection.export(writer);
                                } finally {
                                    out.close();
                                }
                                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(StatementsPanel.this), "saved.");
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            finally {
                                closeConnection(connection);
                            }
                        }
                    }
	            }
	        });
	    }
	    return buttonSaveAs;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
