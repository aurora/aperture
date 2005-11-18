/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * This interface defines a comprehensive list of URIs to be used for modeling DataObject metadata.
 */
public interface Vocabulary {

    public static final String NS = "http://aperture.semanticdesktop.org/ontology/data/";

    public static final URI FULL_TEXT = new URIImpl(NS + "fullText");

    public static final URI MIME_TYPE = new URIImpl(NS + "mimeType");

    public static final URI BYTE_SIZE = new URIImpl(NS + "byteSize");

    // author, artist, etc. NOT: generating application
    public static final URI CREATOR = new URIImpl(NS + "creator");

    public static final URI TITLE = new URIImpl(NS + "title");

    public static final URI SUBJECT = new URIImpl(NS + "subject");

    // e.g. file names, folder names, attachment names
    public static final URI NAME = new URIImpl(NS + "name");

    public static final URI DESCRIPTION = new URIImpl(NS + "description");

    public static final URI KEYWORD = new URIImpl(NS + "keyword");

    // the general "date", typically last modification or publication date
    public static final URI DATE = new URIImpl(NS + "date");

    public static final URI CREATION_DATE = new URIImpl(NS + "creationDate");

    public static final URI PRINT_DATE = new URIImpl(NS + "printDate");

    public static final URI LANGUAGE = new URIImpl(NS + "language");

    public static final URI PAGE_COUNT = new URIImpl(NS + "pageCount");

    // the application that created the file
    public static final URI GENERATOR = new URIImpl(NS + "generator");

    // nesting of files or folders in folders, nesting of attachments in mails, etc.
    public static final URI PART_OF = new URIImpl(NS + "partOf");
}
