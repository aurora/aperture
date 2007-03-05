/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * The source code of this class shows how to use a MimeTypeIdentifier and a collection of Extractors to get
 * the full-text and metadata of a specified file.
 * 
 * <p>
 * The file to process needs to be specified as the first argument. No checks are performed on the validity of
 * this file, meaning that this method may produce all sorts of IOExceptions.
 * 
 * <p>
 * The full-text and metadata are exported as NTriple-encoded RDF statements to System.out.
 */
public class FileInspector {

	// TODO: rewrite this using mark and reset on the stream so that we can reuse the stream.
	// Somehow this didn't work for me, perhaps I misunderstood how it works?

	public static void main(String[] args) throws Exception {
		// check if a commandline argument was specified
		if (args.length == 0) {
			System.err.println("Aperture File Inspector\nUsage: java " + FileInspector.class.getName()
					+ " <file>");
			System.exit(-1);
		}

		// create a MimeTypeIdentifier
		MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();

		// create an ExtractorRegistry containing all available
		// ExtractorFactories
		ExtractorRegistry extractorRegistry = new DefaultExtractorRegistry();

		// read as many bytes of the file as desired by the MIME type identifier
		File file = new File(args[0]);
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
		URI uri = URIImpl.create(file.toURI().toString());
		Model model = new RepositoryModel(false);
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
		container.add(DATA.mimeType, mimeType);

		// report the output to System.out

		container.getModel().writeTo(new PrintWriter(System.out), Syntax.Ntriples);

	}
}
