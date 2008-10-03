/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openrdf.repository.Repository;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.FileExtractor;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.InferenceUtil;

public class FileInspectorPanel extends JPanel {

    private FileSelectorPanel controlPanel = null;

    private MetadataPanel metadataPanel = null;

    private MimeTypeIdentifier mimeTypeIdentifier = null;

    private ExtractorRegistry extractorRegistry = null;

    private JLabel statusBar = null;

    private RDFContainer lastContainer;

    private JFrame parent;
    
    private boolean extractorAvailable;
    
    /**
     * initialized inference utilities
     */
    private InferenceUtil inferenceUtil;

    /**
     * This is the default constructor
     */
    public FileInspectorPanel(JFrame parent) {
        super();
        initialize();
        this.parent = parent;
        initializeAperture();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints11.insets = new java.awt.Insets(3, 10, 3, 10);
        gridBagConstraints11.gridy = 3;
        statusBar = new JLabel();
        statusBar.setText(" ");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.insets = new java.awt.Insets(20, 10, 0, 10);
        gridBagConstraints1.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 10);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(650, 318);
        this.add(getControlPanel(), gridBagConstraints);
        this.add(getMetadataPanel(), gridBagConstraints1);
        this.add(statusBar, gridBagConstraints11);
        this.setTransferHandler(new FileHandler());
    }

    private void initializeAperture() {
        // create a mime type identifier
        mimeTypeIdentifier = new MagicMimeTypeIdentifier();

        // initialize the extractor registry
        extractorRegistry = new DefaultExtractorRegistry();
    };

    public void setFile(File file) {
        // triggers stateChanged event which on its turn triggers inspect(File)
        controlPanel.setFile(file);
    }

    public File getFile() {
        return controlPanel.getFile();
    }

    private void inspect(final File file, final boolean inference) {
        // some checks on whether we can process this file
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File does not exist: " + file.getPath(),
                "Non-existing file", JOptionPane.ERROR_MESSAGE);
        }
        else if (!file.isFile()) {
            JOptionPane.showMessageDialog(this, "Not a file: " + file.getPath(), "Non-file Path",
                JOptionPane.ERROR_MESSAGE);
        }
        else if (!file.canRead()) {
            JOptionPane.showMessageDialog(this, "Cannot read file: " + file.getPath(), "Unreadable file",
                JOptionPane.ERROR_MESSAGE);
        }
        else {
            Thread thread = new Thread() {

                public void run() {
                    process(file, inference);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    private void process(final File file, final boolean inference) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                statusBar.setText("Processing " + file.getPath() + "...");
                FileInspectorPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                metadataPanel.getModel().setMetadata("--", null);
            }
        });

        try {
            extractorAvailable = false;

            // determine the mime type
            FileInputStream stream = new FileInputStream(file);
            int minBufferSize = mimeTypeIdentifier.getMinArrayLength();
            BufferedInputStream buffer = new BufferedInputStream(stream, minBufferSize);

            byte[] bytes = IOUtil.readBytes(buffer, minBufferSize);
            final String mimeType = mimeTypeIdentifier.identify(bytes, file.getPath(), null);

            stream.close();

            // extract the full-text and metadata
            RDFContainer container = null;
            final ProgressDialog dialog = new ProgressDialog(parent,true);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dialog.setText("Extracting text");
                    dialog.setLocationByPlatform(true);
                    dialog.setVisible(true);
                }
            });
            Set factories = extractorRegistry.get(mimeType);
            if (factories != null && !factories.isEmpty()) {
                extractorAvailable = true;
                ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
                Extractor extractor = factory.get();

                RDFContainerFactoryImpl containerFactory = new RDFContainerFactoryImpl();
                container = containerFactory.newInstance(file.toURI().toString());

                // FIXME: use mark() and reset() instead of opening a second stream
                stream = new FileInputStream(file);
                buffer = new BufferedInputStream(stream, 8192);
                extractor.extract(container.getDescribedUri(), buffer, null, mimeType, container);
                stream.close();
            } else {
                Set filefactories = extractorRegistry.getFileExtractorFactories(mimeType);
                if (filefactories != null && !filefactories.isEmpty()) {
                    extractorAvailable = true;
                    FileExtractorFactory factory = (FileExtractorFactory) filefactories.iterator().next();
                    FileExtractor currentFileExtractor = factory.get();

                    RDFContainerFactoryImpl containerFactory = new RDFContainerFactoryImpl();
                    container = containerFactory.newInstance(file.toURI().toString());
                    currentFileExtractor.extract(container.getDescribedUri(), file, null, mimeType, container);
                }
            }
            // do inference
            if (inference) {
                if (inferenceUtil == null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            dialog.setText("Initializing the inference engine (this will happen only once)");
                        }
                    });
                    inferenceUtil = InferenceUtil.createForCoreOntologies();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setText("Inferring additional triples");
                        dialog.setVisible(true);
                    }
                });
                inferenceUtil.extendContent(container);
            }
            // update the UI
            final RDFContainer finalContainer = container;

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    Repository repository = finalContainer == null ? null : (Repository) finalContainer
                            .getModel().getUnderlyingModelImplementation();
                    
                    metadataPanel.getModel().setMetadata(mimeType, repository);

                    if (lastContainer != null) {
                        lastContainer.dispose();
                    }
                    lastContainer = finalContainer;
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setVisible(false);
                    }
                });
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found: " + file.getPath(), "File not found",
                JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "I/O error: " + e.getMessage(), "I/O Error",
                JOptionPane.ERROR_MESSAGE);
        }
        catch (ExtractorException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Extraction error: " + e.getMessage(), "Extraction Error",
                JOptionPane.ERROR_MESSAGE);
        }
        finally {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    FileInspectorPanel.this.setCursor(null);

                    if (!extractorAvailable) {
                        statusBar
                                .setText("<html><b><font color=\"red\">No extractor available for this mime type!</font></b></html>");
                    }
                    else {
                        statusBar.setText(" "); // make sure it has a non-empty string or else its preferred
                                                // height will change!!
                    }
                }
            });
        }
    }

    /**
     * This method initializes controlPanel
     * 
     * @return org.semanticdesktop.aperture.examples.fileinspector.FileSelectorPanel
     */
    private FileSelectorPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new FileSelectorPanel();
            controlPanel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    inspect(controlPanel.getFile(), controlPanel.getInferenceChecked());
                }
            });
        }
        return controlPanel;
    }

    /**
     * This method initializes metadataPanel
     * 
     * @return org.semanticdesktop.aperture.examples.fileinspector.MetadataPanel
     */
    private MetadataPanel getMetadataPanel() {
        if (metadataPanel == null) {
            metadataPanel = new MetadataPanel();
        }
        return metadataPanel;
    }

    public void setTransferHandler(TransferHandler handler) {
        super.setTransferHandler(handler);
        getMetadataPanel().setTransferHandler(handler);
    }

    private class FileHandler extends TransferHandler {

        public boolean canImport(JComponent component, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
                if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                    return true;
                }
            }
            return false;
        }

        public boolean importData(JComponent component, Transferable transferable) {
            try {
                Object value = transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (value instanceof List) {
                    List list = (List) value;
                    if (!list.isEmpty()) {
                        Object firstItem = list.get(0);
                        if (firstItem instanceof File) {
                            File file = (File) firstItem;
                            FileInspectorPanel.this.setFile(file);
                            return true;
                        }
                    }
                }
            }
            catch (Exception e) {
            }

            return false;
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
