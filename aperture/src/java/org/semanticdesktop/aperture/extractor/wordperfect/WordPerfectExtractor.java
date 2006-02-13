/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.wordperfect;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.StringExtractor;

/**
 * An Extractor implementation for WordPerfect documents.
 * 
 * <p>
 * This implementation uses heuristic string extraction algorithms, tuned for WordPerfect files but without
 * any intrinsic knowledge of the WordPerfect file format(s). Consequently, the extracted full-text may be
 * imperfect, e.g. contain some noise that's not part of the document text. Also, the document metadata is not
 * extracted.
 * 
 * <p>
 * The current status of this implementation is that the complete full-text is extracted from WordPerfect
 * documents from version 4.2 up to WordPerfect X3 (tested with 4.2, 5.0, 5.1 and X3, all created using
 * WordPerfect X3), except for the 5.1 Far East format for which our test did not return any text at all. This
 * is probably due to encoding issues. These tests showed that for WordPerfect 5.0 and 5.1 the document
 * metadata also ends up at the start of the extracted full-text.
 */
public class WordPerfectExtractor implements Extractor {

	private static final String[] EXACT_START_LINES = { "doc init", "tech init" };

	private static final String[] START_EXCLUDES = { "wpc", "monotype sorts", "section", "columns",
			"aligned ", "standard", "default ", "biblio", "footnote", "gfootnote", "endnote", "heading",
			"header for ", "underlined heading", "centered heading", "technical", "object #",
			"microsoft word" };

	private static final String[] END_EXCLUDES = { "aligned paragraph numbers", "heading", "bullet list"
	// " style", " roman", "laserJet", "bullet list", "defaults", "typestyle", "land", "landscape", "portrait"
	};

	private static final String[] EXACT_EXCLUDES = { "nlus.", "initialize technical style", "document style",
			"pleading", "times", "and", "where", "left", "right", "over", "(k over", "document", "header",
			"footer", "itemize", "page number", "pages", "body text", "word", "sjablone" };

	private static final String[] CONTAIN_EXCLUDES = { "left (", "right )", "right ]", "right par",
			"default paragraph", };

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		stream = new WPFilterInputStream(stream);
		FuzzyTextExtractor extractor = new FuzzyTextExtractor();

