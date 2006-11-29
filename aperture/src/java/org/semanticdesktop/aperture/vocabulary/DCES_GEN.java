package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 29 15:24:29 CET 2006
 * input file: doc/ontology/dces.rdfs
 * namespace: http://purl.org/dc/elements/1.1/
 */
public interface DCES_GEN {
	public static final String NS_DCES_GEN = "http://purl.org/dc/elements/1.1/";

    /**
     * Label: Title (en-us) 
     * Comment: A name given to the resource. (en-us) 
     */
    public static final URI title = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/title");

    /**
     * Label: Creator (en-us) 
     * Comment: An entity primarily responsible for making the content 
		of the resource. (en-us) 
     */
    public static final URI creator = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/creator");

    /**
     * Label: Subject and Keywords (en-us) 
     * Comment: The topic of the content of the resource. (en-us) 
     */
    public static final URI subject = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/subject");

    /**
     * Label: Description (en-us) 
     * Comment: An account of the content of the resource. (en-us) 
     */
    public static final URI description = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/description");

    /**
     * Label: Publisher (en-us) 
     * Comment: An entity responsible for making the resource available (en-us) 
     */
    public static final URI publisher = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/publisher");

    /**
     * Label: Contributor (en-us) 
     * Comment: An entity responsible for making contributions to the
		content of the resource. (en-us) 
     */
    public static final URI contributor = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/contributor");

    /**
     * Label: Date (en-us) 
     * Comment: A date associated with an event in the life cycle of the
		resource. (en-us) 
     */
    public static final URI date = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/date");

    /**
     * Label: Resource Type (en-us) 
     * Comment: The nature or genre of the content of the resource. (en-us) 
     */
    public static final URI type = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/type");

    /**
     * Label: Format (en-us) 
     * Comment: The physical or digital manifestation of the resource. (en-us) 
     */
    public static final URI format = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/format");

    /**
     * Label: Resource Identifier (en-us) 
     * Comment: An unambiguous reference to the resource within a given context. (en-us) 
     */
    public static final URI identifier = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/identifier");

    /**
     * Label: Source (en-us) 
     * Comment: A reference to a resource from which the present resource
		is derived. (en-us) 
     */
    public static final URI source = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/source");

    /**
     * Label: Language (en-us) 
     * Comment: A language of the intellectual content of the resource. (en-us) 
     */
    public static final URI language = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/language");

    /**
     * Label: Relation (en-us) 
     * Comment: A reference to a related resource. (en-us) 
     */
    public static final URI relation = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/relation");

    /**
     * Label: Coverage (en-us) 
     * Comment: The extent or scope of the content of the resource. (en-us) 
     */
    public static final URI coverage = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/coverage");

    /**
     * Label: Rights Management (en-us) 
     * Comment: Information about rights held in and over the resource. (en-us) 
     */
    public static final URI rights = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/rights");

}
