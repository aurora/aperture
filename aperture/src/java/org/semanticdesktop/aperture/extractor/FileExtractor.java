/*
 * Copyright (c) 2007 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.File;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * FileExtractors extract information from files. FileExtractors are typically specific for a single MIME type or a
 * number of closely related MIME types. They are used in cases where efficient metadata extraction is difficult
 * or impossible to implement with an InputStream.
 */
public interface FileExtractor {

	/**
	 * Extracts full-text and metadata from the specified file and stores the extracted information
	 * as RDF statements in the specified RDFContainer. The optionally specified Charset and MIME type can be
	 * used to direct how the stream should be parsed.
	 * 
	 * <p>
	 * 
	 * @param id the URI identifying the object (e.g. a file or web page) from which the file was obtained.
	 *            The generated statements should describe this URI. Note that it doesn't have to be
	 *            the URI of the file that is passed as the second argument. 
	 * @param file the File with the content from which metadata is to be extracted
	 * @param charset the charset in which the file is encoded (optional).
	 * @param mimeType the MIME type of the file (optional).
	 * @param result the container in which this FileExtractor can put the RDF statements it generates
	 * @throws ExtractorException in case of any error during the extraction process.
	 */
	public void extract(URI id, File file, Charset charset,
			String mimeType, RDFContainer result) throws ExtractorException;
}
