/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 * This interface defines a comprehensive list of URIs to be used for modeling DataSource configurations.
 */
public interface SourceVocabulary {

    /* Namespaces */

    public static final String NS = "http://aperture.semanticdesktop.org/ontology/source/";

    /* Classes */

    public static final URI DATA_SOURCE = new URIImpl(NS + "DataSource");

    public static final URI FILE_SYSTEM_DATA_SOURCE = new URIImpl(NS + "FileSystemDataSource");

    public static final URI WEB_DATA_SOURCE = new URIImpl(NS + "WebDataSource");
    
    public static final URI IMAP_DATA_SOURCE = new URIImpl(NS + "IMAPDataSource");
    
    public static final URI PATTERN = new URIImpl(NS + "Pattern");

    public static final URI REGEXP_PATTERN = new URIImpl(NS + "RegExpPattern");

    public static final URI SUBSTRING_PATTERN = new URIImpl(NS + "SubstringPattern");

    /* Properties */

    public static final URI ROOT_URL = new URIImpl(NS + "rootUrl");

    public static final URI PASSWORD = new URIImpl(NS + "password");
    
    public static final URI CONNECTION_SECURITY = new URIImpl(NS + "connectionSecurity");
    
    public static final URI MAXIMUM_DEPTH = new URIImpl(NS + "maximumDepth");

    public static final URI MAXIMUM_BYTE_SIZE = new URIImpl(NS + "maximumSize");
    
    public static final URI INCLUDE_PATTERN = new URIImpl(NS + "includePattern");

    public static final URI EXCLUDE_PATTERN = new URIImpl(NS + "excludePattern");

    public static final URI CONDITION = new URIImpl(NS + "condition");

    public static final URI INCLUDE_HIDDEN_RESOURCES = new URIImpl(NS + "includeHiddenResources");
    
    public static final URI INCLUDE_EMBEDDED_RESOURCES = new URIImpl(NS + "includeEmbeddedResources");

    /* Literals */

    public static final Literal STARTS_WITH = new LiteralImpl("startsWith", XMLSchema.STRING);

    public static final Literal ENDS_WITH = new LiteralImpl("endsWith", XMLSchema.STRING);

    public static final Literal CONTAINS = new LiteralImpl("contains", XMLSchema.STRING);

    public static final Literal DOES_NOT_CONTAIN = new LiteralImpl("doesNotContain", XMLSchema.STRING);
    
    public static final Literal PLAIN = new LiteralImpl("plain", XMLSchema.STRING);
    
    public static final Literal SSL = new LiteralImpl("ssl", XMLSchema.STRING);
}
