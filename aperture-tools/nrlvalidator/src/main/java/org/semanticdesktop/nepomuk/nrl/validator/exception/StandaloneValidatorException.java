package org.semanticdesktop.nepomuk.nrl.validator.exception;

/**
 * An exception that signifies an error in the standalone validator.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class StandaloneValidatorException extends Exception {

    /** serial version UID */
    private static final long serialVersionUID = 7809146897489770917L;
    
    /**
     * Constructor accepting a string.
     * @param msg
     */
    public StandaloneValidatorException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor accepting a cause
     * @param cause the cause.
     */
    public StandaloneValidatorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor accepting a message and a cause.
     * @param msg a message
     * @param cause a cause
     */
    public StandaloneValidatorException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
