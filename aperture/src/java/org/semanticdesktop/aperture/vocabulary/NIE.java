/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Oct 28 01:54:50 CET 2008
 * input file: D:\ganymedeworkspace\aperture-trunk/doc/ontology/nie.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#
 */
public class NIE {

    /** Path to the ontology resource */
    public static final String NIE_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/nie.rdfs";

    /**
     * Puts the NIE ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNIEOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NIE_RESOURCE_PATH, NIE.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NIE_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NIE */
    public static final URI NS_NIE = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#");
    /**
     * Type: Class <br/>
     * Label: DataObject  <br/>
     * Comment: A unit of data that is created, annotated and processed on the user desktop. It represents a native structure the user works with. The usage of the term 'native' is important. It means that a DataObject can be directly mapped to a data structure maintained by a native application. This may be a file, a set of files or a part of a file. The granularity depends on the user. This class is not intended to be instantiated by itself. Use more specific subclasses.  <br/>
     */
    public static final URI DataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject");
    /**
     * Type: Class <br/>
     * Label: DataSource  <br/>
     * Comment: A superclass for all entities from which DataObjects can be extracted. Each entity represents a native application or some other system that manages information that may be of interest to the user of the Semantic Desktop. Subclasses may include FileSystems, Mailboxes, Calendars, websites etc. The exact choice of subclasses and their properties is considered application-specific. Each data extraction application is supposed to provide it's own DataSource ontology. Such an ontology should contain supported data source types coupled with properties necessary for the application to gain access to the data sources.  (paths, urls, passwords  etc...)  <br/>
     */
    public static final URI DataSource = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource");
    /**
     * Type: Class <br/>
     * Label: InformationElement  <br/>
     * Comment: A unit of content the user works with. This is a superclass for all interpretations of a DataObject.  <br/>
     */
    public static final URI InformationElement = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement");
    /**
     * Type: Property <br/>
     * Label: generatorOption  <br/>
     * Comment: A common superproperty for all settings used by the generating software. This may include compression settings, algorithms, autosave, interlaced/non-interlaced etc. Note that this property has no range specified and therefore should not be used directly. Always use more specific properties.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     */
    public static final URI generatorOption = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#generatorOption");
    /**
     * Type: Property <br/>
     * Label: generator  <br/>
     * Comment: Software used to "generate" the contents. E.g. a word processor name.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI generator = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#generator");
    /**
     * Type: Property <br/>
     * Label: description  <br/>
     * Comment: A textual description of the resource. This property may be used for any metadata fields that provide some meta-information or comment about a resource in the form of a passage of text. This property is not to be confused with nie:plainTextContent. Use more specific subproperties wherever possible.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI description = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#description");
    /**
     * Type: Property <br/>
     * Label: disclaimer  <br/>
     * Comment: A disclaimer  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI disclaimer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#disclaimer");
    /**
     * Type: Property <br/>
     * Label: legal  <br/>
     * Comment: A common superproperty for all properties that point at legal information about an Information Element  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI legal = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#legal");
    /**
     * Type: Property <br/>
     * Label: dataSource  <br/>
     * Comment: Marks the provenance of a DataObject, what source does a data object come from.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     */
    public static final URI dataSource = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#dataSource");
    /**
     * Type: Property <br/>
     * Label: depends  <br/>
     * Comment: Dependency relation. A piece of content depends on another piece of data in order to be properly understood/used/interpreted.  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI depends = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#depends");
    /**
     * Type: Property <br/>
     * Label: relatedTo  <br/>
     * Comment: A common superproperty for all relations between a piece of content and other pieces of data (which may be interpreted as other pieces of content).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI relatedTo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#relatedTo");
    /**
     * Type: Property <br/>
     * Label: links  <br/>
     * Comment: A linking relation. A piece of content links/mentions a piece of data  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI links = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#links");
    /**
     * Type: Property <br/>
     * Label: copyright  <br/>
     * Comment: Content copyright  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI copyright = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#copyright");
    /**
     * Type: Property <br/>
     * Label: comment  <br/>
     * Comment: A user comment about an InformationElement.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI comment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#comment");
    /**
     * Type: Property <br/>
     * Label: interpretedAs  <br/>
     * Comment: Links the DataObject with the InformationElement it is interpreted as.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     */
    public static final URI interpretedAs = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#interpretedAs");
    /**
     * Type: Property <br/>
     * Label: isStoredAs  <br/>
     * Comment: Links the information element with the DataObject it is stored in.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI isStoredAs = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isStoredAs");
    /**
     * Type: Property <br/>
     * Label: contentLastModified  <br/>
     * Comment: The date of the last modification of the content.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI contentLastModified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentLastModified");
    /**
     * Type: Property <br/>
     * Label: informationElementDate  <br/>
     * Comment: A point or period of time associated with an event in the lifecycle of an Information Element. A common superproperty for all date-related properties of InformationElements in the NIE Framework.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI informationElementDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#informationElementDate");
    /**
     * Type: Property <br/>
     * Label: mimeType  <br/>
     * Comment: The mime type of the resource, if available. Example: "text/plain". See http://www.iana.org/assignments/media-types/. This property applies to data objects that can be described with one mime type. In cases where the object as a whole has one mime type, while it's parts have other mime types, or there is no mime type that can be applied to the object as a whole, but some parts of the content have mime types - use more specific properties.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI mimeType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#mimeType");
    /**
     * Type: Property <br/>
     * Label: plainTextContent  <br/>
     * Comment: Plain-text representation of the content of a InformationElement with all markup removed. The main purpose of this property is full-text indexing and search. Its exact content is considered application-specific. The user can make no assumptions about what is and what is not contained within. Applications should use more specific properties wherever possible.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI plainTextContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#plainTextContent");
    /**
     * Type: Property <br/>
     * Label: version  <br/>
     * Comment: The current version of the given data object. Exact semantics is unspecified at this level. Use more specific subproperties if needed.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI version = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#version");
    /**
     * Type: Property <br/>
     * Label: contentCreated  <br/>
     * Comment: The date of the content creation. This may not necessarily be equal to the date when the DataObject (i.e. the physical representation) itself was created. Compare with nie:created property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI contentCreated = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentCreated");
    /**
     * Type: Property <br/>
     * Label: subject  <br/>
     * Comment: An overall topic of the content of a InformationElement  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#subject");
    /**
     * Type: Property <br/>
     * Label: byteSize  <br/>
     * Comment: The overall size of the data object in bytes. That means the WHOLE data object and ONLY the data object, not of the content that is of interest to the user. For cases where the content size is different (e.g. in compressed files the content is larger, in messages the content excludes headings and is smaller) use more specific properties, not necessarily subproperties of this one.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI byteSize = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#byteSize");
    /**
     * Type: Property <br/>
     * Label: lastRefreshed  <br/>
     * Comment: Date when information about this data object was retrieved (for the first time) or last refreshed from the data source. This property is important for metadata extraction applications that don't receive any notifications of changes in the data source and have to poll it regularly. This may lead to information becoming out of date. In these cases this property may be used to determine the age of data, which is an important element of it's dependability.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI lastRefreshed = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#lastRefreshed");
    /**
     * Type: Property <br/>
     * Label: license  <br/>
     * Comment: Terms and intellectual property rights licensing conditions.  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI license = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#license");
    /**
     * Type: Property <br/>
     * Label: created  <br/>
     * Comment: Date of creation of the DataObject. Note that this date refers to the creation of the DataObject itself (i.e. the physical representation). Compare with nie:contentCreated.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI created = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#created");
    /**
     * Type: Property <br/>
     * Label: identifier  <br/>
     * Comment: An unambiguous reference to the InformationElement within a given context. Recommended best practice is to identify the resource by means of a string conforming to a formal identification system.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI identifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#identifier");
    /**
     * Type: Property <br/>
     * Label: keyword  <br/>
     * Comment: Adapted DublinCore: The topic of the content of the resource, as keyword. No sentences here. Recommended best practice is to select a value from a controlled vocabulary or formal classification scheme.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI keyword = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#keyword");
    /**
     * Type: Property <br/>
     * Label: hasLogicalPart  <br/>
     * Comment: Generic property used to express 'logical' containment relationships between InformationElements. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of InformationElement to use those specific subproperties. Note the difference between 'physical' containment (hasPart) and logical containment (hasLogicalPart)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     */
    public static final URI hasLogicalPart = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#hasLogicalPart");
    /**
     * Type: Property <br/>
     * Label: isLogicalPartOf  <br/>
     * Comment: Generic property used to express 'logical' containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of InformationElement to use those specific subproperties. Note the difference between 'physical' containment (isPartOf) and logical containment (isLogicalPartOf)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     */
    public static final URI isLogicalPartOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isLogicalPartOf");
    /**
     * Type: Property <br/>
     * Label: title  <br/>
     * Comment: Name given to an InformationElement  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI title = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#title");
    /**
     * Type: Property <br/>
     * Label: hasPart  <br/>
     * Comment: Generic property used to express 'physical' containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of DataObjects to use those specific subproperties. Note to the developers: Please be aware of the distinction between containment relation and provenance. The hasPart relation models physical containment, an InformationElement (a nmo:Message) can have a 'physical' part (an nfo:Attachment).  Also, please note the difference between physical containment (hasPart) and logical containment (hasLogicalPart) the former has more strict meaning. They may occur independently of each other.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI hasPart = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#hasPart");
    /**
     * Type: Property <br/>
     * Label: isPartOf  <br/>
     * Comment: Generic property used to express containment relationships between DataObjects. NIE extensions are encouraged to provide more specific subproperties of this one. It is advisable for actual instances of DataObjects to use those specific subproperties. Note to the developers: Please be aware of the distinction between containment relation and provenance. The isPartOf relation models physical containment, a nie:DataObject (e.g. an nfo:Attachment) is a 'physical' part of an nie:InformationElement (a nmo:Message). Also, please note the difference between physical containment (isPartOf) and logical containment (isLogicalPartOf) the former has more strict meaning. They may occur independently of each other.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     */
    public static final URI isPartOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#isPartOf");
    /**
     * Type: Property <br/>
     * Label: licenseType  <br/>
     * Comment: The type of the license. Possible values for this field may include "GPL", "BSD", "Creative Commons" etc.  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI licenseType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#licenseType");
    /**
     * Type: Property <br/>
     * Label: characterSet  <br/>
     * Comment: Characterset in which the content of the InformationElement was created. Example: ISO-8859-1, UTF-8. One of the registered character sets at http://www.iana.org/assignments/character-sets. This characterSet is used to interpret any textual parts of the content. If more than one characterSet is used within one data object, use more specific properties.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI characterSet = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#characterSet");
    /**
     * Type: Property <br/>
     * Label: language  <br/>
     * Comment: Language the InformationElement is expressed in. This property applies to the data object in its entirety. If the data object is divisible into parts expressed in multiple languages - more specific properties should be used. Users are encouraged to use the two-letter code specified in the RFC 3066  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI language = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#language");
    /**
     * Type: Property <br/>
     * Label: contentSize  <br/>
     * Comment: The size of the content. This property can be used whenever the size of the content of an InformationElement differs from the size of the DataObject. (e.g. because of compression, encoding, encryption or any other representation issues). The contentSize in expressed in bytes.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI contentSize = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contentSize");
    /**
     * Type: Property <br/>
     * Label: rootElementOf  <br/>
     * Comment: DataObjects extracted from a single data source are organized into a containment tree. This property links the root of that tree with the datasource it has been extracted from  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     */
    public static final URI rootElementOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#rootElementOf");
}
