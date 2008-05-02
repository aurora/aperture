package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary used by the ModelAccessData class.
 * 
 * @deprecated these constants have been moved to the ModelAccessData class itself, this class is NOT USED
 *             anymore, PLEASE USE ModelAccessData.linksTo, .redirectsTo etc.
 */
public interface MAD {

    public static final String NS_MAD_STRING = "http://aperture.sourceforge.net/2007/07/19/mad#";

    public static final URI NS_MAD = new URIImpl(NS_MAD_STRING);

    public static final URI linksTo = new URIImpl(NS_MAD_STRING + "linksTo");

    public static final URI redirectsTo = new URIImpl(NS_MAD_STRING + "redirectsTo");

    public static final URI dateAsNumber = new URIImpl(NS_MAD_STRING + "dateAsNumber");

    public static final URI byteSize = new URIImpl(NS_MAD_STRING + "byteSize");
}
