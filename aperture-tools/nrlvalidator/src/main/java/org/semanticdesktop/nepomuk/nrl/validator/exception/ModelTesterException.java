package org.semanticdesktop.nepomuk.nrl.validator.exception;

/**
 * Indicates an error that occured while testing a repository.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class ModelTesterException extends Exception {
    
    /** serial version UID */
    private static final long serialVersionUID = 3411358274152309449L;

    /**
     * A constructor accepting a cause.
     * @param cause the cause.
     */
    public ModelTesterException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructor accepting a message.
     * @param msg
     */
    public ModelTesterException(String msg) {
        super(msg);
    }

}
