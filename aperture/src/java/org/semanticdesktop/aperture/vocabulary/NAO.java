/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.vocabulary;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Oct 28 01:54:59 CET 2008
 * input file: D:\ganymedeworkspace\aperture-trunk/doc/ontology/nao.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#
 */
public class NAO {

    /** Path to the ontology resource */
    public static final String NAO_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/nao.rdfs";

    /**
     * Puts the NAO ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNAOOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NAO_RESOURCE_PATH, NAO.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NAO_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NAO */
    public static final URI NS_NAO = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#");
    /**
     * Type: Class <br/>
     * Label: party  <br/>
     * Comment: Represents a single or a group of individuals  <br/>
     */
    public static final URI Party = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Party");
    /**
     * Type: Class <br/>
     * Label: symbol  <br/>
     * Comment: Represents a symbol  <br/>
     */
    public static final URI Symbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol");
    /**
     * Type: Class <br/>
     * Label: tag  <br/>
     * Comment: Represents a generic tag  <br/>
     */
    public static final URI Tag = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag");
    /**
     * Type: Class <br/>
     * Label: freedesktopicon  <br/>
     * Comment: Represents a desktop icon as defined in the FreeDesktop Icon Naming Standard  <br/>
     */
    public static final URI FreeDesktopIcon = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#FreeDesktopIcon");
    /**
     * Type: Property <br/>
     * Label: annotation  <br/>
     * Comment: Generic annotation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI annotation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#annotation");
    /**
     * Type: Property <br/>
     * Label: alternative label  <br/>
     * Comment: An alternative label alongside the preferred label for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI altLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#altLabel");
    /**
     * Type: Property <br/>
     * Label: iconname  <br/>
     * Comment: Defines a name for a FreeDesktop Icon as defined in the FreeDesktop Icon Naming Standard  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#FreeDesktopIcon  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI iconName = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#iconName");
    /**
     * Type: Property <br/>
     * Label: score  <br/>
     * Comment: An authorative score for an item valued between 0 and 1  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI score = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#score");
    /**
     * Type: Property <br/>
     * Label: scoreparameter  <br/>
     * Comment: A marker property to mark selected properties which are input to a mathematical algorithm to generate scores for resources. Properties are marked by being defined as subproperties of this property  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI scoreParameter = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#scoreParameter");
    /**
     * Type: Property <br/>
     * Label: contributor  <br/>
     * Comment: Refers to a single or a group of individuals that contributed to a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Party  <br/>
     */
    public static final URI contributor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#contributor");
    /**
     * Type: Property <br/>
     * Label: creator  <br/>
     * Comment: Refers to the single or group of individuals that created the resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Party  <br/>
     */
    public static final URI creator = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#creator");
    /**
     * Type: Property <br/>
     * Label: description  <br/>
     * Comment: A non-technical textual annotation for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI description = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#description");
    /**
     * Type: Property <br/>
     * Label: engineering tool  <br/>
     * Comment: Specifies the engineering tool used to generate the graph  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI engineeringTool = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#engineeringTool");
    /**
     * Type: Property <br/>
     * Label: has default namespace  <br/>
     * Comment: Defines the default static namespace for a graph  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI hasDefaultNamespace = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasDefaultNamespace");
    /**
     * Type: Property <br/>
     * Label: has default namespace abbreviation  <br/>
     * Comment: Defines the default static namespace abbreviation for a graph  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI hasDefaultNamespaceAbbreviation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasDefaultNamespaceAbbreviation");
    /**
     * Type: Property <br/>
     * Label: rating  <br/>
     * Comment: Annotation for a resource in the form of an unrestricted rating  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI rating = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#rating");
    /**
     * Type: Property <br/>
     * Label: numeric rating  <br/>
     * Comment:  Annotation for a resource in the form of a numeric rating (float value), allowed values are between 1 and 10 whereas 0 is interpreted as not set  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI numericRating = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#numericRating");
    /**
     * Type: Property <br/>
     * Label: has symbol  <br/>
     * Comment: Annotation for a resource in the form of a symbol representation  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI hasSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasSymbol");
    /**
     * Type: Property <br/>
     * Label: preferred symbol  <br/>
     * Comment: A unique preferred symbol representation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI prefSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefSymbol");
    /**
     * Type: Property <br/>
     * Label: alternative symbol  <br/>
     * Comment: An alternative symbol representation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI altSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#altSymbol");
    /**
     * Type: Property <br/>
     * Label: has tag  <br/>
     * Comment: Defines an existing tag for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag  <br/>
     */
    public static final URI hasTag = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasTag");
    /**
     * Type: Property <br/>
     * Label: has topic  <br/>
     * Comment: Defines a relationship between two resources, where the object is a topic of the subject  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI hasTopic = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasTopic");
    /**
     * Type: Property <br/>
     * Label: identifier  <br/>
     * Comment: Defines a generic identifier for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI identifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#identifier");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#SymmetricProperty <br/>
     * Label: is related to  <br/>
     * Comment: Defines an annotation for a resource in the form of a relationship between the subject resource and another resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isRelated = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isRelated");
    /**
     * Type: Property <br/>
     * Label: is tag for  <br/>
     * Comment: States which resources a tag is associated with  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isTagFor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isTagFor");
    /**
     * Type: Property <br/>
     * Label: is topic of  <br/>
     * Comment: Defines a relationship between two resources, where the subject is a topic of the object  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isTopicOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isTopicOf");
    /**
     * Type: Property <br/>
     * Label: modified at  <br/>
     * Comment: States the modification time for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI modified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#modified");
    /**
     * Type: Property <br/>
     * Label: created at  <br/>
     * Comment: States the creation, or first modification time for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI created = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#created");
    /**
     * Type: Property <br/>
     * Label: lastModified  <br/>
     * Comment: States the last modification time for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI lastModified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#lastModified");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#InverseFunctionalProperty <br/>
     * Label: personal identifier  <br/>
     * Comment: Defines a personal string identifier for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI personalIdentifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#personalIdentifier");
    /**
     * Type: Property <br/>
     * Label: preferred label  <br/>
     * Comment: A preferred label for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI prefLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel");
    /**
     * Type: Property <br/>
     * Label: preferred label plural form  <br/>
     * Comment: The plural form of the preferred label for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI pluralPrefLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#pluralPrefLabel");
    /**
     * Type: Property <br/>
     * Label: serialization language  <br/>
     * Comment: States the serialization language for a named graph that is represented within a document  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#DocumentGraph  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI serializationLanguage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#serializationLanguage");
    /**
     * Type: Property <br/>
     * Label: status  <br/>
     * Comment: Specifies the status of a graph, stable, unstable or testing  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI status = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#status");
    /**
     * Type: Property <br/>
     * Label: version  <br/>
     * Comment: Specifies the version of a graph, in numeric format  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI version = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#version");
}
