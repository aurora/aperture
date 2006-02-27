package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Feb 26 18:35:06 CET 2006
 * input file: doc/ontology/vcard.rdfs
 * namespace: http://www.gnowsis.org/ont/vcard#
 */
public class VCARD {
	public static final String NS = "http://www.gnowsis.org/ont/vcard#";

    /**
     * Label: vCard 
     */
    public static final URI VCard = new URIImpl("http://www.gnowsis.org/ont/vcard#vCard");

    /**
     * Label: Address 
     * Comment:  
     */
    public static final URI Address = new URIImpl("http://www.gnowsis.org/ont/vcard#Address");

    /**
     * Label: Family Name 
     * Comment:  
     */
    public static final URI nameFamily = new URIImpl("http://www.gnowsis.org/ont/vcard#nameFamily");

    /**
     * Label: Given Name 
     * Comment:  
     */
    public static final URI nameGiven = new URIImpl("http://www.gnowsis.org/ont/vcard#nameGiven");

    /**
     * Label: Additional Name 
     * Comment:  
     */
    public static final URI nameAdditional = new URIImpl("http://www.gnowsis.org/ont/vcard#nameAdditional");

    /**
     * Label: Honorific Prefix Name 
     * Comment:  
     */
    public static final URI nameHonorificPrefix = new URIImpl("http://www.gnowsis.org/ont/vcard#nameHonorificPrefix");

    /**
     * Label: Honorific Suffix Name 
     * Comment:  
     */
    public static final URI nameHonorificSuffix = new URIImpl("http://www.gnowsis.org/ont/vcard#nameHonorificSuffix");

    /**
     * Label: Fullname 
     * Comment: To specify the formatted text corresponding to the name
   of the object the vCard represents. Subproperty of RDFS:label 
     */
    public static final URI fullname = new URIImpl("http://www.gnowsis.org/ont/vcard#fullname");

    /**
     * Label: Organisation 
     * Comment:  
     */
    public static final URI org = new URIImpl("http://www.gnowsis.org/ont/vcard#org");

    /**
     * Label: Title 
     * Comment:  
     */
    public static final URI title = new URIImpl("http://www.gnowsis.org/ont/vcard#title");

    /**
     * Label: Note 
     * Comment:  
     */
    public static final URI note = new URIImpl("http://www.gnowsis.org/ont/vcard#note");

    /**
     * Label: Telefone number 
     * Comment:  
     */
    public static final URI tel = new URIImpl("http://www.gnowsis.org/ont/vcard#tel");

    /**
     * Label: Work Telefone number 
     * Comment:  
     */
    public static final URI telWork = new URIImpl("http://www.gnowsis.org/ont/vcard#telWork");

    /**
     * Label: Home Telefone number 
     * Comment:  
     */
    public static final URI telHome = new URIImpl("http://www.gnowsis.org/ont/vcard#telHome");

    /**
     * Label: Message Telefone number 
     * Comment:  
     */
    public static final URI telMsg = new URIImpl("http://www.gnowsis.org/ont/vcard#telMsg");

    /**
     * Label: Prefered Telefone number 
     * Comment:  
     */
    public static final URI telPref = new URIImpl("http://www.gnowsis.org/ont/vcard#telPref");

    /**
     * Label: Voice Telefone number 
     * Comment:  
     */
    public static final URI telVoice = new URIImpl("http://www.gnowsis.org/ont/vcard#telVoice");

    /**
     * Label: Fax Telefone number 
     * Comment:  
     */
    public static final URI telFax = new URIImpl("http://www.gnowsis.org/ont/vcard#telFax");

    /**
     * Label: Cell Telefone number 
     * Comment:  
     */
    public static final URI telCell = new URIImpl("http://www.gnowsis.org/ont/vcard#telCell");

    /**
     * Label: Video Telefone number 
     * Comment:  
     */
    public static final URI telVideo = new URIImpl("http://www.gnowsis.org/ont/vcard#telVideo");

