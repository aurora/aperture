/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

/**
 * thrown when the binary resource could not be found
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class UrlNotFoundException extends Exception {

    public UrlNotFoundException() {
        super();
    }

    public UrlNotFoundException(String message) {
        super(message);
    }

    public UrlNotFoundException(Throwable cause) {
        super(cause);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */