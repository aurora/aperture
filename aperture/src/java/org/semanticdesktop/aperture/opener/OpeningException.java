/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.opener;

/**
 * thrown when opening failed because of third-party reasons.
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class OpeningException extends Exception {

    public OpeningException() {
        super();
    }

    public OpeningException(String message) {
        super(message);
    }

    public OpeningException(Throwable cause) {
        super(cause);
    }

    public OpeningException(String message, Throwable cause) {
        super(message, cause);
    }

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */