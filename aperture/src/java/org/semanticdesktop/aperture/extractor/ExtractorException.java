/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.extractor;

/**
 * thrown when the metadata of the stream cannot be extracted, when the stream
 * does not conform to the MimeType's norms.
 * 
 * 
 * @author Sauermann $Id$
 */
public class ExtractorException extends Exception {

    public ExtractorException() {
        super();
    }

    public ExtractorException(String message) {
        super(message);
    }

    public ExtractorException(Throwable cause) {
        super(cause);
    }

    public ExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */