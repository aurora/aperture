package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 29 11:14:15 CET 2006
 * input file: doc/ontology/dctype.rdfs
 * namespace: http://purl.org/dc/dcmitype/
 */
public class DCTYPE_GEN {
	public static final String NS = "http://purl.org/dc/dcmitype/";

    /**
     */
    public static final URI DCMIType = URIImpl.createURIWithoutChecking("http://purl.org/dc/terms/DCMIType");

    /**
     * Label: Collection (en-us) 
     * Comment: An aggregation of resources. (en-us) 
     */
    public static final URI Collection = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Collection");

    /**
     * Label: Dataset (en-us) 
     * Comment: Data encoded in a defined structure. (en-us) 
     */
    public static final URI Dataset = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Dataset");

    /**
     * Label: Event (en-us) 
     * Comment: A non-persistent, time-based occurrence. (en-us) 
     */
    public static final URI Event = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Event");

    /**
     * Label: Image (en-us) 
     * Comment: A visual representation other than text. (en-us) 
     */
    public static final URI Image = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Image");

    /**
     * Label: Interactive Resource (en-us) 
     * Comment: A resource requiring interaction from the user to
                 be understood, executed, or experienced. (en-us) 
     */
    public static final URI InteractiveResource = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/InteractiveResource");

    /**
     * Label: Service (en-us) 
     * Comment: A system that provides one or more functions. (en-us) 
     */
    public static final URI Service = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Service");

    /**
     * Label: Software (en-us) 
     * Comment: A computer program in source or compiled form. (en-us) 
     */
    public static final URI Software = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Software");

    /**
     * Label: Sound (en-us) 
     * Comment: A resource primarily intended to be heard. (en-us) 
     */
    public static final URI Sound = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Sound");

    /**
     * Label: Text (en-us) 
     * Comment: A resource consisting primarily of words for reading. (en-us) 
     */
    public static final URI Text = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/Text");

    /**
     * Label: Physical Object (en-us) 
     * Comment: An inanimate, three-dimensional object or substance. (en-us) 
     */
    public static final URI PhysicalObject = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/PhysicalObject");

    /**
     * Label: Still Image (en-us) 
     * Comment: A static visual representation. (en-us) 
     */
    public static final URI StillImage = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/StillImage");

    /**
     * Label: Moving Image (en-us) 
     * Comment: A series of visual representations imparting
                 an impression of motion when shown in succession. (en-us) 
     */
    public static final URI MovingImage = URIImpl.createURIWithoutChecking("http://purl.org/dc/dcmitype/MovingImage");

}
