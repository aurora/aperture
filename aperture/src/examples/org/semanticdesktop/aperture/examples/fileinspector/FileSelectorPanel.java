/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.JCheckBox;

public class FileSelectorPanel extends JPanel {

    private File file;

    protected EventListenerList listenerList;

    private ChangeEvent changeEvent;

    private JLabel fileLabel = null;

    private JTextField fileField = null;

    private JButton browseButton = null;

    private JFileChooser fileChooser;

	private JCheckBox inferenceCheck = null;

    /**
     * This is the default constructor
     */
    public FileSelectorPanel() {
        super();
        listenerList = new EventListenerList();
        changeEvent = new ChangeEvent(this);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 0;
        gridBagConstraints12.gridwidth = 2;
        gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints12.gridy = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 2;
        gridBagConstraints11.insets = new java.awt.Insets(0, 10, 0, 0);
        gridBagConstraints11.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        gridBagConstraints.gridy = 0;
        fileLabel = new JLabel();
        fileLabel.setText("Inspected File:");
        this.setLayout(new GridBagLayout());
        this.setSize(509, 76);
        this.add(fileLabel, gridBagConstraints);
        this.add(getFileField(), gridBagConstraints1);
        this.add(getBrowseButton(), gridBagConstraints11);
        this.add(getInferenceCheck(), gridBagConstraints12);
    }
    
    /**
     * This method initializes filefield
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFileField() {
        if (fileField == null) {
            fileField = new JTextField();
            fileField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // circumvent setFile so that the field doesn't update again:
                    // unnecessary and leads to circular events
                    file = new File(fileField.getText());
                    fireStateChanged();
                }
            });
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
                    }
                    int result = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(FileSelectorPanel.this));
                    if (result == JFileChooser.APPROVE_OPTION) {
                        setFile(fileChooser.getSelectedFile());
                    }
                }
            });
        }
        return browseButton;
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * Returns an array of all ChangeListeners registered on this FileSelectorPanel.
     * 
     * @return all of this model's ChangeListeners or an empty array if no change listeners are currently
     *         registered.
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
    }

    protected void fireStateChanged() {
        // guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
        fileField.setText(file.getPath());
        fireStateChanged();
    }

	/**
	 * This method initializes inferenceCheck	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getInferenceCheck() {
	    if (inferenceCheck == null) {
	        inferenceCheck = new JCheckBox();
	        inferenceCheck.setText("inference?");
	        inferenceCheck.setToolTipText("check to add inferred facts based on subproperties/subclasses. For example, nie:subject is added when a nmo:messageSubject is present.");
	        inferenceCheck.setSelected(true);
	        inferenceCheck.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	                
	            }
	        });
	    }
	    return inferenceCheck;
	}

    /**
     * @return return whether inference is true or not
     */
    public boolean getInferenceChecked() {
        return getInferenceCheck().isSelected();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
