/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import javax.swing.JLabel;

public class FileInspectorPanel extends JPanel {

    private FileSelectorPanel controlPanel = null;

    private MetadataPanel metadataPanel = null;

    private MimeTypeIdentifier mimeTypeIdentifier = null;

    private ExtractorRegistry extractorRegistry = null;

    private JLabel statusBar = null;

    /**
     * This is the default constructor
     */
    public FileInspectorPanel() {
        super();
        initialize();

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
        gridBagConstraints11.insets = new java.awt.Insets(3,10,3,10);
        gridBagConstraints11.gridy = 3;
        statusBar = new JLabel();
        statusBar.setText(" ");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.insets = new java.awt.Insets(20,10,0,10);
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

    public void inspect(final File file) {
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
                    process(file);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    private void process(final File file) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                statusBar.setText("Processing " + file.getPath() + "...");
                FileInspectorPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
        
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
                // of mark() and reset(). I probably misunderstood something in the API. For now I'll
                // just
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
            JOptionPane.showMessageDialog(this, "Extraction error: " + e.getMessage(), "Extraction Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        finally {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    FileInspectorPanel.this.setCursor(null);
                    statusBar.setText(" "); // make sure it has a non-empty string or else its preferred height will change!!
                }
            });
        }
    }

    /**
     * This method initializes controlPanel
     * 
     * @return org.semanticdesktop.aperture.examples.filebrowser.FileSelectorPanel
     */
    private FileSelectorPanel getControlPanel() {
        if (controlPanel == null) {
            controlPanel = new FileSelectorPanel();
            controlPanel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    inspect(controlPanel.getFile());
                }
            });
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
