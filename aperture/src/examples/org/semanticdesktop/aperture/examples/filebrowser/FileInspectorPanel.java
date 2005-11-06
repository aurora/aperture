/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.html.HtmlExtractorFactory;
import org.semanticdesktop.aperture.extractor.impl.ExtractorRegistryImpl;
import org.semanticdesktop.aperture.extractor.opendocument.OpenDocumentExtractorFactory;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorFactory;
import org.semanticdesktop.aperture.extractor.plaintext.PlainTextExtractorFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;
import org.semanticdesktop.aperture.util.IOUtil;
import javax.swing.JSeparator;

public class FileInspectorPanel extends JPanel {

    private ControlPanel controlPanel = null;

    private MetadataPanel metadataPanel = null;

    private MimeTypeIdentifier mimeTypeIdentifier = null;

    private ExtractorRegistry extractorRegistry = null;

    /**
     * This is the default constructor
     */
    public FileInspectorPanel() {
        super();
        initialize();

        initializeAperture();
        initializeInspectionListener();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.insets = new java.awt.Insets(20,10,10,10);
        gridBagConstraints1.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 10);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(648, 542);
        this.add(getControlPanel(), gridBagConstraints);
        this.add(getMetadataPanel(), gridBagConstraints1);
    }

    private void initializeAperture() {
        // initialize the mime type identifier
        mimeTypeIdentifier = new MagicMimeTypeIdentifierFactory().get();

        // initialize the extractor registry
        extractorRegistry = new ExtractorRegistryImpl();
        extractorRegistry.add(new PlainTextExtractorFactory());
        extractorRegistry.add(new HtmlExtractorFactory());
        extractorRegistry.add(new PdfExtractorFactory());
        extractorRegistry.add(new OpenDocumentExtractorFactory());
    };

    private void initializeInspectionListener() {
        getControlPanel().getInspectButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                inspect();
            }
        });
    }

    private void inspect() {
        inspect(controlPanel.getSelectedFile());
    }
    
    public void inspect(File file) {
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
            try {
                // determine the mime type
                FileInputStream stream = new FileInputStream(file);
                int minBufferSize = mimeTypeIdentifier.getMinArrayLength();
                BufferedInputStream buffer = new BufferedInputStream(stream, minBufferSize);
                
                byte[] bytes = IOUtil.readBytes(stream, minBufferSize);
                String mimeType = mimeTypeIdentifier.identify(bytes, file.getPath(), null);
                
                stream.close();
                
                // extract the full-text and metadata
                URI uri = new URIImpl(file.toURI().toString());
                RDFContainerSesame container = new RDFContainerSesame(file.toURI().toString());

                Set factories = extractorRegistry.get(mimeType);
                if (factories != null && !factories.isEmpty()) {
                    ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
                    Extractor extractor = factory.get();
                    
                    // Somehow I couldn't get this working with a single stream and buffer and the use
                    // of mark() and reset(). I probably misunderstood something in the API. For now I'll just
                    // open a second stream.
                    stream = new FileInputStream(file);
                    buffer = new BufferedInputStream(stream, 8192);
                    extractor.extract(uri, buffer, null, mimeType, container);
                    stream.close();
                }
                
                // update the UI
                metadataPanel.getModel().setMetadata(mimeType, container.getRepository());
            }
            catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "File not found: " + file.getPath(), "File not found",
                        JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this, "I/O error: " + e.getMessage(), "I/O Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            catch (ExtractorException e) {
                JOptionPane.showMessageDialog(this, "Extraction error: " + e.getMessage(),
                        "Extraction Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method initializes controlPanel
     * 
     * @return org.semanticdesktop.aperture.examples.filebrowser.ControlPanel
     */
    private ControlPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new ControlPanel();
        }
        return controlPanel;
    }

    /**
     * This method initializes metadataPanel
     * 
     * @return org.semanticdesktop.aperture.examples.filebrowser.MetadataPanel
     */
    private MetadataPanel getMetadataPanel() {
        if (metadataPanel == null) {
            metadataPanel = new MetadataPanel();
        }
        return metadataPanel;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