    /**
     * Label: Pager Telefone number 
     * Comment:  
     */
    public static final URI telPager = new URIImpl("http://www.gnowsis.org/ont/vcard#telPager");

    /**
     * Label: Bbs Telefone number 
     * Comment:  
     */
    public static final URI telBbs = new URIImpl("http://www.gnowsis.org/ont/vcard#telBbs");

    /**
     * Label: Modem Telefone number 
     * Comment:  
     */
    public static final URI telModem = new URIImpl("http://www.gnowsis.org/ont/vcard#telModem");

    /**
     * Label: Car Telefone number 
     * Comment:  
     */
    public static final URI telCar = new URIImpl("http://www.gnowsis.org/ont/vcard#telCar");

    /**
     * Label: Isdn Telefone number 
     * Comment:  
     */
    public static final URI telIsdn = new URIImpl("http://www.gnowsis.org/ont/vcard#telIsdn");

    /**
     * Label: Pcs Telefone number 
     * Comment:  
     */
    public static final URI telPcs = new URIImpl("http://www.gnowsis.org/ont/vcard#telPcs");

    /**
     * Label: email address 
     * Comment:  
     */
    public static final URI email = new URIImpl("http://www.gnowsis.org/ont/vcard#email");

    /**
     * Label: has address 
     * Comment:  
     */
    public static final URI address = new URIImpl("http://www.gnowsis.org/ont/vcard#address");

    /**
     * Label: domestic delivery address 
     * Comment: domestic delivery address 
     */
    public static final URI addressDom = new URIImpl("http://www.gnowsis.org/ont/vcard#addressDom");

    /**
     * Label: international delivery address 
     * Comment: international delivery address 
     */
    public static final URI addressIntl = new URIImpl("http://www.gnowsis.org/ont/vcard#addressIntl");

    /**
     * Label: postal delivery address 
     * Comment: domestic delivery address 
     */
    public static final URI addressPostal = new URIImpl("http://www.gnowsis.org/ont/vcard#addressPostal");

    /**
     * Label: parcel delivery address 
     * Comment: parcel delivery address 
     */
    public static final URI addressParcel = new URIImpl("http://www.gnowsis.org/ont/vcard#addressParcel");

    /**
     * Label: home address 
     * Comment: delivery address for a residence 
     */
    public static final URI addressHome = new URIImpl("http://www.gnowsis.org/ont/vcard#addressHome");

    /**
     * Label: work address 
     * Comment: delivery address for a place of work 
     */
    public static final URI addressWork = new URIImpl("http://www.gnowsis.org/ont/vcard#addressWork");

    /**
     * Label: preferred address 
     * Comment: preferred delivery address when more than one address is specified 
     */
    public static final URI addressPref = new URIImpl("http://www.gnowsis.org/ont/vcard#addressPref");

    /**
     * Label: PO Box 
     * Comment: Post office box 
     */
    public static final URI pobox = new URIImpl("http://www.gnowsis.org/ont/vcard#pobox");

    /**
     * Label: extended address 
     * Comment:  
     */
    public static final URI extendedAddress = new URIImpl("http://www.gnowsis.org/ont/vcard#extendedAddress");

    /**
     * Label: street address 
     * Comment:  
     */
    public static final URI streetAddress = new URIImpl("http://www.gnowsis.org/ont/vcard#streetAddress");

    /**
     * Label: City 
     * Comment: Locality or City 
     */
    public static final URI locality = new URIImpl("http://www.gnowsis.org/ont/vcard#locality");

    /**
     * Label: Region 
     * Comment: Region 
     */
    public static final URI region = new URIImpl("http://www.gnowsis.org/ont/vcard#region");

    /**
     * Label: Postal Code 
     * Comment: Postal Code 
     */
    public static final URI postalcode = new URIImpl("http://www.gnowsis.org/ont/vcard#postalcode");

    /**
     * Label: country 
     * Comment: Country 
     */
    public static final URI country = new URIImpl("http://www.gnowsis.org/ont/vcard#country");

}
