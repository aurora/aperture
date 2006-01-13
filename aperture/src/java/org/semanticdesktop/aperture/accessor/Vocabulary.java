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

    // Key used to store the MIME type of the content of an object when it is different from the object's
    // main MIME type. This value can be used, for example, to model an e-mail message whose mime type is
    // "message/rfc822", but whose content has type "text/html". If not specified, the MIME type of the
    // content defaults to the value specified by the MIME_TYPE key.
    public static final URI CONTENT_MIME_TYPE = new URIImpl(NS + "contentMimeType");

    public static final URI CHARACTER_SET = new URIImpl(NS + "characterSet");

    public static final URI BYTE_SIZE = new URIImpl(NS + "byteSize");

    // author, artist, etc. NOT: generating application
    public static final URI CREATOR = new URIImpl(NS + "creator");

    public static final URI FROM = new URIImpl(NS + "from");

    public static final URI TO = new URIImpl(NS + "to");

    public static final URI CC = new URIImpl(NS + "cc");

    public static final URI BCC = new URIImpl(NS + "bcc");
    
    public static final URI TITLE = new URIImpl(NS + "title");

    public static final URI SUBJECT = new URIImpl(NS + "subject");

    // e.g. file names, folder names, attachment names
    public static final URI NAME = new URIImpl(NS + "name");

    public static final URI DESCRIPTION = new URIImpl(NS + "description");

    public static final URI KEYWORD = new URIImpl(NS + "keyword");

    // the general "date", typically last modification or publication date
    public static final URI DATE = new URIImpl(NS + "date");

    public static final URI CREATION_DATE = new URIImpl(NS + "creationDate");

    public static final URI RETRIEVAL_DATE = new URIImpl(NS + "retrievalDate");
    
    public static final URI EXPIRATION_DATE = new URIImpl(NS + "expirationDate");
    
    public static final URI PRINT_DATE = new URIImpl(NS + "printDate");

    public static final URI LANGUAGE = new URIImpl(NS + "language");

    public static final URI PAGE_COUNT = new URIImpl(NS + "pageCount");

    // the application that created the file
    public static final URI GENERATOR = new URIImpl(NS + "generator");

    // nesting of files or folders in folders, nesting of attachments in mails, etc.
    public static final URI PART_OF = new URIImpl(NS + "partOf");
    
    public static final URI EMAIL_ADDRESS = new URIImpl(NS + "emailAddress");
}
