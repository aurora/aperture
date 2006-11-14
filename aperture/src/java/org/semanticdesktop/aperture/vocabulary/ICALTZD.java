package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Contains vocabulary elements that weren't extracted by the VocabularyWriter.
 */
public class ICALTZD extends ICALTZD_GEN {
    /**
     * The uri for the DATE-TIME datatype.
     */
    public static URI Value_DATETIME 
            = URIImpl.createURIWithoutChecking("" + ICALTZD.NS + "Value_DATE-TIME");
    
    public static final URI bymonthday 
            = URIImpl.createURIWithoutChecking("" + ICALTZD.NS + "bymonthday");
    
    public static final URI realBlankNodes 
            = URIImpl.createURIWithoutChecking(DATASOURCE_GEN.NS + "realBlankNodes");
}
