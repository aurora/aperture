package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Fri Aug 17 14:24:47 CEST 2007
 * input file: D:\workspace\aperture-nie/doc/ontology/sourceformat.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/sourceformat#
 */
public class SOURCEFORMAT {

    /** Path to the ontology resource */
    public static final String SOURCEFORMAT_RESOURCE_PATH = 
      SOURCEFORMAT.class.getPackage().getName().replace('.', '/') + "/sourceformat.rdfs";

    /**
     * Puts the SOURCEFORMAT ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getSOURCEFORMATOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(SOURCEFORMAT_RESOURCE_PATH, SOURCEFORMAT.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + SOURCEFORMAT_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for SOURCEFORMAT */
    public static final URI NS_SOURCEFORMAT = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#");
    /**
     * Type: Class <br/>
     * Label: CheckBoxWidget  <br/>
     * Comment: Use boolean checkbox to style the property. checked = boolean:true, unchecked=boolean:false.  <br/>
     */
    public static final URI CheckBoxWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#CheckBoxWidget");
    /**
     * Type: Class <br/>
     * Label: ComboBoxEntry  <br/>
     */
    public static final URI ComboBoxEntry = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry");
    /**
     * Type: Class <br/>
     * Label: ComboBoxWidget  <br/>
     * Comment: A combo box. Define the displayed labels and internal values using instances of ComboBoxEntry.  <br/>
     */
    public static final URI ComboBoxWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxWidget");
    /**
     * Type: Class <br/>
     * Label: IntegerFieldWidget  <br/>
     */
    public static final URI IntegerFieldWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#IntegerFieldWidget");
    /**
     * Type: Class <br/>
     * Label: PasswordTextFieldWidget  <br/>
     * Comment: A textfield hiding passwords behind * or other funny characters.  <br/>
     */
    public static final URI PasswordTextFieldWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#PasswordTextFieldWidget");
    /**
     * Type: Class <br/>
     * Label: TextFieldWidget  <br/>
     * Comment: A textfield  <br/>
     */
    public static final URI TextFieldWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#TextFieldWidget");
    /**
     * Type: Class <br/>
     * Label: MultipleTextFieldWidget  <br/>
     * Comment: A textfield for a multi-valued property  <br/>
     */
    public static final URI MultipleTextFieldWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#MultipleTextFieldWidget");
    /**
     * Type: Class <br/>
     * Label: PatternWidget  <br/>
     * Comment: A domain boundaries pattern widget  <br/>
     */
    public static final URI PatternWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#PatternWidget");
    /**
     * Type: Class <br/>
     * Label: UIWidget  <br/>
     * Comment: Superclass of UI widgets. Use instances of widgets to configure gui.  <br/>
     */
    public static final URI UIWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#UIWidget");
    /**
     * Type: Property <br/>
     * Label: hasEntry  <br/>
     * Comment: Binds a combo box widget with combo box entries  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxWidget  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry  <br/>
     */
    public static final URI hasEntry = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#hasEntry");
    /**
     * Type: Property <br/>
     * Label: label  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI label = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#label");
    /**
     * Type: Property <br/>
     * Label: value  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/sourceformat#ComboBoxEntry  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI value = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#value");
    /**
     * Type: Property <br/>
     * Label: valueWidget  <br/>
     * Domain: http://www.w3.org/2004/09/fresnel#Format  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/sourceformat#UIWidget  <br/>
     */
    public static final URI valueWidget = new URIImpl("http://aperture.semanticdesktop.org/ontology/sourceformat#valueWidget");
}
