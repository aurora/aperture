/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.Literal;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.XMLSchema;


/**
 * This class contains additional constants that cannot be represented in RDF.
 * ATM, this means Literal constants for use with the datasource vocabulary. 
 * 
 * @author grimnes
 * $Id$
 */
public class DATASOURCE extends DATASOURCE_GEN {
	
	public static final Literal STARTS_WITH = new LiteralImpl("startsWith", XMLSchema.STRING);

    public static final Literal ENDS_WITH = new LiteralImpl("endsWith", XMLSchema.STRING);

    public static final Literal CONTAINS = new LiteralImpl("contains", XMLSchema.STRING);

    public static final Literal DOES_NOT_CONTAIN = new LiteralImpl("doesNotContain", XMLSchema.STRING);
    
    public static final Literal PLAIN = new LiteralImpl("plain", XMLSchema.STRING);
    
    public static final Literal SSL = new LiteralImpl("ssl", XMLSchema.STRING);
}

