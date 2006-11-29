package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 29 15:23:45 CET 2006
 * input file: doc/ontology/dcq.rdfs
 * namespace: http://purl.org/dc/terms/
 */
public interface DCTERMS_GEN {
	public static final String NS_DCTERMS_GEN = "http://purl.org/dc/terms/";

    /**
     * Label: Subject Encoding Schemes (en-us) 
     * Comment: A set of subject encoding schemes and/or formats (en-us) 
     */
    public static final URI SubjectScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/SubjectScheme");

    /**
     * Label: Date Encoding Schemes (en-us) 
     * Comment: A set of date encoding schemes and/or formats (en-us) 
     */
    public static final URI DateScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/DateScheme");

    /**
     * Label: Format Encoding Schemes (en-us) 
     * Comment: A set of format encoding schemes. (en-us) 
     */
    public static final URI FormatScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/FormatScheme");

    /**
     * Label: Language Encoding Schemes (en-us) 
     * Comment: A set of language encoding schemes and/or formats. (en-us) 
     */
    public static final URI LanguageScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/LanguageScheme");

    /**
     * Label: Place Encoding Schemes (en-us) 
     * Comment: A set of geographic place encoding schemes and/or formats (en-us) 
     */
    public static final URI SpatialScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/SpatialScheme");

    /**
     * Label: Encoding Schemes
      for temporal characteristics (en-us) 
     * Comment: A set of encoding schemes for 
     the coverage qualifier "temporal" (en-us) 
     */
    public static final URI TemporalScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/TemporalScheme");

    /**
     * Label: Resource Type Encoding Schemes (en-us) 
     * Comment: A set of resource type encoding schemes and/or formats (en-us) 
     */
    public static final URI TypeScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/TypeScheme");

    /**
     * Label: Resource Identifier Encoding Schemes (en-us) 
     * Comment: A set of resource identifier encoding schemes and/or formats (en-us) 
     */
    public static final URI IdentifierScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/IdentifierScheme");

    /**
     * Label: Resource Relation Encoding Schemes (en-us) 
     * Comment: A set of resource relation encoding schemes and/or formats (en-us) 
     */
    public static final URI RelationScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/RelationScheme");

    /**
     * Label: Source Encoding Schemes (en-us) 
     * Comment: A set of source encoding schemes and/or formats (en-us) 
     */
    public static final URI SourceScheme = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/SourceScheme");

    /**
     * Label: LCSH (en-us) 
     * Comment: Library of Congress Subject Headings (en-us) 
     */
    public static final URI LCSH = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/LCSH");

    /**
     * Label: MeSH (en-us) 
     * Comment: Medical Subject Headings (en-us) 
     */
    public static final URI MESH = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/MESH");

    /**
     * Label: DDC (en-us) 
     * Comment: Dewey Decimal Classification (en-us) 
     */
    public static final URI DDC = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/DDC");

    /**
     * Label: LCC (en-us) 
     * Comment: Library of Congress Classification (en-us) 
     */
    public static final URI LCC = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/LCC");

    /**
     * Label: UDC (en-us) 
     * Comment: Universal Decimal Classification (en-us) 
     */
    public static final URI UDC = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/UDC");

    /**
     * Label: DCMI Type Vocabulary (en-us) 
     * Comment: A list of types used to categorize the nature or genre 
										of the content of the resource. (en-us) 
     */
    public static final URI DCMIType = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/DCMIType");

    /**
     * Label: IMT (en-us) 
     * Comment: The Internet media type of the resource. (en-us) 
     */
    public static final URI IMT = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/IMT");

    /**
     * Label: ISO 639-2 (en-us) 
     * Comment: ISO 639-2: Codes for the representation of names of languages. (en-us) 
     */
    public static final URI ISO639_2 = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/ISO639-2");

    /**
     * Label: RFC 1766 (en-us) 
     * Comment: Internet RFC 1766 'Tags for the identification of Language' 
										specifies a two letter code taken from ISO 639, followed 
										optionally by a two letter country code taken from ISO 3166. (en-us) 
     */
    public static final URI RFC1766 = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/RFC1766");

    /**
     * Label: URI (en-us) 
     * Comment: A URI Uniform Resource Identifier (en-us) 
     */
    public static final URI URI = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/URI");

    /**
     * Label: DCMI Point (en-us) 
     * Comment: The DCMI Point identifies a point in space using its geographic coordinates. (en-us) 
     */
    public static final URI Point = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/Point");

    /**
     * Label: ISO 3166 (en-us) 
     * Comment: ISO 3166 Codes for the representation of names of countries (en-us) 
     */
    public static final URI ISO3166 = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/ISO3166");

    /**
     * Label: DCMI Box (en-us) 
     * Comment: The DCMI Box identifies a region of space using its geographic limits. (en-us) 
     */
    public static final URI Box = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/Box");

    /**
     * Label: TGN (en-us) 
     * Comment: The Getty Thesaurus of Geographic Names (en-us) 
     */
    public static final URI TGN = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/TGN");

    /**
     * Label: DCMI Period (en-us) 
     * Comment: A specification of the limits of a time interval. (en-us) 
     */
    public static final URI Period = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/Period");

    /**
     * Label: W3C-DTF (en-us) 
     * Comment: W3C Encoding rules for dates and times - a profile based on ISO 8601 (en-us) 
     */
    public static final URI W3CDTF = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/W3CDTF");

    /**
     * Label: RFC 3066 (en-us) 
     * Comment: Internet RFC 3066 'Tags for the Identification of 
		Languages' specifies a primary subtag which
		is a two-letter code taken from ISO 639 part
		1 or a three-letter code taken from ISO 639
		part 2, followed optionally by a two-letter
		country code taken from ISO 3166.  When a
		language in ISO 639 has both a two-letter and
		three-letter code, use the two-letter code;
		when it has only a three-letter code, use the
		three-letter code.  This RFC replaces RFC
		1766. (en-us) 
     */
    public static final URI RFC3066 = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/RFC3066");

    /**
     * Label: NLM (en-us) 
     * Comment: National Library of Medicine Classification (en-us) 
     */
    public static final URI NLM = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/NLM");

    /**
     * Label: Audience (en-us) 
     * Comment: A class of entity for whom the resource is intended or useful. (en-us) 
     */
    public static final URI audience = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/audience");

    /**
     * Label: Alternative (en-us) 
     * Comment: Any form of the title used as a substitute or alternative 
		to the formal title of the resource. (en-us) 
     */
    public static final URI alternative = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/alternative");

    /**
     * Label: Table Of Contents (en-us) 
     * Comment: A list of subunits of the content of the resource. (en-us) 
     */
    public static final URI tableOfContents = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/tableOfContents");

    /**
     * Label: Abstract (en-us) 
     * Comment: A summary of the content of the resource. (en-us) 
     */
    public static final URI abstract_ = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/abstract");

    /**
     * Label: Created (en-us) 
     * Comment: Date of creation of the resource. (en-us) 
     */
    public static final URI created = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/created");

    /**
     * Label: Valid (en-us) 
     * Comment: Date (often a range) of validity of a resource. (en-us) 
     */
    public static final URI valid = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/valid");

    /**
     * Label: Available (en-us) 
     * Comment: Date (often a range) that the resource will become or did 
		become available. (en-us) 
     */
    public static final URI available = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/available");

    /**
     * Label: Issued (en-us) 
     * Comment: Date of formal issuance (e.g., publication) of the resource. (en-us) 
     */
    public static final URI issued = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/issued");

    /**
     * Label: Modified (en-us) 
     * Comment: Date on which the resource was changed. (en-us) 
     */
    public static final URI modified = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/modified");

    /**
     * Label: Extent (en-us) 
     * Comment: The size or duration of the resource. (en-us) 
     */
    public static final URI extent = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/extent");

    /**
     * Label: Medium (en-us) 
     * Comment: The material or physical carrier of the resource. (en-us) 
     */
    public static final URI medium = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/medium");

    /**
     * Label: Is Version Of (en-us) 
     * Comment: The described resource is a version, edition, or adaptation 
		of the referenced resource. Changes in version imply substantive 
		changes in content rather than differences in format. (en-us) 
     */
    public static final URI isVersionOf = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isVersionOf");

    /**
     * Label: Has Version (en-us) 
     * Comment: The described resource has a version, edition, or adaptation, 
		namely, the referenced resource. (en-us) 
     */
    public static final URI hasVersion = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/hasVersion");

    /**
     * Label: Is Replaced By (en-us) 
     * Comment: The described resource is supplanted, displaced, or 
		superseded by the referenced resource. (en-us) 
     */
    public static final URI isReplacedBy = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isReplacedBy");

    /**
     * Label: Replaces (en-us) 
     * Comment: The described resource supplants, displaces, or supersedes 
		the referenced resource. (en-us) 
     */
    public static final URI replaces = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/replaces");

    /**
     * Label: Is Required By (en-us) 
     * Comment: The described resource is required by the referenced resource, 
		either physically or logically. (en-us) 
     */
    public static final URI isRequiredBy = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isRequiredBy");

    /**
     * Label: Requires (en-us) 
     * Comment: The described resource requires the referenced resource to 
		support its function, delivery, or coherence of content. (en-us) 
     */
    public static final URI requires = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/requires");

    /**
     * Label: Is Part Of (en-us) 
     * Comment: The described resource is a physical or logical part of the 
		referenced resource. (en-us) 
     */
    public static final URI isPartOf = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isPartOf");

