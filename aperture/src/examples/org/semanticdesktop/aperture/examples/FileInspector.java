/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.ontoware.rdf2go.model.Syntax;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * The source code of this class shows how to use a MimeTypeIdentifier and a collection of Extractors to get
 * the full-text and metadata of a specified file.
 * 
 * <p>
 * The file to process needs to be specified as the first argument. No checks are performed on the validity of
 * this file, meaning that this method may produce all sorts of IOExceptions.
 * 
 * <p>
 * The full-text and metadata are exported as Turtle-encoded RDF statements to System.out.
 */
public class FileInspector {

    public static void main(String[] args) throws Exception {
        // check if a commandline argument was specified
        if (args.length == 0) {
            System.err.println("Aperture File Inspector\nUsage: java " + FileInspector.class.getName()
                    + " <file>");
            System.exit(-1);
        }

        // create a MimeTypeIdentifier
        MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();

        // create an ExtractorRegistry containing all Extractors
        ExtractorRegistry extractorRegistry = new DefaultExtractorRegistry();

        // create a stream of the specified file
        File file = new File(args[0]);
        FileInputStream stream = new FileInputStream(file);

        // read as many bytes of the file as desired by the MIME type identifier
        int minimumArrayLength = identifier.getMinArrayLength();
        int bufferSize = Math.max(minimumArrayLength, 8192);
        BufferedInputStream buffer = new BufferedInputStream(stream, bufferSize);
        buffer.mark(minimumArrayLength + 10); // add some for safety
        byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);

        // let the MimeTypeIdentifier determine the MIME type of this file
        String mimeType = identifier.identify(bytes, file.getPath(), null);

        // skip the extraction phase when the MIME type could not be determined
        if (mimeType == null) {
            System.err.println("WARNING: MIME type could not be established.");
        }
        else {
            // create the RDFContainer that will hold the RDF model
            RDFContainerFactoryImpl containerFactory = new RDFContainerFactoryImpl();
            RDFContainer container = containerFactory.newInstance(file.toURI().toString());

            // determine and apply an Extractor that can handle this MIME type
            Set factories = extractorRegistry.get(mimeType);
            if (factories != null && !factories.isEmpty()) {
                // just fetch the first available Extractor
                ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
                Extractor extractor = factory.get();

                // apply the extractor on the specified file
                buffer.reset();
                extractor.extract(container.getDescribedUri(), buffer, null, mimeType, container);
            }
            
            // add the MIME type as an additional statement to the RDF model
            container.add(NIE.mimeType, mimeType);

            // report the output to System.out
            container.getModel().writeTo(System.out, Syntax.Turtle);
            container.dispose();
        }

        buffer.close();
    }
}
