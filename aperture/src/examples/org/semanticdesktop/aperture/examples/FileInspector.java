/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.ntriples.NTriplesWriter;
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
import org.semanticdesktop.aperture.model.Vocabulary;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;

/**
 * The source code of this class shows how to use a MimeTypeIdentifier and a collection of Extractors to
 * get the full-text and metadata of a specified file.
 * 
 * <p>
 * The file to process needs to be specified as the first argument. No checks are performed on the
 * validity of this file, meaning that this method may produce all sorts of IOExceptions.
 * 
 * <p>
 * The full-text and metadata are exported as NTriple-encoded RDF statements to System.out.
 */
public class FileInspector {

    // TODO: rewrite this using mark and reset on the stream so that we can reuse the stream.
    // Somehow this didn't work for me, perhaps I misunderstood how it works?

    public static void main(String[] args) throws IOException, ExtractorException {
        // check if a commandline argument was specified
        if (args.length == 0) {
            System.err.println("Aperture File Inspector\nUsage: java " + FileInspector.class.getName()
                    + " <file>");
            System.exit(-1);
        }

        // We're only going to use a single MimeTypeIdentifier instance and in the current code
        // base there is only a single implementation available, so we instantiate it directly instead of
        // accessing it through a MimeTypeIdentifierRegistry.
        MimeTypeIdentifier identifier = new MagicMimeTypeIdentifierFactory().get();

        // Create an ExtractorRegistry. Right now this initialization process is hard-coded. If this
        // were an OSGi-based application, this would not have been necessary, you would simply be able
        // to ask the platform for implementations of these services. Consequently, a new Extractor
        // implementation would immediately become available.
        ExtractorRegistry extractorRegistry = new ExtractorRegistryImpl();
        extractorRegistry.add(new PlainTextExtractorFactory());
        extractorRegistry.add(new HtmlExtractorFactory());
        extractorRegistry.add(new PdfExtractorFactory());
        extractorRegistry.add(new OpenDocumentExtractorFactory());

        // read as many bytes of the file as desired by the MIME type identifier
        File file = new File(args[0]);
        FileInputStream stream = new FileInputStream(file);
        BufferedInputStream buffer = new BufferedInputStream(stream);
        byte[] bytes = IOUtil.readBytes(buffer, identifier.getMinArrayLength());
        stream.close();

        // let the MimeTypeIdentifier determine the MIME type of this file
        String mimeType = identifier.identify(bytes, file.getPath(), null);

        // exit when the MIME type could not be determined
        if (mimeType == null) {
            System.err.println("MIME type could not be established.");
            System.exit(-1);
        }

        // create the RDFContainer that will hold the RDF model
        URI uri = new URIImpl(file.toURI().toString());
        SesameRDFContainer container = new SesameRDFContainer(uri);

        // retrieve an Extractor that can process this MIME type
        Set factories = extractorRegistry.get(mimeType);
        if (factories != null && !factories.isEmpty()) {
            // just fetch the first available Extractor
            ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
            Extractor extractor = factory.get();

            // apply the extractor on the specified file
            stream = new FileInputStream(file);
            buffer = new BufferedInputStream(stream, 8192);
            extractor.extract(uri, buffer, null, mimeType, container);
            stream.close();
        }

        // add the mime type as an additional statement to the RDF model
        container.put(Vocabulary.MIME_TYPE_URI, mimeType);

        // report the output to System.out
        RDFWriter writer = new NTriplesWriter(System.out);
        try {
            container.getRepository().extractStatements(writer);
        }
        catch (RDFHandlerException e) {
            // signals an internal error
            throw new RuntimeException(e);
        }
    }
}
