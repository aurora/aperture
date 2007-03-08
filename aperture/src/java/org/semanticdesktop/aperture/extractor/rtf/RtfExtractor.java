/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtfExtractor implements Extractor {

	/*
	 * I've tried another parser (http://www.cobase.cs.ucla.edu/pub/javacc/rtf_parser_src.jar, used in Nutch)
	 * based on a JavaCC grammer, but Swing's internal parser clearly outperforms this parser both in speed
	 * (it's practically instantaneous) and quality (some documents gave ParseExceptions using the other
	 * parser, Swing handled all my 27 test docs perfectly). This has been tested with Java 5. In my
	 * experience the RTF support in Swing used to be very brittle, apparently something has changed because
	 * it now works like a charm.
	 */

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		RTFEditorKit rtfParser = new RTFEditorKit();
		Document document = rtfParser.createDefaultDocument();
		try {
			rtfParser.read(stream, document, 0);
			String text = document.getText(0, document.getLength());
			result.add(DATA.fullText, text);
		}
		catch (BadLocationException e) {
			// problem relates to the file contents: just log and ignore
            Logger logger = LoggerFactory.getLogger(getClass());
			logger.warn("Bad RTF location", e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}
}
