package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary used by the ModelAccessData class.
 */
public interface MAD {
  /** The namespace for NIE */
    
    public static final String NS_MAD_STRING = "http://aperture.sourceforge.net/2007/07/19/mad#";
    
	public static final URI NS_MAD = new URIImpl(NS_MAD_STRING);

    public static final URI linksTo = new URIImpl(NS_MAD_STRING + "linksTo");
    
    public static final URI redirectsTo = new URIImpl(NS_MAD_STRING + "redirectsTo");
    
    public static final URI dateAsNumber = new URIImpl(NS_MAD_STRING + "dateAsNumber");
    
    public static final URI byteSize = new URIImpl(NS_MAD_STRING + "byteSize");
}
