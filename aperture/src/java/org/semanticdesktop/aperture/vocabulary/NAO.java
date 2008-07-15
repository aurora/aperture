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
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:36:18 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/nao.rdfs
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
     * Label: Party  <br/>
     * Comment: Represents a single or a group of individuals  <br/>
     */
    public static final URI Party = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Party");
    /**
     * Type: Class <br/>
     * Label: Symbol  <br/>
     * Comment: Represents a symbol  <br/>
     */
    public static final URI Symbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol");
    /**
     * Type: Class <br/>
     * Label: Tag  <br/>
     * Comment: Represents a generic tag  <br/>
     */
    public static final URI Tag = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag");
    /**
     * Type: Property <br/>
     * Label: altLabel  <br/>
     * Comment: An alternative label alongside the preferred label for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI altLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#altLabel");
    /**
     * Type: Property <br/>
     * Label: annotation  <br/>
     * Comment: Generic annotation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI annotation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#annotation");
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
     * Label: engineeringTool  <br/>
     * Comment: Specifies the engineering tool used to generate the graph  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI engineeringTool = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#engineeringTool");
    /**
     * Type: Property <br/>
     * Label: hasDefaultNamespace  <br/>
     * Comment: Defines the default static namespace for a graph  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#Data  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI hasDefaultNamespace = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasDefaultNamespace");
    /**
     * Type: Property <br/>
     * Label: hasDefaultNamespaceAbbreviation  <br/>
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
     * Label: numericRating  <br/>
     * Comment: Annotation for a resource in the form of a numeric decimal rating  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#decimal  <br/>
     */
    public static final URI numericRating = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#numericRating");
    /**
     * Type: Property <br/>
     * Label: hasSymbol  <br/>
     * Comment: Annotation for a resource in the form of a symbol representation  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI hasSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasSymbol");
    /**
     * Type: Property <br/>
     * Label: prefSymbol  <br/>
     * Comment: A unique preferred symbol representation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI prefSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefSymbol");
    /**
     * Type: Property <br/>
     * Label: altSymbol  <br/>
     * Comment: An alternative symbol representation for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Symbol  <br/>
     */
    public static final URI altSymbol = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#altSymbol");
    /**
     * Type: Property <br/>
     * Label: hasTag  <br/>
     * Comment: Defines an existing tag for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag  <br/>
     */
    public static final URI hasTag = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#hasTag");
    /**
     * Type: Property <br/>
     * Label: hasTopic  <br/>
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
     * Label: isRelated  <br/>
     * Comment: Defines an annotation for a resource in the form of a relationship between the subject resource and another resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isRelated = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isRelated");
    /**
     * Type: Property <br/>
     * Label: isTagFor  <br/>
     * Comment: States which resources a tag is associated with  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/08/15/nao#Tag  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isTagFor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isTagFor");
    /**
     * Type: Property <br/>
     * Label: isTopicOf  <br/>
     * Comment: Defines a relationship between two resources, where the subject is a topic of the object  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI isTopicOf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#isTopicOf");
    /**
     * Type: Property <br/>
     * Label: modified  <br/>
     * Comment: States the modification time for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI modified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#modified");
    /**
     * Type: Property <br/>
     * Label: created  <br/>
     * Comment: States the creation, or first modification time for a resource  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI created = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#created");
    /**
     * Type: Property <br/>
     * Label: lastModified  <br/>
     * Comment: States the last modification time for a graph  <br/>
     * Domain: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI lastModified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#lastModified");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#InverseFunctionalProperty <br/>
     * Label: personalIdentifier  <br/>
     * Comment: Defines a personal string identifier for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI personalIdentifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#personalIdentifier");
    /**
     * Type: Property <br/>
     * Label: prefLabel  <br/>
     * Comment: A preferred label for a resource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI prefLabel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel");
    /**
     * Type: Property <br/>
     * Label: serializationLanguage  <br/>
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
