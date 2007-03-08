/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.UtfUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextExtractor implements Extractor {

	// Every developer should read Joel Spolsky's "The Absolute Minimum Every Software Developer Absolutely,
	// Positively Must Know About Unicode and Character Sets (No Excuses!)"
	// See: http://www.joelonsoftware.com/articles/Unicode.html

	private Logger logger = LoggerFactory.getLogger(getClass());

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		try {
			// Try to see whether the stream starts with a UTF Byte Order Mark. If a BOM is found, it is
			// consumed and the specified Charset is overruled with a Charset consistent with the BOM.
			PushbackInputStream pbStream = new PushbackInputStream(stream, UtfUtil.MAX_BOM_LENGTH);
			byte[] firstBytes = IOUtil.readBytes(pbStream, UtfUtil.MAX_BOM_LENGTH);
			byte[] bomBytes = UtfUtil.findMatchingBOM(firstBytes);

			if (bomBytes == null) {
				// no BOM: unread all bytes
				pbStream.unread(firstBytes);
			}
			else {
				// a BOM was found: unread the remaining bytes that were already read
				pbStream.unread(firstBytes, bomBytes.length, firstBytes.length - bomBytes.length);

				// lookup the corresponding Charset
				String charsetName = UtfUtil.getCharsetName(bomBytes);
				if (charsetName != null) {
					try {
						charset = Charset.forName(charsetName);
					}
					catch (UnsupportedCharsetException e) {
						logger.info("Unsupported charset, trying to continue with current charset", e);
					}
				}
			}

			// create a Reader that will convert the bytes to characters
			Reader reader = charset == null ? new InputStreamReader(pbStream) : new InputStreamReader(
					pbStream, charset);

			// verify that the first 256 characters really are text characters
			String firstChars = IOUtil.readString(reader, 256);

			int nrChars = firstChars.length();
			for (int i = 0; i < nrChars; i++) {
				char c = firstChars.charAt(i);
				if (!Character.isDefined(c) || (Character.isISOControl(c) && !Character.isWhitespace(c))) {
					// c is not a Unicode char or is a control character that is not a whitespace char
					logger.warn("Document does not contain plain text");
					return;
				}
			}

			// everything is ok, read the full document
			String remainingChars = IOUtil.readString(reader);

			if (firstChars.length() > 0 || remainingChars.length() > 0) {
				result.add(DATA.fullText, firstChars + remainingChars);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}
}
