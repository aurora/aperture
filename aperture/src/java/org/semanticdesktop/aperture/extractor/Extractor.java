/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.ParseException;

import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Extractors extract information from binary streams such as document full-text, titles, authors and
 * other metadata that may be supported by the format. Extractors are typically specific for a single
 * mimetype or a number of closely related mimetypes.
 */
public interface Extractor {

	/**
	 * Extracts full-text and metadata from the specified binary stream and stores the extracted
	 * information as RDF statements in the specified RDFContainer. The optionally specified Charset and
	 * mimetype can be used to direct how the stream should be parsed.
	 * 
	 * @param id the URI identifying the object (e.g. a file or web page) from which the stream was
	 *            obtained. The generated statements should describe this URI.
	 * @param stream the InputStream delivering the raw bytes.
	 * @param charset the charset in which the inputstream is encoded (optional).
	 * @param mimetype the mimetype of the passed stream (optional).
	 * @param result the container in which this Extractor can put its created RDF statements.
	 * @throws ParseException when the stream does not conform to the structure expected by this
	 *             Extractor.
	 * @throws IOException in case of any other I/O error.
	 */
	public void extract(URI id, InputStream stream, Charset charset, String mimetype, RDFContainer result)
			throws ParseException, IOException;
}
