/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.tutorials;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;


public class ExtractorExample {
    public static void main(String[] args) throws Exception {
        // create a MimeTypeIdentifier
        MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();
        // create an ExtractorRegistry containing all available
        // ExtractorFactories
        ExtractorRegistry extractorRegistry = new DefaultExtractorRegistry();
        // read as many bytes of the file as desired by the MIME type identifier
        File file = new File("somefile.someextension");
        FileInputStream stream = new FileInputStream(file);
        BufferedInputStream buffer = new BufferedInputStream(stream);
        byte[] bytes = IOUtil.readBytes(buffer, identifier.getMinArrayLength());
        stream.close();
        // let the MimeTypeIdentifier determine the MIME type of this file
        String mimeType = identifier.identify(bytes, file.getPath(), null);
        // skip when the MIME type could not be determined
        if (mimeType == null) {
            System.err.println("MIME type could not be established.");
            return;
        }
        // create the RDFContainer that will hold the RDF model
        URI uri = new URIImpl(file.toURI().toString());
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer container = new RDFContainerImpl(model, uri);
        // determine and apply an Extractor that can handle this MIME type
        Set factories = extractorRegistry.get(mimeType);
        if (factories != null && !factories.isEmpty()) {
            // just fetch the first available Extractor
            ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
            Extractor extractor = factory.get();
 
            // apply the extractor on the specified file
            // (just open a new stream rather than buffer the previous stream)
            stream = new FileInputStream(file);
            buffer = new BufferedInputStream(stream, 8192);
            extractor.extract(uri, buffer, null, mimeType, container);
            stream.close();
        }
        // add the MIME type as an additional statement to the RDF model
        container.add(NIE.mimeType, mimeType);
        // report the output to System.out
        container.getModel().writeTo(new PrintWriter(System.out),Syntax.Ntriples);
    }
}

