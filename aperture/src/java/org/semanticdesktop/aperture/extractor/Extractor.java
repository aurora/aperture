/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Extractors extract information from binary streams such as document full-text, titles, authors and other
 * metadata that may be supported by the format. Extractors are typically specific for a single MIME type or a
 * number of closely related MIME types.
 */
public interface Extractor {

	/**
	 * Extracts full-text and metadata from the specified binary stream and stores the extracted information
	 * as RDF statements in the specified RDFContainer. The optionally specified Charset and MIME type can be
	 * used to direct how the stream should be parsed.
	 * 
	 * <p>
	 * The specified InputStream is expected to already use some kind of buffering so that the Extractors are
	 * not required to internally buffer bytes to improve performance.
	 * 
	 * @param id the URI identifying the object (e.g. a file or web page) from which the stream was obtained.
	 *            The generated statements should describe this URI.
	 * @param stream the InputStream delivering the raw bytes.
	 * @param charset the charset in which the inputstream is encoded (optional).
	 * @param mimeType the MIME type of the passed stream (optional).
	 * @param result the container in which this Extractor can put its created RDF statements.
	 * @throws ExtractorException in case of any error during the extraction process.
	 */
	public void extract(URI id, InputStream stream, Charset charset,
			String mimeType, RDFContainer result) throws ExtractorException;
}
