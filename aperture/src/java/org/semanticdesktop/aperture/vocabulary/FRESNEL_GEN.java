package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Thu Jan 11 15:35:41 CET 2007
 * input file: doc/ontology/fresnel.owl
 * namespace: http://www.w3.org/2004/09/fresnel#
 */
public interface FRESNEL_GEN {
	public static final String NS_FRESNEL_GEN = "http://www.w3.org/2004/09/fresnel#";

    /**
     * Comment: This is a convenience class for the OWL specification of Fresnel (an rdf:List of resource selectors only).^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI ClassList = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#ClassList");

    /**
     * Label: Convenience Token^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Abstract class for expressing shorthands a browser must understand how to expand.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI ConvenienceToken = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#ConvenienceToken");

    /**
     * Label: Convenience Property^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Abstract class of pseudo properties.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI ConvienceProperty = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#ConvienceProperty");

    /**
     * Label: Format Class^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: A Fresnel format class for displaying RDF data; the domain property can be only one of :formatDomain, :classFormatDomain, :instanceFormatDomain.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI Format = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#Format");

    /**
     * Label: Format Description^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Description of fresnel:Format content separators; super class, not to be used directly.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI FormatDescription = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#FormatDescription");

    /**
     * Label: Format Description No Substitution^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Description of fresnel:Format content separators only; contentNoValue will not be recognized in conjunction with this class.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI FormatDescriptionNoSubstitution = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution");

    /**
     * Label: Format Description Substitution^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Description of fresnel:Format content separators and content replacement if values are missing.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI FormatDescriptionSubstitution = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution");

    /**
     * Label: Group^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: A Fresnel Group is used for grouping formats and lenses together.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI Group = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#Group");

    /**
     * Label: Hide Property List^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: This is a convenience class for the OWL specification of Fresnel.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI HidePropertyList = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#HidePropertyList");

    /**
     * Label: Labelling Format^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Abstract superclass of all labelling formats.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI LabellingFormat = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#LabellingFormat");

    /**
     * Label: Lens^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Lens for viewing RDF data.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI Lens = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#Lens");

    /**
     * Label: Property Description^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: More detailed description of the property, e.g. for specifing sublenses or merging properties.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI PropertyDescription = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#PropertyDescription");

    /**
     * Label: Property Set^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Abstract class of property sets.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI PropertySet = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#PropertySet");

    /**
     * Label: Property value display style^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Abstract superclass of all property value display styles.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI PropertyValueStyle = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#PropertyValueStyle");

    /**
     * Label: Purpose^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Propose in which a specific lens might be appropriate.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI Purpose = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#Purpose");

    /**
     * Label: Show Property List^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: This is a convenience class for the OWL specification of Fresnel.^^http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI ShowPropertyList = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#ShowPropertyList");

    /**
     * Label: content after^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Content that should be displayed before the content of the current box.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#FormatDescription 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI contentAfter = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#contentAfter");

    /**
     * Label: content before^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Content that should be displayed before the content of the current box.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#FormatDescription 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI contentBefore = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#contentBefore");

    /**
     * Label: content first^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Content that should be displayed before the content of the first element in a list of boxes; takes over the first :contentBefore element in case of a conflict.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#FormatDescription 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI contentFirst = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#contentFirst");

    /**
     * Label: content last^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Content that should be displayed after the content of the last element in a list of boxes; takes over the last :contentAfter element in case of a conflict.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#FormatDescription 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI contentLast = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#contentLast");

    /**
     * Label: content no value^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Content that should be displayed if the current property is missing.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI contentNoValue = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#contentNoValue");

    /**
     * Label: depth^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Closure mechanism, if lenses are recursively used; the recursive depth limit.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#PropertyDescription 
     * Range: http://www.w3.org/2001/XMLSchema#nonNegativeInteger 
     */
    public static final URI depth = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#depth");

    /**
     * Label: label style^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Attaching a symbol appropriate for styling labels (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx93 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI labelStyle = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#labelStyle");

    /**
     * Label: property style^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Attaching a symbol appropriate for styling properties (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx102 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI propertyStyle = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#propertyStyle");

    /**
     * Label: resource style^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Attaching a symbol appropriate for styling a resource.  resourceStyle is ignored if not used with :classFormatDomain or :instanceFormatDomain.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx112 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI resourceStyle = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#resourceStyle");

    /**
     * Label: value style^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Attaching a symbol appropriate for styling the values of a property (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx129 
     * Range: http://www.w3.org/2001/XMLSchema#string 
     */
    public static final URI valueStyle = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#valueStyle");

