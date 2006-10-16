package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Contains vocabulary elements that weren't extracted by the VocabularyWriter.
 */
public class ICALTZD extends ICALTZD_GEN {
    /**
     * The uri for the DATE-TIME datatype.
     */
    public static URI Value_DATETIME 
            = new URIImpl("" + ICALTZD.NS + "Value_DATE-TIME");
    
    public static final URI bymonthday 
            = new URIImpl("" + ICALTZD.NS + "bymonthday");
    
    public static final URI realBlankNodes 
            = new URIImpl(DATASOURCE_GEN.NS + "realBlankNodes");
}
