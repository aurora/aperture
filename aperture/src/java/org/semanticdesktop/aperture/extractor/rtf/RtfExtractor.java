/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public class RtfExtractor implements Extractor {

	/*
	 * I've tried another parser (http://www.cobase.cs.ucla.edu/pub/javacc/rtf_parser_src.jar, used in Nutch)
	 * based on a JavaCC grammer, but Swing's internal parser clearly outperforms this parser both in speed
	 * (it's practically instantaneous) and quality (some documents gave ParseExceptions using the other
	 * parser, Swing handled all my 27 test docs perfectly). This has been tested with Java 5. In my
	 * experience the RTF support in Swing used to be very brittle, apparently something has changed because
	 * it now works like a charm.
	 */

	private static final Logger LOGGER = Logger.getLogger(RtfExtractor.class.getName());

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		RTFEditorKit rtfParser = new RTFEditorKit();
		Document document = rtfParser.createDefaultDocument();
		try {
			rtfParser.read(stream, document, 0);
			String text = document.getText(0, document.getLength());
			result.put(AccessVocabulary.FULL_TEXT, text);
		}
		catch (BadLocationException e) {
			// problem relates to the file contents: just log and ignore
			LOGGER.log(Level.WARNING, "Bad RTF location", e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}
}