    /**
     * Label: class format domain^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: The Format should be applied to instances of this class.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     * Range: http://www.w3.org/2000/01/rdf-schema#Class 
     */
    public static final URI classFormatDomain = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#classFormatDomain");

    /**
     * Label: class lens domain^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies that the lens is usable for the specified class and its subclasses.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Lens 
     * Range: http://www.w3.org/2000/01/rdf-schema#Class 
     */
    public static final URI classLensDomain = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#classLensDomain");

    /**
     * Label: group^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies the format group to which a format or a lens belongs.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx83 
     * Range: http://www.w3.org/2004/09/fresnel#Group 
     */
    public static final URI group = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#group");

    /**
     * Label: hide properties^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: List of all properties which should be hidden.  Must be used together with 'fresnel:showProperties fresnel:allProperties.'^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Lens 
     * Range: node1208pbpdrx86 
     */
    public static final URI hideProperties = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#hideProperties");

    /**
     * Label: instance format domain^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: The Format should be applied to this set of instances.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final URI instanceFormatDomain = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#instanceFormatDomain");

    /**
     * Label: instance lens domain^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: The lens is usable for the specified set of instances.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Lens 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final URI instanceLensDomain = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#instanceLensDomain");

    /**
     * Label: label^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies how a property is labelled (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     */
    public static final URI label = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#label");

    /**
     * Label: label format^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Assign content formatting to a set of labels (theoretically, this does not make much sense, but it does round out the set of formatting properties; only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx90 
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution 
     */
    public static final URI labelFormat = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#labelFormat");

    /**
     * Label: primaryClasses^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies the classes that should be considered primaries, or first class results; secondary resources not matching the primaries will only be shown as sublenses.  The range is a list of resource selectors.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Group 
     * Range: http://www.w3.org/2004/09/fresnel#ClassList 
     */
    public static final URI primaryClasses = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#primaryClasses");

    /**
     * Label: property^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: The RDF property, which is described.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#PropertyDescription 
     * Range: http://www.w3.org/1999/02/22-rdf-syntax-ns#Property 
     */
    public static final URI property = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#property");

    /**
     * Label: property format^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Assign content formatting to a set of properties (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx96 
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionSubstitution 
     */
    public static final URI propertyFormat = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#propertyFormat");

    /**
     * Label: property format domain^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: The Format should be used for the specified properties.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     * Range: node1208pbpdrx99 
     */
    public static final URI propertyFormatDomain = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#propertyFormatDomain");

    /**
     * Label: purpose^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies a purpose for which a lens or format might be appropriate.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx105 
     * Range: http://www.w3.org/2004/09/fresnel#Purpose 
     */
    public static final URI purpose = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#purpose");

    /**
     * Label: resource format^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Assign content formatting to a set of resources (only works in conjunction with classFormatDomain and instanceFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx109 
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution 
     */
    public static final URI resourceFormat = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#resourceFormat");

    /**
     * Label: show properties^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: List of all properties which should be shown.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Lens 
     * Range: node1208pbpdrx115 
     */
    public static final URI showProperties = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#showProperties");

    /**
     * Label: sublens^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Sublens which should be used for displaying property values.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#PropertyDescription 
     * Range: http://www.w3.org/2004/09/fresnel#Lens 
     */
    public static final URI sublens = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#sublens");

    /**
     * Label: use^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Explicit definition of fresnel:Group containing formats that should be used to render the lens or sublens.^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx120 
     * Range: node1208pbpdrx123 
     */
    public static final URI use = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#use");

    /**
     * Label: property value^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Specifies how a property value is displayed (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     * Range: http://www.w3.org/2004/09/fresnel#PropertyValueStyle 
     */
    public static final URI value = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#value");

    /**
     * Label: value format^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: Assign content formatting to a set of values (only works in conjunction with propertyFormatDomain).^^http://www.w3.org/2001/XMLSchema#string 
     * Comment: node1208pbpdrx126 
     * Range: http://www.w3.org/2004/09/fresnel#FormatDescriptionNoSubstitution 
     */
    public static final URI valueFormat = URIImpl.createURIWithoutChecking("http://www.w3.org/2004/09/fresnel#valueFormat");

}
