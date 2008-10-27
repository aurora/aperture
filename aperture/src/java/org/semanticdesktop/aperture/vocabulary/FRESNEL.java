/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
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
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:36:07 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/fresnel.owl
 * namespace: http://www.w3.org/2004/09/fresnel#
 */
public class FRESNEL {

    /** Path to the ontology resource */
    public static final String FRESNEL_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/fresnel.owl";

    /**
     * Puts the FRESNEL ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getFRESNELOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(FRESNEL_RESOURCE_PATH, FRESNEL.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + FRESNEL_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for FRESNEL */
    public static final URI NS_FRESNEL = new URIImpl("http://www.w3.org/2004/09/fresnel#");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Comment: This is a convenience class for the OWL specification of Fresnel (an rdf:List of resource selectors only).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI ClassList = new URIImpl("http://www.w3.org/2004/09/fresnel#ClassList");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Convenience Token^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Abstract class for expressing shorthands a browser must understand how to expand.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI ConvenienceToken = new URIImpl("http://www.w3.org/2004/09/fresnel#ConvenienceToken");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Convenience Property^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Abstract class of pseudo properties.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI ConvienceProperty = new URIImpl("http://www.w3.org/2004/09/fresnel#ConvienceProperty");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Format Class^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: A Fresnel format class for displaying RDF data; the domain property can be only one of :formatDomain, :classFormatDomain, :instanceFormatDomain.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI Format = new URIImpl("http://www.w3.org/2004/09/fresnel#Format");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Format Description^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Description of fresnel:Format content separators; super class, not to be used directly.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI FormatDescription = new URIImpl("http://www.w3.org/2004/09/fresnel#FormatDescription");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Format Description No Substitution^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Description of fresnel:Format content separators only; contentNoValue will not be recognized in conjunction with this class.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI FormatDescriptionNoSubstitution = new URIImpl("http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Format Description Substitution^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Description of fresnel:Format content separators and content replacement if values are missing.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI FormatDescriptionSubstitution = new URIImpl("http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Group^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: A Fresnel Group is used for grouping formats and lenses together.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI Group = new URIImpl("http://www.w3.org/2004/09/fresnel#Group");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Hide Property List^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: This is a convenience class for the OWL specification of Fresnel.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI HidePropertyList = new URIImpl("http://www.w3.org/2004/09/fresnel#HidePropertyList");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Labelling Format^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Abstract superclass of all labelling formats.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI LabellingFormat = new URIImpl("http://www.w3.org/2004/09/fresnel#LabellingFormat");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Lens^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Lens for viewing RDF data.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI Lens = new URIImpl("http://www.w3.org/2004/09/fresnel#Lens");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Property Description^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: More detailed description of the property, e.g. for specifing sublenses or merging properties.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI PropertyDescription = new URIImpl("http://www.w3.org/2004/09/fresnel#PropertyDescription");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Property Set^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Abstract class of property sets.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI PropertySet = new URIImpl("http://www.w3.org/2004/09/fresnel#PropertySet");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Property value display style^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Abstract superclass of all property value display styles.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI PropertyValueStyle = new URIImpl("http://www.w3.org/2004/09/fresnel#PropertyValueStyle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Purpose^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Propose in which a specific lens might be appropriate.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI Purpose = new URIImpl("http://www.w3.org/2004/09/fresnel#Purpose");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#Class <br/>
     * Label: Show Property List^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: This is a convenience class for the OWL specification of Fresnel.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI ShowPropertyList = new URIImpl("http://www.w3.org/2004/09/fresnel#ShowPropertyList");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#ConvienceProperty <br/>
     * Label: member^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: A special token to indicate the relationship between any RDF container or collection (rdf:List) and its member items.  Do not use as anything other than an individual.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI member = new URIImpl("http://www.w3.org/2004/09/fresnel#member");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#LabellingFormat <br/>
     * Label: none^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Do not show any label for the property.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI none = new URIImpl("http://www.w3.org/2004/09/fresnel#none");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#LabellingFormat <br/>
     * Label: Show Label^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Show the RDFS Label of the property. This is the default and doesn't have to be declared.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI show = new URIImpl("http://www.w3.org/2004/09/fresnel#show");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#PropertySet <br/>
     * Label: All Properties^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Set of all properties of the current instance, which have not be explicitly named before.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI allProperties = new URIImpl("http://www.w3.org/2004/09/fresnel#allProperties");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#PropertyValueStyle <br/>
     * Label: External Link^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Show the property value as a dereferenceable URL.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI externalLink = new URIImpl("http://www.w3.org/2004/09/fresnel#externalLink");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#PropertyValueStyle <br/>
     * Label: Image^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Show the property value as an image, such as PNG or JPEG .^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI image = new URIImpl("http://www.w3.org/2004/09/fresnel#image");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#PropertyValueStyle <br/>
     * Label: Replaced Resource^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Any kind of retrievable resource like an image, audio or video that should be displayed as property value.  The browser should try retrieve the resource and show it instead of the property value URI.  The browser has to negotiate an appropriate media type with the server using HTTP content negotiation.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI replacedResource = new URIImpl("http://www.w3.org/2004/09/fresnel#replacedResource");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#PropertyValueStyle <br/>
     * Label: URI^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Show the property value as an URI.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI uri = new URIImpl("http://www.w3.org/2004/09/fresnel#uri");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#Purpose <br/>
     * Label: Default Lens^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Provides the browser with a starting point which lens to show.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI defaultLens = new URIImpl("http://www.w3.org/2004/09/fresnel#defaultLens");
    /**
     * Type: Instance of http://www.w3.org/2004/09/fresnel#Purpose <br/>
     * Label: Label Lens^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Lens for providing a label for a resource.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI labelLens = new URIImpl("http://www.w3.org/2004/09/fresnel#labelLens");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: content after^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Content that should be displayed before the content of the current box.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#FormatDescription  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentAfter = new URIImpl("http://www.w3.org/2004/09/fresnel#contentAfter");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: content before^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Content that should be displayed before the content of the current box.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#FormatDescription  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentBefore = new URIImpl("http://www.w3.org/2004/09/fresnel#contentBefore");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: content first^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Content that should be displayed before the content of the first element in a list of boxes; takes over the first :contentBefore element in case of a conflict.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#FormatDescription  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentFirst = new URIImpl("http://www.w3.org/2004/09/fresnel#contentFirst");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: content last^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Content that should be displayed after the content of the last element in a list of boxes; takes over the last :contentAfter element in case of a conflict.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#FormatDescription  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentLast = new URIImpl("http://www.w3.org/2004/09/fresnel#contentLast");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: content no value^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Content that should be displayed if the current property is missing.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentNoValue = new URIImpl("http://www.w3.org/2004/09/fresnel#contentNoValue");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: depth^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Closure mechanism, if lenses are recursively used; the recursive depth limit.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#PropertyDescription  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#nonNegativeInteger  <br/>
     */
    public static final URI depth = new URIImpl("http://www.w3.org/2004/09/fresnel#depth");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: label style^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Attaching a symbol appropriate for styling labels (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx93  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI labelStyle = new URIImpl("http://www.w3.org/2004/09/fresnel#labelStyle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: property style^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Attaching a symbol appropriate for styling properties (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx102  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI propertyStyle = new URIImpl("http://www.w3.org/2004/09/fresnel#propertyStyle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: resource style^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Attaching a symbol appropriate for styling a resource.  resourceStyle is ignored if not used with :classFormatDomain or :instanceFormatDomain.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx112  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI resourceStyle = new URIImpl("http://www.w3.org/2004/09/fresnel#resourceStyle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#DatatypeProperty <br/>
     * Label: value style^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Attaching a symbol appropriate for styling the values of a property (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx129  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI valueStyle = new URIImpl("http://www.w3.org/2004/09/fresnel#valueStyle");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: class format domain^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: The Format should be applied to instances of this class.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Class  <br/>
     */
    public static final URI classFormatDomain = new URIImpl("http://www.w3.org/2004/09/fresnel#classFormatDomain");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: class lens domain^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies that the lens is usable for the specified class and its subclasses.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Lens  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Class  <br/>
     */
    public static final URI classLensDomain = new URIImpl("http://www.w3.org/2004/09/fresnel#classLensDomain");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: group^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies the format group to which a format or a lens belongs.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx83  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#Group  <br/>
     */
    public static final URI group = new URIImpl("http://www.w3.org/2004/09/fresnel#group");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: hide properties^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: List of all properties which should be hidden.  Must be used together with 'fresnel:showProperties fresnel:allProperties.'^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Lens  <br/>
     * Range: _:node13ck6vouqx86  <br/>
     */
    public static final URI hideProperties = new URIImpl("http://www.w3.org/2004/09/fresnel#hideProperties");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: instance format domain^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: The Format should be applied to this set of instances.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI instanceFormatDomain = new URIImpl("http://www.w3.org/2004/09/fresnel#instanceFormatDomain");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: instance lens domain^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: The lens is usable for the specified set of instances.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Lens  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI instanceLensDomain = new URIImpl("http://www.w3.org/2004/09/fresnel#instanceLensDomain");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: label^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies how a property is labelled (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     */
    public static final URI label = new URIImpl("http://www.w3.org/2004/09/fresnel#label");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: label format^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Assign content formatting to a set of labels (theoretically, this does not make much sense, but it does round out the set of formatting properties; only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx90  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution  <br/>
     */
    public static final URI labelFormat = new URIImpl("http://www.w3.org/2004/09/fresnel#labelFormat");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: primaryClasses^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies the classes that should be considered primaries, or first class results; secondary resources not matching the primaries will only be shown as sublenses.  The range is a list of resource selectors.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Group  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#ClassList  <br/>
     */
    public static final URI primaryClasses = new URIImpl("http://www.w3.org/2004/09/fresnel#primaryClasses");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: property^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: The RDF property, which is described.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#PropertyDescription  <br/>
     * Range: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property  <br/>
     */
    public static final URI property = new URIImpl("http://www.w3.org/2004/09/fresnel#property");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: property format^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Assign content formatting to a set of properties (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx96  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution  <br/>
     */
    public static final URI propertyFormat = new URIImpl("http://www.w3.org/2004/09/fresnel#propertyFormat");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: property format domain^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: The Format should be used for the specified properties.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     * Range: _:node13ck6vouqx99  <br/>
     */
    public static final URI propertyFormatDomain = new URIImpl("http://www.w3.org/2004/09/fresnel#propertyFormatDomain");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: purpose^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies a purpose for which a lens or format might be appropriate.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx105  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#Purpose  <br/>
     */
    public static final URI purpose = new URIImpl("http://www.w3.org/2004/09/fresnel#purpose");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: resource format^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Assign content formatting to a set of resources (only works in conjunction with classFormatDomain and instanceFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx109  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution  <br/>
     */
    public static final URI resourceFormat = new URIImpl("http://www.w3.org/2004/09/fresnel#resourceFormat");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: show properties^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: List of all properties which should be shown.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Lens  <br/>
     * Range: _:node13ck6vouqx115  <br/>
     */
    public static final URI showProperties = new URIImpl("http://www.w3.org/2004/09/fresnel#showProperties");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: sublens^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Sublens which should be used for displaying property values.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#PropertyDescription  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#Lens  <br/>
     */
    public static final URI sublens = new URIImpl("http://www.w3.org/2004/09/fresnel#sublens");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: use^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Explicit definition of fresnel:Group containing formats that should be used to render the lens or sublens.^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx120  <br/>
     * Range: _:node13ck6vouqx123  <br/>
     */
    public static final URI use = new URIImpl("http://www.w3.org/2004/09/fresnel#use");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: property value^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Specifies how a property value is displayed (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#PropertyValueStyle  <br/>
     */
    public static final URI value = new URIImpl("http://www.w3.org/2004/09/fresnel#value");
    /**
     * Type: Instance of http://www.w3.org/2002/07/owl#ObjectProperty <br/>
     * Label: value format^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Comment: Assign content formatting to a set of values (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string  <br/>
     * Domain: _:node13ck6vouqx126  <br/>
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution  <br/>
     */
    public static final URI valueFormat = new URIImpl("http://www.w3.org/2004/09/fresnel#valueFormat");
}
