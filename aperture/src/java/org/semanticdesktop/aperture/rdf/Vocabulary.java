/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary defines a comprehensive list of URIs to be used in communication with various Aperture
 * components.
 */
public class Vocabulary {
   
    public static final String NS = "http://aperture.semanticdesktop.org/ontology/data#";
    
    public static final String FULL_TEXT = NS + "full-text";
    public static final String MIME_TYPE = NS + "mimeType";
    public static final String CREATOR = NS + "creator";  // author, artist, etc. NOT: generating application
    public static final String TITLE = NS + "title";
    public static final String SUBJECT = NS + "subject";
    public static final String DESCRIPTION = NS + "description";
    public static final String KEYWORD = NS + "keyword";
    public static final String DATE = NS + "date";   // the general "date", typically last modification or publication date
    public static final String CREATION_DATE = NS + "creationDate";
    public static final String PRINT_DATE = NS + "printDate";
    public static final String LANGUAGE = NS + "language";
    public static final String PAGE_COUNT = NS + "pageCount";
    public static final String GENERATOR = NS + "generator"; // the application that created the file
    
    static {
        ValueFactory factory = new ValueFactoryImpl();
        FULL_TEXT_URI = factory.createURI(FULL_TEXT);
        MIME_TYPE_URI = factory.createURI(MIME_TYPE);
        CREATOR_URI = factory.createURI(CREATOR);
        TITLE_URI = factory.createURI(TITLE);
        SUBJECT_URI = factory.createURI(SUBJECT);
        DESCRIPTION_URI = factory.createURI(DESCRIPTION);
        KEYWORD_URI = factory.createURI(KEYWORD);
        DATE_URI = factory.createURI(DATE);
        CREATION_DATE_URI = factory.createURI(CREATION_DATE);
        PRINT_DATE_URI = factory.createURI(PRINT_DATE);
        LANGUAGE_URI = factory.createURI(LANGUAGE);
        PAGE_COUNT_URI = factory.createURI(PAGE_COUNT);
        GENERATOR_URI = factory.createURI(GENERATOR);
    }
    
    public static final URI FULL_TEXT_URI;
    public static final URI MIME_TYPE_URI;
    public static final URI CREATOR_URI;
    public static final URI TITLE_URI;
    public static final URI SUBJECT_URI;
    public static final URI DESCRIPTION_URI;
    public static final URI KEYWORD_URI;
    public static final URI DATE_URI;
    public static final URI CREATION_DATE_URI;
    public static final URI PRINT_DATE_URI;
    public static final URI LANGUAGE_URI;
    public static final URI PAGE_COUNT_URI;
    public static final URI GENERATOR_URI;

    private Vocabulary() {
    	// prevents instantiation of this class
    }
}
