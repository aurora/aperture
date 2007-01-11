package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Thu Jan 11 15:33:18 CET 2007
 * input file: doc/ontology/sourceformat.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/sourceformat#
 */
public interface SOURCEFORMAT_GEN {
	public static final String NS_SOURCEFORMAT_GEN = "http://aperture.semanticdesktop.org/ontology/sourceformat#";

    /**
     * Label: CheckBoxWidget 
     * Comment: Use boolean checkbox to style the property. checked = boolean:true, unchecked=boolean:false. 
     */
    public static final URI CheckBoxWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#CheckBoxWidget");

    /**
     * Label: ComboBoxEntry 
     */
    public static final URI ComboBoxEntry = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry");

    /**
     * Label: ComboBoxWidget 
     * Comment: A combo box. Define the displayed labels and internal values using instances of ComboBoxEntry. 
     */
    public static final URI ComboBoxWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxWidget");

    /**
     * Label: PasswordTextFieldWidget 
     * Comment: A textfield hiding passwords behind * or other funny characters. 
     */
    public static final URI PasswordTextFieldWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#PasswordTextFieldWidget");

    /**
     * Label: TextFieldWidget 
     * Comment: A textfield 
     */
    public static final URI TextFieldWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#TextFieldWidget");

    /**
     * Label: UIWidget 
     * Comment: Superclass of UI widgets. Use instances of widgets to configure gui. 
     */
    public static final URI UIWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#UIWidget");

    /**
     * Label: hasEntry 
     * Comment: Binds a combo box widget with combo box entries 
     * Comment: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxWidget 
     * Range: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry 
     */
    public static final URI hasEntry = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#hasEntry");

    /**
     * Label: label 
     * Comment: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI label = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#label");

    /**
     * Label: value 
     * Comment: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI value = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#value");

    /**
     * Label: valueWidget 
     * Comment: http://www.w3.org/2004/09/fresnel#Format 
     * Range: http://aperture.semanticdesktop.org/ontology/sourceformat#UIWidget 
     */
    public static final URI valueWidget = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/sourceformat#valueWidget");

}