    /**
     * Label: Has Part (en-us) 
     * Comment: The described resource includes the referenced resource either 
		physically or logically. (en-us) 
     */
    public static final URI hasPart = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/hasPart");

    /**
     * Label: Is Referenced By (en-us) 
     * Comment: The described resource is referenced, cited, or otherwise 
		pointed to by the referenced resource. (en-us) 
     */
    public static final URI isReferencedBy = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isReferencedBy");

    /**
     * Label: References (en-us) 
     * Comment: The described resource references, cites, or otherwise points 
		to the referenced resource. (en-us) 
     */
    public static final URI references = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/references");

    /**
     * Label: Is Format Of (en-us) 
     * Comment: The described resource is the same intellectual content of 
		the referenced resource, but presented in another format. (en-us) 
     */
    public static final URI isFormatOf = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/isFormatOf");

    /**
     * Label: Has Format (en-us) 
     * Comment: The described resource pre-existed the referenced resource, 
		which is essentially the same intellectual content presented 
		in another format. (en-us) 
     */
    public static final URI hasFormat = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/hasFormat");

    /**
     * Label: Conforms To (en-us) 
     * Comment: A reference to an established standard to which the resource conforms. (en-us) 
     */
    public static final URI conformsTo = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/conformsTo");

    /**
     * Label: Spatial (en-us) 
     * Comment: Spatial characteristics of the intellectual content of the resource. (en-us) 
     */
    public static final URI spatial = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/spatial");

    /**
     * Label: Temporal (en-us) 
     * Comment: Temporal characteristics of the intellectual content of the resource. (en-us) 
     */
    public static final URI temporal = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/temporal");

    /**
     * Label: Mediator (en-us) 
     * Comment: A class of entity that mediates access to the
		resource and for whom the resource is intended or useful. (en-us) 
     */
    public static final URI mediator = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/mediator");

    /**
     * Label: Date Accepted (en-us) 
     * Comment: Date of acceptance of the resource (e.g. of thesis
		by university department, of article by journal, etc.). (en-us) 
     */
    public static final URI dateAccepted = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/dateAccepted");

    /**
     * Label: Date Copyrighted (en-us) 
     * Comment: Date of a statement of copyright. (en-us) 
     */
    public static final URI dateCopyrighted = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/dateCopyrighted");

    /**
     * Label: Date Submitted (en-us) 
     * Comment: Date of submission of the resource (e.g. thesis, 
		articles, etc.). (en-us) 
     */
    public static final URI dateSubmitted = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/dateSubmitted");

    /**
     * Label: Audience Education Level (en-us) 
     * Comment: A general statement describing the education or 
		training context.  Alternatively, a more specific 
		statement of the location of the audience in terms of 
		its progression through an education or training context. (en-us) 
     */
    public static final URI educationLevel = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/educationLevel");

    /**
     * Label: Access Rights (en-us) 
     * Comment: Information about who can access the
        resource or an indication of its security status. (en-us) 
     */
    public static final URI accessRights = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/accessRights");

    /**
     * Label: Bibliographic Citation (en-us) 
     * Comment: A bibliographic reference for the resource. (en-us) 
     */
    public static final URI bibliographicCitation = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/bibliographicCitation");

    /**
     * Label: License (en-us) 
     * Comment: A legal document giving official permission to do something
        with the resource. (en-us) 
     */
    public static final URI license = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/license");

    /**
     * Label: Rights Holder (en-us) 
     * Comment: A person or organization owning or managing rights over the resource. (en-us) 
     */
    public static final URI rightsHolder = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/rightsHolder");

    /**
     * Label: Provenance (en-us) 
     * Comment: A statement of any changes in ownership and custody
        of the resource since its creation that are
        significant for its authenticity, integrity and
        interpretation. (en-us) 
     */
    public static final URI provenance = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/provenance");

    /**
     * Label: Instructional Method (en-us) 
     * Comment: A process, used to engender knowledge, attitudes and skills,
                    that the resource is designed to support. (en-us) 
     */
    public static final URI instructionalMethod = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/instructionalMethod");

    /**
     * Label: Accrual Method (en-us) 
     * Comment: The method by which items are added to a collection. (en-us) 
     */
    public static final URI accrualMethod = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/accrualMethod");

    /**
     * Label: Accrual Periodicity (en-us) 
     * Comment: The frequency with which items are added to a collection. (en-us) 
     */
    public static final URI accrualPeriodicity = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/accrualPeriodicity");

    /**
     * Label: Accrual Policy (en-us) 
     * Comment: The policy governing the addition of items to a collection. (en-us) 
     */
    public static final URI accrualPolicy = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/accrualPolicy");

}
