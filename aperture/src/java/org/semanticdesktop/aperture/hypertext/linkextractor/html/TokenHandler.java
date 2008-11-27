/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

/**
 * The TokenHandler interface should be implemented by classes that want to receives tokens from a
 * Tokenizer.
 */
public interface TokenHandler {

    /**
     * Notification of the start of a new document.
     */
    public void startDocument();

    /**
     * Notification of the end of a document.
     */
    public void endDocument();

    /**
     * Notification of the start of a start tag.
     * 
     * @param name The tag name.
     */
    public void startOfStartTag(String name);

    /**
     * Notification of the end of a start tag.
     */
    public void endOfStartTag();

    /**
     * Notification of an end tag.
     * 
     * @param name The tag name.
     */
    public void endTag(String name);

    /**
     * Notification of an attribute for the most recently reported element. The reported attribute does
     * not have a value.
     * 
     * @param name The name of the attribute.
     */
    public void attribute(String name);

    /**
     * Notification of an attribute for the most recently reported element.
     * 
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    public void attribute(String name, String value);

    /**
     * Notification of text.
     * 
     * @param text the text.
     */
    public void text(String text);

    /**
     * Notification of comment.
     * 
     * @param comment The comment.
     */
    public void comment(String comment);

    /**
     * Notification of a processing instruction.
     * 
     * @param name The type name, e.g. HTML.
     * @param sysId The system id, e.g. PUBLIC or SYSTEM.
     * @param fpi The Formal Public Identifier, e.g. "-//W3C//DTD HTML 4.0 Transitional//EN".
     * @param url The URL of the DTD, e.g. "http://www.w3.org/TR/REC-html40/loose.dtd".
     */
    public void docType(String name, String sysId, String fpi, String url);

    /**
     * Notification of a detected error.
     * 
     * @param message An error message.
     */
    public void error(String message);
}