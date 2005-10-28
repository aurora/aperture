/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.FileDialog;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.semanticdesktop.aperture.util.GuiUtil;

/**
 * visual frame, showing a file browser interface that renders metadata about a file
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class FileBrowserFrame extends JFrame {
    static protected FileDialog fileDlg;

    private FileBrowserData data = new FileBrowserData();
    private JPanel jContentPane = null;
    private JToolBar toolbarFile = null;
    private JTextField textFilename = null;
    private JButton buttonLoad = null;
    private JButton buttonChooseFile = null;
    private MetadataPanel metadataPanel = null;
    

    public FileBrowserFrame() throws HeadlessException {
        super();

        initialize();
    }

    public FileBrowserFrame(GraphicsConfiguration gc) {
        super(gc);
        initialize();
    }

    public FileBrowserFrame(String title) throws HeadlessException {
        super(title);
        initialize();
    }

    public FileBrowserFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(439, 366);
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getJContentPane());
        this.setTitle("Aperture Example File Browser");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if (jContentPane == null)
        {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getToolbarFile(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getMetadataPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }
    
    /**
     * show the passed file now
     * @param file the new file
     */
    public void loadFile(File file) throws ParseException, IOException {
        data.loadFile(file);
        displayData();
        getTextFilename().setText(file.toString());
    }
    
    /**
     * display the data from the data object
     *
     */
    protected void displayData() {
        metadataPanel.loadDataFrom(data);       
    }

    /**
     * This method initializes toolbarFile	
     * 	
     * @return javax.swing.JToolBar	
     */
    private JToolBar getToolbarFile()
    {
        if (toolbarFile == null)
        {
            toolbarFile = new JToolBar();
            toolbarFile.add(getButtonChooseFile());
            toolbarFile.add(getTextFilename());
            toolbarFile.add(getButtonLoad());
        }
        return toolbarFile;
    }

    /**
     * This method initializes textFilename	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getTextFilename()
    {
        if (textFilename == null)
        {
            textFilename = new JTextField();
        }
        return textFilename;
    }

    /**
     * This method initializes buttonLoad	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getButtonLoad()
    {
        if (buttonLoad == null)
        {
            buttonLoad = new JButton();
            buttonLoad.setText("go");
            buttonLoad.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    File f = new File(getTextFilename().getText());
                    try
                    {
                        loadFile(f);
                    } catch (Exception e1)
                    {
                        GuiUtil.showException(e1, FileBrowserFrame.this);
                    }
                }
            });
        }
        return buttonLoad;
    }

    /**
     * This method initializes buttonChooseFile	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getButtonChooseFile()
    {
        if (buttonChooseFile == null)
        {
            buttonChooseFile = new JButton();
            buttonChooseFile.setText("open file");
            buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (fileDlg == null) 
                        fileDlg = new FileDialog(FileBrowserFrame.this);
                    //fileDlg.setFile("*.txt");
                    fileDlg.show();
                    if (fileDlg.getFile() == null)
                        return;
                    String file = fileDlg.getDirectory() + fileDlg.getFile();
                    File fileF = new File(file);
                    try {
                        loadFile(fileF);
                    } catch (Exception x)
                    {
                        GuiUtil.showException(x, FileBrowserFrame.this);
                    }
                }
            });
        }
        return buttonChooseFile;
    }

    /**
     * This method initializes metadataPanel	
     * 	
     * @return org.semanticdesktop.aperture.examples.filebrowser.MetadataPanel	
     */
    private MetadataPanel getMetadataPanel()
    {
        if (metadataPanel == null)
        {
            metadataPanel = new MetadataPanel();
        }
        return metadataPanel;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"

/*
 * $Log$
 * Revision 1.1  2005/10/28 13:14:20  leo_sauermann
 * new project settings and example file gui
 *
 */