		try {
			String text = extractor.extract(stream).trim();
			if (text.length() > 0) {
				result.put(AccessVocabulary.FULL_TEXT, text);
			}
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private static class FuzzyTextExtractor extends StringExtractor {

		// overrides StringExtractor.isTextCharacter
		protected boolean isTextCharacter(int charNumber) {
			return super.isTextCharacter(charNumber) || charNumber >= 0xC0 && charNumber <= 0xFF
			// accented ANSI character
					|| charNumber == 0x91 // backquote
					|| charNumber == 0x92; // quote
		}

		// overrides StringExtractor.isStartLine
		protected boolean isStartLine(String lineLowerCase) {
			for (int i = 0; i < EXACT_START_LINES.length; i++) {
				if (lineLowerCase.equals(EXACT_START_LINES[i])) {
					return true;
				}
			}
			return false;
		}

		// overrides StringExtractor.isValidLine
		protected boolean isValidLine(String lineLowerCase) {
			for (int i = 0; i < EXACT_EXCLUDES.length; i++) {
				if (lineLowerCase.equals(EXACT_EXCLUDES[i])) {
					return false;
				}
			}

			for (int i = 0; i < START_EXCLUDES.length; i++) {
				if (lineLowerCase.startsWith(START_EXCLUDES[i])) {
					return false;
				}
			}

			for (int i = 0; i < END_EXCLUDES.length; i++) {
				if (lineLowerCase.endsWith(END_EXCLUDES[i])) {
					return false;
				}
			}

			// most expensive operation: make sure this is the last check
			for (int i = 0; i < CONTAIN_EXCLUDES.length; i++) {
				if (lineLowerCase.indexOf(CONTAIN_EXCLUDES[i]) >= 0) {
					return false;
				}
			}

			return super.isValidLine(lineLowerCase);
		}
	}

	/**
	 * A FilterInputStream that processes bytes that have a special meaning in WordPerfect documents.
	 * Processed characters are: <tt>0x80</tt> (space character in WP 6), <tt>0xA9</tt> (hyphen in WP 6)
	 * and <tt>0xAC</tt> (word break indicator).
	 */
	private static class WPFilterInputStream extends FilterInputStream {

		public WPFilterInputStream(InputStream in) {
			super(new PushbackInputStream(in, 4));
		}

		/**
		 * Converts all characters to 0 except for the readable ASCII-characters and characters from special
		 * byte sequences.
		 */
		public int read() throws IOException {
			int result = in.read();

			if (result >= 0x20 && result <= 0x7E) {
				// readable ASCII character, leave it intact
			}
			else if (result == -1) {
				// end of file, leave it intact
			}
			else if (result == 0x80) {
				// replace WP 6 space with normal space
				result = ' ';
			}
			else if (result == 0xA9) {
				// replace special word separator with normal '-'
				result = '-';
			}
			else if (result == 0xAC) {
				// remove word break indicators from stream
				result = read();
			}
			else if (result == 0xC0) {
				// this could mark the start of a 4-byte sequence representing a
				// non-ASCII character (e.g. an accented character)
				byte[] buf = new byte[3];
				int bytesRead = IOUtil.fillByteArray(in, buf);

				if (bytesRead == 3 && buf[2] == (byte) 0xC0) {
					// likely, a pattern was found
					if (buf[1] == (byte) 0x01) {
						switch (buf[0]) {
						case (byte) 0x17:
							return 0xDF; // szlig
						case (byte) 0x1A:
							return 0xC1; // Aacute
						case (byte) 0x1B:
							return 0xE1; // aacute
						case (byte) 0x1C:
							return 0xC2; // Acirc
						case (byte) 0x1D:
							return 0xE2; // acirc
						case (byte) 0x1E:
							return 0xC4; // Auml
						case (byte) 0x1F:
							return 0xE4; // auml
						case (byte) 0x20:
							return 0xC0; // Agrave
						case (byte) 0x21:
							return 0xE0; // agrave
						case (byte) 0x22:
							return 0xC5; // A with small circle
						case (byte) 0x23:
							return 0xE5; // a with small circle
						case (byte) 0x24:
							return 0xC6; // AE
						case (byte) 0x25:
							return 0xE6; // ae
						case (byte) 0x26:
							return 0xC7; // C-cedille
						case (byte) 0x27:
							return 0xE7; // c-cedille
						case (byte) 0x28:
							return 0xC9; // Eacute
						case (byte) 0x29:
							return 0xE9; // eacute
						case (byte) 0x2A:
							return 0xCA; // Ecirc
						case (byte) 0x2B:
							return 0xEA; // ecirc
						case (byte) 0x2C:
							return 0xCB; // Euml
						case (byte) 0x2D:
							return 0xEB; // euml
						case (byte) 0x2E:
							return 0xC8; // Egrave
						case (byte) 0x2F:
							return 0xE8; // egrave
						case (byte) 0x30:
							return 0xCD; // Iacute
						case (byte) 0x31:
							return 0xED; // iacute
						case (byte) 0x32:
							return 0xCE; // Icirc
						case (byte) 0x33:
							return 0xEE; // icirc
						case (byte) 0x34:
							return 0xCF; // Iuml
						case (byte) 0x35:
							return 0xEF; // iuml
						case (byte) 0x36:
							return 0xCC; // Igrave
						case (byte) 0x37:
							return 0xEC; // igrave
						case (byte) 0x38:
							return 0xD1; // Ntitle
						case (byte) 0x39:
							return 0xF1; // ntitle
						case (byte) 0x3A:
							return 0xD3; // Oacute
						case (byte) 0x3B:
							return 0xF3; // oacute
						case (byte) 0x3C:
							return 0xD4; // Ocirc
						case (byte) 0x3D:
							return 0xF4; // ocirc
						case (byte) 0x3E:
							return 0xD6; // Ouml
						case (byte) 0x3F:
							return 0xF6; // ouml
						case (byte) 0x40:
							return 0xD2; // Ograve
						case (byte) 0x41:
							return 0xF2; // ograve
						case (byte) 0x42:
							return 0xDA; // Uacute
						case (byte) 0x43:
							return 0xFA; // uacute
						case (byte) 0x44:
							return 0xDB; // Ucirc
						case (byte) 0x45:
							return 0xFB; // ucirc
						case (byte) 0x46:
							return 0xDC; // Uuml
						case (byte) 0x47:
							return 0xFC; // uuml
						case (byte) 0x48:
							return 0xD9; // Ugrave
						case (byte) 0x49:
							return 0xF9; // ugrave
						case (byte) 0x4A:
							return (int) 'Y'; // Yuml (not supported in ANSI)
						case (byte) 0x4B:
							return 0xFF; // yuml
						case (byte) 0x4C:
							return 0xC3; // Atilde
						case (byte) 0x4D:
							return 0xE3; // atilde
						case (byte) 0x4E:
							return 0xD0; // ETH (again, see 0x56)
						case (byte) 0x50:
							return 0xD8; // Oslash
						case (byte) 0x51:
							return 0xF8; // oslash
						case (byte) 0x52:
							return 0xD5; // Otilde
						case (byte) 0x53:
							return 0xF5; // otilde
						case (byte) 0x54:
							return 0xDD; // Yacute
						case (byte) 0x55:
							return 0xFD; // yacute
						case (byte) 0x56:
							return 0xD0; // ETH
						case (byte) 0x57:
							return 0xF0; // eth
						case (byte) 0x58:
							return 0xDE; // THORN
						case (byte) 0x59:
							return 0xFE; // thorn
						}
					}
					else if (buf[1] == (byte) 0x04) {
						switch (buf[0]) {
						case (byte) 0x1C:
							return 0x92; // quote
						case (byte) 0x1D:
							return 0x91; // backquote
						}
					}
				}

				// No special sequence was recognized, unread the buffer
				((PushbackInputStream) in).unread(buf, 0, bytesRead);

				result = 0;
			}
			else if (result == 0xC3 || result == 0xC4) {
				// this could mark the start of a 3-byte sequence representing a
				// change in type face (bold, italic, underlined).
				byte[] buf = new byte[2];
				int bytesRead = IOUtil.fillByteArray(in, buf);

				if (bytesRead == 2 && (result == 0xC3 && buf[1] == (byte) 0xC3 ||
				// start/bold/italic/underline
						result == 0xC4 && buf[1] == (byte) 0xC4) // end bold/italic/underline
						&& (buf[0] == (byte) 0x08 || // italic/bold/underline
								buf[0] == (byte) 0x0C || buf[0] == (byte) 0x0E)) {
					// ignore control sequence
					result = read();
				}
				else {
					// no special sequence was recognized, unread the buffer
					((PushbackInputStream) in).unread(buf, 0, bytesRead);

					result = 0;
				}
			}
			else {
				// convert all other character to 0 to prevent StringExtractor to accept accented
				// characters that were not encoded and thus had another meaning
				result = 0;
			}

			return result;
		}

		public int read(byte[] byteArray, int off, int len) throws IOException {
			int i = off;
			for (; i < off + len; i++) {
				int b = read();

				if (b == -1) {
					if (i == off) {
						// no bytes were available
						return -1;
					}
					break;
				}

				byteArray[i] = (byte) b;
			}

			// return the number of bytes that were read
			return i - off;
		}

		// public long skip(long n) throws IOException {
		// long i = 0L;
		//			
		// for (; i < n; i++) {
		// int b = read();
		//
		// if (b == -1) {
		// break;
		// }
		// }
		//
		// return i;
		// }
	}
}
