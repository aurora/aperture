/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.vocabulary.XSD;


/**
 * This class contains additional constants that cannot be represented in RDF.
 * ATM, this means Literal constants for use with the datasource vocabulary. 
 * 
 * @author grimnes
 * $Id$
 */
public class DATASOURCE extends DATASOURCE_GEN {

	public static final DatatypeLiteral STARTS_WITH = new DatatypeLiteralImpl("startsWith", XSD._string);

    public static final DatatypeLiteral ENDS_WITH = new DatatypeLiteralImpl("endsWith", XSD._string);

    public static final DatatypeLiteral CONTAINS = new DatatypeLiteralImpl("contains", XSD._string);

    public static final DatatypeLiteral DOES_NOT_CONTAIN = new DatatypeLiteralImpl("doesNotContain", XSD._string);
    
    public static final DatatypeLiteral PLAIN = new DatatypeLiteralImpl("plain", XSD._string);
    
    public static final DatatypeLiteral SSL = new DatatypeLiteralImpl("ssl", XSD._string);
    
    public static final DatatypeLiteral SSL_NO_CERT = new DatatypeLiteralImpl("ssl-no-cert", XSD._string);
}

