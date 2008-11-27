/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.rdf;

/**
 * An UpdateException is thrown by a RDFContainer when a state-changing method cannot alter the state for
 * some reason.
 * 
 * <p>
 * UpdateExceptions often wrap an Exception thrown by the underlying RDF model implementation.
 */
public class UpdateException extends RuntimeException {

    public UpdateException() {
        super();
    }
    
    public UpdateException(String message) {
        super(message);
    }
    
    public UpdateException(Throwable cause) {
        super(cause);
    }
    
    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}