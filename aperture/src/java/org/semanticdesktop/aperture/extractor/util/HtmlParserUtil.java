/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.lexer.InputStreamSource;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.ParserFeedback;
import org.htmlparser.util.Translate;
import org.htmlparser.visitors.NodeVisitor;
import org.semanticdesktop.aperture.extractor.ExtractorException;

/**
 * A utility class for HTML parsing using the HTMLParser library. This class embeds all the logic necessary to
 * apply HTMLParser at the cost of a slightly more complicated NodeVisitor, namely one that can be reset.
 * 
 * @link http://htmlparser.sourceforge.net/
 */
public class HtmlParserUtil {

	private static final Logger LOGGER = Logger.getLogger(HtmlParserUtil.class.getName());

	private static final int BUFFER_SIZE = InputStreamSource.BUFFER_SIZE;

	private static final ParserFeedback FEEDBACK_LOGGER = new ParserFeedback() {

		public void info(String message) {
			LOGGER.log(Level.INFO, message);
		}

		public void warning(String message) {
			LOGGER.log(Level.INFO, message);
		}

		public void error(String message, ParserException e) {
			LOGGER.log(Level.WARNING, message, e);
		}
	};

	/**
	 * Parses the specified document stream using the HTMLParser library, using the specified
	 * ResetableNodeVisitor as a NodeVisitor during parsing.
	 * 
	 * @param stream The stream containing the HTML document.
	 * @param charset The charset of the HTML document (optional);
	 * @param extractor The extractor that is informed about encountered document parts.
	 * @throws ExtractorException
	 */
	public static void parse(InputStream stream, Charset charset, ContentExtractor extractor)
			throws ExtractorException {
		// the specified charset will be used to direct the parser, until it encounters a meta tag that
		// tells him to use a different charset
		String charsetName = (charset == null) ? Page.DEFAULT_CHARSET : charset.displayName();

		// wrap the InputStream in a BufferedInputStream if it does not support mark and reset
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, BUFFER_SIZE);
		}

		// mark the stream with a sufficiently high read limit so that the Parser can do a reset after it
		// encounteres a <meta http-equiv="content-type" content="..."> statement. Apparently the Parser
		// does a reset in this case but does not do a mark beforehand. The chosen read limit should be
		// greater than or equal to the buffer size of the InputStreamSource created later on as the
		// latter will fill its entire buffer.
		stream.mark(BUFFER_SIZE);

		// parse the document
		try {
			// setup some data structures
			InputStreamSource source = new InputStreamSource(stream, charsetName, BUFFER_SIZE);
			Page page = new Page(source);
			Lexer lexer = new Lexer(page);
			Parser parser = new Parser(lexer, FEEDBACK_LOGGER);

			// start parsing
			try {
				try {
					parser.visitAllNodesWith(extractor);
				}
				catch (EncodingChangeException e) {
					// The encoding found in the page didn't match the specified encoding and resulted in
					// a different intepretation of the content; try reparsing with the encountered
					// encoding. The parser has internally already switched to the new encoding, no need
					// to set it ourselves.
					parser.reset();
					extractor.reset();
					parser.visitAllNodesWith(extractor);
				}
			}
			catch (ParserException e) {
				throw new ExtractorException(e);
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new ExtractorException(e);
		}
	}

	/**
	 * A NodeVisitor specialization that is able to start all over with interpreting parsing events.
	 */
	public static class ContentExtractor extends NodeVisitor {

        private static final String XMP = "XMP";

        private static final String PLAINTEXT = "PLAINTEXT";

        private static final String STYLE = "STYLE";

        private static final String SCRIPT = "SCRIPT";

        private static final String TITLE = "TITLE";

        /**
         * Flag indicating whether we're currently in a text context. Only text that is found in text
         * contexts will be included in the extracted text. This excludes text between STYLE or SCRIPT
         * tags.
         */
        private boolean inTextContext;

        /**
         * Flag indicating whether we're currently in a title context.
         */
        private boolean inTitleContext;

        /**
         * Flag indicating that a piece of text should be decoded. Decoding means that any entity
         * references will be resolved and any special characters will be replaced by normal characters.
         */
        private boolean decodeText;

        /**
         * Buffer that temporarily contains the extracted text.
         */
        private StringBuilder textBuffer = new StringBuilder(32 * 1024);

        /**
         * Buffer that temporarily contains keywords found in the meta element.
         */
        private HashSet keywordBuffer = new HashSet();

        // temporary pointers to extracted metadata
        private String title;

        private String author;

        private String description;

        public ContentExtractor() {
            initFlags();
        }

        private void initFlags() {
            inTextContext = true;
            inTitleContext = false;
            decodeText = true;
        }

        /**
         * Remove all extracted information so that the ContentExtractor can be used anew. 
         */
        public void reset() {
            initFlags();
            textBuffer.setLength(0);
            keywordBuffer.clear();
            title = null;
            author = null;
            description = null;
        }
        
        /**
         * Return the extracted full-text, if any.
         */
        public String getText() {
        	return textBuffer.toString();
        }

        /**
         * Return the extracted meta keywords, if any.
         */
        public Iterator getKeywords() {
        	return keywordBuffer.iterator();
        }

        /**
         * Return the extracted title, if any.
         */
        public String getTitle() {
        	return title;
        }
        
        /**
         * Return the extracted author, if any. 
         */
        public String getAuthor() {
        	return author;
        }
        
        /**
         * Return the extracted description, if any.
         */
        public String getDescription() {
        	return description;
        }

        public void visitStringNode(Text node) {
            if (inTitleContext) {
                title = resolveText(node.getText());
                if (title != null) {
                    title = title.trim();
                }
            }

            if (inTextContext) {
                String text = node.getText();
                if (decodeText) {
                    text = resolveText(text);
                }

                textBuffer.append(text);

                // All white space in the document is also reported. However, we don't want to include
                // knowledge about HTML block elements, so we add an extra space for safety.
                textBuffer.append(' ');
            }
        }

        private String resolveText(String text) {
            text = Translate.decode(text);

            // &nbsp; entities are decoded to \u00a0, we replace them with regular spaces
            text = text.replace('\u00a0', ' ');

            return text;
        }

        public void visitTag(Tag tag) {
            String tagName = tag.getTagName();

            if (STYLE.equals(tagName) || SCRIPT.equals(tagName)) {
                // disable text extraction inside these elements
                inTextContext = false;
            }
            else {
                // reenable text extraction
                inTextContext = true;

                // see if we are in a title context
                inTitleContext = TITLE.equals(tagName);

                if (tag instanceof MetaTag) {
                    // handle metadata
                    MetaTag metaTag = (MetaTag) tag;
                    String metaTagName = metaTag.getMetaTagName();
                    String metaTagContent = metaTag.getMetaContent();

                    if (metaTagName != null && metaTagContent != null) {
                        metaTagName = metaTagName.toLowerCase();

                        if (metaTagName.equals("author")) {
                            author = metaTagContent;
                        }
                        else if (metaTagName.equals("description")) {
                            description = metaTagContent;
                        }
                        else if (metaTagName.equals("keywords")) {
                            StringTokenizer tokenizer = new StringTokenizer(metaTagContent, " ,\t", false);
                            while (tokenizer.hasMoreTokens()) {
                                String keyword = tokenizer.nextToken();
                                if (keyword != null) {
                                    keywordBuffer.add(keyword);
                                }
                            }
                        }
                    }
                }
                else if (XMP.equals(tagName) || PLAINTEXT.equals(tagName)) {
                    // disable decoding of entities etc. inside these elements
                    decodeText = false;
                }
            }
        }

        public void visitEndTag(Tag tag) {
            inTitleContext = false;

            String tagName = tag.getTagName();
            if (XMP.equals(tagName) || PLAINTEXT.equals(tagName)) {
                decodeText = true;
            }
        }
	}
}
