/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.io.IOException;
import java.io.InputStream;

/**
 * Tokenizer is a speed-optimized tokenizer for HTML(-like) documents. It reads documents from
 * InputStreams and supplies the tokens it parses to a TokenHandler.
 */
public class Tokenizer {

    /** The decimal value of a '&lt;'. * */
    private static final int LESS_THEN = (int) '<';

    /** The decimal value of a '&gt;'. * */
    private static final int GREATER_THEN = (int) '>';

    /** The decimal value of a '='. * */
    private static final int EQUALS = (int) '=';

    /** The decimal value of a '!'. * */
    private static final int EXCLAMATION = (int) '!';

    /** The decimal value of a '-'. * */
    private static final int MINUS = (int) '-';

    /** The decimal value of a '"'. * */
    private static final int DOUBLE_QUOTE = (int) '"';

    /** The decimal value of a "'". * */
    private static final int QUOTE = (int) '\'';

    /** The decimal value of a '/'. * */
    private static final int SLASH = (int) '/';

    /** A character array containing the word "DOCTYPE". * */
    private static final char DOCTYPE[] = { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };

    /**
     * The token handler to supply the tokens to.
     */
    private TokenHandler tokenHandler;

    /**
     * Creates a new Tokenizer.
     * 
     * @param tokenHandler A TokenHandler that will handle the parsed tokens.
     */
    public Tokenizer(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    /**
     * Reads the entire contents of the supplied stream and tokenizes it. The parsed tokens are supplied
     * to the token handler.
     * 
     * @param input The stream to read.
     * @exception IOException If an I/O error occurs.
     */
    public void read(InputStream input) throws IOException {
        tokenHandler.startDocument();

        int b = input.read();
        while (b != -1) {
            if (b == LESS_THEN) {
                b = readTag(input);
            }
            else {
                b = readText(input, b);
            }
        }

        tokenHandler.endDocument();
    }

    /**
     * Reads a "tag" (anything between '&lt;' and '&gt;') from the supplied stream and supplies it to the
     * token handler. The first '&lt;' is assumed to have been read already. The method returns the
     * character directly after the characters that are part of the tag.
     * 
     * @param input The stream to read from.
     * @return The first byte after the '&gt;' or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readTag(InputStream input) throws IOException {
        int b = input.read();

        if (b == SLASH) {
            b = readEndTag(input);
        }
        else if (b == EXCLAMATION) {
            b = readDocTypeOrComment(input);
        }
        else {
            b = readStartTag(input, b);
        }

        return b;
    }

    /**
     * Reads a start tag from the supplied stream and supplies the token to the token handler. The first
     * '&lt;' and the character following that are assumed to have been read already. The latter should
     * be supplied as an argument. The method returns the character directly after the start tag.
     * 
     * @param input The stream to read from.
     * @param firstByte The first byte after the '&lt;'.
     * @return The first byte after the '&gt;' or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readStartTag(InputStream input, int firstByte) throws IOException {
        StringBuilder buffer = new StringBuilder();

        int b = firstByte;
        while (b != GREATER_THEN && b != -1 && !Character.isWhitespace((char) b)) {
            buffer.append((char) b);
            b = input.read();
        }

        if (b == -1) {
            return -1;
        }

        tokenHandler.startOfStartTag(buffer.toString());

        b = readAttributes(input, b);

        tokenHandler.endOfStartTag();

        return b;
    }

    /**
     * Reads an end tag from the stream and supplies the token to the token handler. The first two bytes
     * (a '&lt;' and a '/') are assumed to have been read already. The method returns the character
     * directly after the end tag.
     * 
     * @param input The stream to read from.
     * @return The first byte after the '&gt;' or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readEndTag(InputStream input) throws IOException {
        StringBuffer buffer = new StringBuffer();

        int b = input.read();
        while (b != GREATER_THEN && b != -1) {
            buffer.append((char) b);
            b = input.read();
        }

        if (b != -1) {
            tokenHandler.endTag(buffer.toString());
            b = input.read();
        }

        return b;
    }

    /**
     * Reads a document type definition or a comment from the supplied stream and returns the token to
     * the token handler. The first two bytes (a '&lt;' and a '!') are assumed to have been read already.
     * If the parsed token appears not te be a document type definition or comment after all, it is
     * assumed to be a start tag.
     * 
     * @param input The stream to read from.
     * @return The first byte after the '&gt;' or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readDocTypeOrComment(InputStream input) throws IOException {
        StringBuffer buffer = new StringBuffer();

        boolean isDocType = true;

        int b = -1;
        for (int i = 0; i < DOCTYPE.length; i++) {
            b = input.read();
            if (b != -1) {
                if (Character.toUpperCase((char) b) != DOCTYPE[i]) {
                    isDocType = false;
                    break;
                }
            }
            else {
                isDocType = false;
                break;
            }
        }

        if (isDocType) {
            b = input.read();

            // read name
            StringBuilder nameBuffer = new StringBuilder();
            b = readWord(nameBuffer, input, b, false);

            // read id
            StringBuilder sysIdBuffer = new StringBuilder();
            b = readWord(sysIdBuffer, input, b, false);

            // read FPI
            StringBuilder fpiBuffer = new StringBuilder();
            b = readWord(fpiBuffer, input, b, false);

            // read URI
            StringBuilder uriBuffer = new StringBuilder();
            b = readWord(uriBuffer, input, b, false);

            tokenHandler.docType(nameBuffer.toString(), sysIdBuffer.toString(), fpiBuffer.toString(),
                    uriBuffer.toString());
        }
        else {
            // Not a doctype, maybe it's comment
            int firstB = b;
            if (firstB != GREATER_THEN && firstB != -1) {
                b = input.read();
            }
            else {
                b = -1;
            }

            if (firstB == MINUS && b == MINUS) {
                // two minus signs have been read, the comment is next
                boolean minusRead = false;

                b = input.read();
                while (b != -1) {
                    if (b == MINUS) {
                        if (minusRead) {
                            // two minus symbols read
                            b = input.read();
                            break;
                        }
                        else {
                            minusRead = true;
                            b = input.read();
                        }
                    }
                    else {
                        if (minusRead) {
                            buffer.append((char) MINUS);
                            minusRead = false;
                        }
                        buffer.append((char) b);
                        b = input.read();
                    }
                }

                tokenHandler.comment(buffer.toString());
            }
            else {
                buffer.append((char) EXCLAMATION);
                if (firstB != GREATER_THEN && firstB != -1) {
                    buffer.append((char) firstB);
                }

                while (b != GREATER_THEN && b != -1) {
                    buffer.append((char) b);
                    b = input.read();
                }

                tokenHandler.startOfStartTag(buffer.toString());
                tokenHandler.endOfStartTag();
            }
        }

        // skip until '>'
        while (b != GREATER_THEN && b != -1) {
            b = input.read();
        }

        if (b != -1) {
            b = input.read();
        }

        return b;
    }

    /**
     * Reads the attributes of a start tag from the supplied stream and supplies them as tokens to the
     * token handler. The supplied byte should be considered as the first byte to read. This method stops
     * reading at an '&gt;'. The method returns the character directly after the '&gt;'.
     * 
     * @param input The stream to read from.
     * @return The first byte after the '&gt;' or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readAttributes(InputStream input, int firstByte) throws IOException {
        StringBuilder nameBuffer;
        StringBuilder valueBuffer;

        int b = firstByte;
        while (b != GREATER_THEN && b != -1) {
            nameBuffer = new StringBuilder();

            b = readWord(nameBuffer, input, b, true);

            if (nameBuffer.length() != 0) {
                b = skipWhitespace(input, b);

                if (b == EQUALS) {
                    valueBuffer = new StringBuilder();

                    b = input.read();
                    b = readWord(valueBuffer, input, b, false);

                    tokenHandler.attribute(nameBuffer.toString(), valueBuffer.toString());
                }
                else {
                    tokenHandler.attribute(nameBuffer.toString());
                }
                valueBuffer = new StringBuilder();
            }
            else {
                if (b == EQUALS) {
                    // error: attributeNAME is '='
                    tokenHandler.error("Illegal attribute name '=', skipping character");
                    b = input.read();
                }
            }
        }

        if (b != -1) {
            b = input.read();
        }
        return b;
    }

    /**
     * Reads text from the stream and supplies the token to the token handler. The first character is
     * assumed to have been read already and to be supplied as an argument. The method returns the
     * character directly after the text (this is probably an '&lt;').
     * 
     * @param input The stream to read from.
     * @param firstByte The first byte of the text.
     * @return The first byte after the text or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readText(InputStream input, int firstByte) throws IOException {
        StringBuilder buffer = new StringBuilder();

        int b = firstByte;
        while (b != LESS_THEN && b != -1) {
            buffer.append((char) b);
            b = input.read();
        }

        tokenHandler.text(buffer.toString());

        return b;
    }

    /**
     * Skips all white space characters, starting with the supplied 'firstByte'. The first non-whitespace
     * character is returned. If the supplied first byte is a non-whitespace character then that
     * character will be returned.
     * 
     * @param input The stream to read from.
     * @return The first non-whitespace character or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int skipWhitespace(InputStream input, int firstByte) throws IOException {
        int b = firstByte;

        while (b != -1 && Character.isWhitespace((char) b)) {
            b = input.read();
        }

        return b;
    }

    /**
     * Reads a word from the stream and places it in the supplied buffer. 'firstByte' is assumed to be
     * the byte to start with. This method reads a "word". The word returned starts with the first
     * non-whitespace character and ends on an EOF, an '&gt;', an '=' or a whitespace character. The word
     * can also be surrounded by quotation marks ('"'). In that case, any whitespace characters between
     * the quotation marks are also assumed to be part of the word. The quotation marks themselves are
     * not returned as part of the word. The method returns the byte directly after the word (or directly
     * after the quotation mark).
     * 
     * @param word A StringBuffer to place the word in.
     * @param input The stream to read from.
     * @param firstByte The first byte to read.
     * @return The byte directly after the read word or -1 if the end of the stream has been reached.
     * @exception IOException If an I/O error occurs.
     */
    private int readWord(StringBuilder word, InputStream input, int firstByte, boolean stopOnEquals)
            throws IOException {
        int b = firstByte;

        // skip whitespace
        b = skipWhitespace(input, b);

        // read the word
        if (b == DOUBLE_QUOTE || b == QUOTE) {
            // remember the quote type
            int endQuote = b;

            // read until next quote
            b = input.read();
            while (b != endQuote && b != -1) {
                word.append((char) b);
                b = input.read();
            }
            if (b == endQuote) {
                b = input.read();
            }
        }
        else {
            // read until white space
            while ((b != EQUALS || !stopOnEquals) && b != GREATER_THEN && b != -1
                    && !Character.isWhitespace((char) b)) {
                word.append((char) b);
                b = input.read();
            }
        }

        return b;
    }
}
