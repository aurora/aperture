/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf.sesame;

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