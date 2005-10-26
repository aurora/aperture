/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.datasource;

/**
 * thrown when a DataSource is not configured properly or cannot connect
 * to its underlying data (because of wrong passwords, etc)
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class InitializationException extends Exception {

    public InitializationException() {
        super();
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */