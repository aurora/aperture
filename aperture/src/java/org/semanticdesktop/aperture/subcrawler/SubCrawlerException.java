/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

/**
 * Thrown to indicate that an error occurred while extracting information from an InputStream by a
 * SubCrawler. A typical use case of this exception is for reporting parse errors.
 */
public class SubCrawlerException extends Exception {

    /** the serial version uid */
    private static final long serialVersionUID = 5850947385737157681L;

    /**
     * Constructs an SubCrawlerException with no detail message.
     */
    public SubCrawlerException() {
        super();
    }

    /**
     * Constructs a SubCrawlerException with the specified detail message.
     * 
     * @param msg The detail message.
     */
    public SubCrawlerException(String msg) {
        super(msg);
    }

    /**
     * Constructs an SubCrawlerException with the specified detail message and cause.
     * 
     * Note that the detail message associated with the cause is not automatically incorporated in this
     * exception's detail message.
     * 
     * @param msg The detail message.
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public SubCrawlerException(String msg, Throwable source) {
        super(msg, source);
    }

    /**
     * Constructs a SubCrawlerException with the specified cause and a detail message of <tt>(cause==null ?
     * null : cause.toString())</tt> (which typically contains the class and detail message of cause).
     * This constructor is useful for exceptions that are little more than wrappers for other throwables.
     * 
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public SubCrawlerException(Throwable source) {
        super(source);
    }
}
