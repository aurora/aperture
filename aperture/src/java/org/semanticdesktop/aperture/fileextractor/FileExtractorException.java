/*
 * Copyright (c) 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor;

/**
 * Thrown to indicate that an error occurred while extracting information from a file by a FileExtractor. A
 * typical use case of this exception is for reporting parse errors.
 */
public class FileExtractorException extends Exception {

    /** the serial version UID */
    private static final long serialVersionUID = -4789105838637837413L;

    /**
     * Constructs a FileExtractorException with no detail message.
     */
    public FileExtractorException() {
        super();
    }

    /**
     * Constructs a FileExtractorException with the specified detail message.
     * 
     * @param msg The detail message.
     */
    public FileExtractorException(String msg) {
        super(msg);
    }

    /**
     * Constructs a FileExtractorException with the specified detail message and cause.
     * 
     * Note that the detail message associated with the cause is not automatically incorporated in this
     * exception's detail message.
     * 
     * @param msg The detail message.
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A null
     *            value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public FileExtractorException(String msg, Throwable source) {
        super(msg, source);
    }

    /**
     * Constructs a FileExtractorException with the specified cause and a detail message of <tt>(cause==null ?
     * null : cause.toString())</tt>
     * (which typically contains the class and detail message of cause). This constructor is useful for
     * exceptions that are little more than wrappers for other throwables.
     * 
     * @param source The cause, which is saved for later retrieval by the Throwable.getCause() method. A null
     *            value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public FileExtractorException(Throwable source) {
        super(source);
    }
}
