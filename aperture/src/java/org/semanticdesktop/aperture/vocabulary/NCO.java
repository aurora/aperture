package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Aug 21 16:32:33 CEST 2007
 * input file: D:\workspace\aperture/doc/ontology/nco.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#
 */
public class NCO {

    /** Path to the ontology resource */
    public static final String NCO_RESOURCE_PATH = 
      NCO.class.getPackage().getName().replace('.', '/') + "/nco.rdfs";

    /**
     * Puts the NCO ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNCOOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NCO_RESOURCE_PATH, NCO.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NCO_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NCO */
    public static final URI NS_NCO = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#");
    /**
     * Type: Class <br/>
     * Label: Affiliation  <br/>
     * Comment: Aggregates three properties defined in RFC2426. Originally all three were attached directly to a person. One person could have only one title and one role within one organization. This class is intended to lift this limitation.  <br/>
     */
    public static final URI Affiliation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation");
    /**
     * Type: Class <br/>
     * Label: AudioIMAccount  <br/>
     * Comment: An account in an InstantMessaging system capable of real-time audio conversations.  <br/>
     */
    public static final URI AudioIMAccount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#AudioIMAccount");
    /**
     * Type: Class <br/>
     * Label: BbsNumber  <br/>
     * Comment: A Bulletin Board System (BBS) phone number. Inspired by the (TYPE=bbsl) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI BbsNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#BbsNumber");
    /**
     * Type: Class <br/>
     * Label: CarPhoneNumber  <br/>
     * Comment: A car phone number. Inspired by the (TYPE=car) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI CarPhoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CarPhoneNumber");
    /**
     * Type: Class <br/>
     * Label: CellPhoneNumber  <br/>
     * Comment: A cellular phone number. Inspired by the (TYPE=cell) parameter of the TEL property as defined in RFC 2426 sec  3.3.1. Usually a cellular phone can accept voice calls as well as textual messages (SMS), therefore this class has two superclasses.  <br/>
     */
    public static final URI CellPhoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#CellPhoneNumber");
    /**
     * Type: Class <br/>
     * Label: Contact  <br/>
     * Comment: A Contact. A piece of data that can provide means to identify or communicate with an entity.  <br/>
     */
    public static final URI Contact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact");
    /**
     * Type: Class <br/>
     * Label: ContactGroup  <br/>
     * Comment: A group of Contacts. Could be used to express a group in an addressbook or on a contact list of an IM application.  <br/>
     */
    public static final URI ContactGroup = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup");
    /**
     * Type: Class <br/>
     * Label: ContactListDataObject  <br/>
     * Comment: An entity occuring on a contact list (usually interpreted as an nco:Contact)  <br/>
     */
    public static final URI ContactListDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactListDataObject");
    /**
     * Type: Class <br/>
     * Label: ContactMedium  <br/>
     * Comment: A superclass for all contact media - ways to contact an entity represented by a Contact instance. Some of the subclasses of this class (the various kinds of telephone numbers and postal addresses) have been inspired by the values of the TYPE parameter of ADR and TEL properties defined in RFC 2426 sec. 3.2.1. and 3.3.1 respectively. Each value is represented by an appropriate subclass with two major exceptions TYPE=home and TYPE=work. They are to be expressed by the roles these contact media are attached to i.e. contact media with TYPE=home parameter are to be attached to the default role (nco:Contact or nco:PersonContact), whereas media with TYPE=work parameter should be attached to nco:Affiliation or nco:OrganizationContact.  <br/>
     */
    public static final URI ContactMedium = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium");
    /**
     * Type: Class <br/>
     * Label: DomesticDeliveryAddress  <br/>
     * Comment: Domestic Delivery Addresse. Class inspired by TYPE=dom parameter of the ADR property defined in RFC 2426 sec. 3.2.1  <br/>
     */
    public static final URI DomesticDeliveryAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#DomesticDeliveryAddress");
    /**
     * Type: Class <br/>
     * Label: EmailAddress  <br/>
     * Comment: An email address.  <br/>
     */
    public static final URI EmailAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress");
    /**
     * Type: Class <br/>
     * Label: FaxNumber  <br/>
     * Comment: A fax number. Inspired by the (TYPE=fax) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI FaxNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#FaxNumber");
    /**
     * Type: Class <br/>
     * Label: Gender  <br/>
     * Comment: Gender. Instances of this class may include male and female.  <br/>
     */
    public static final URI Gender = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender");
    /**
     * Type: Class <br/>
     * Label: IMAccount  <br/>
     * Comment: An account in an Instant Messaging system.  <br/>
     */
    public static final URI IMAccount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount");
    /**
     * Type: Class <br/>
     * Label: InternationalDeliveryAddress  <br/>
     * Comment: International Delivery Addresse. Class inspired by TYPE=intl parameter of the ADR property defined in RFC 2426 sec. 3.2.1  <br/>
     */
    public static final URI InternationalDeliveryAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#InternationalDeliveryAddress");
    /**
     * Type: Class <br/>
     * Label: IsdnNumber  <br/>
     * Comment: An ISDN phone number. Inspired by the (TYPE=isdn) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI IsdnNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IsdnNumber");
    /**
     * Type: Class <br/>
     * Label: MessagingNumber  <br/>
     * Comment: A number that can accept textual messages.  <br/>
     */
    public static final URI MessagingNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#MessagingNumber");
    /**
     * Type: Class <br/>
     * Label: ModemNumber  <br/>
     * Comment: A modem phone number. Inspired by the (TYPE=modem) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI ModemNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ModemNumber");
    /**
     * Type: Class <br/>
     * Label: OrganizationContact  <br/>
     * Comment: A Contact that denotes on Organization.  <br/>
     */
    public static final URI OrganizationContact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact");
    /**
     * Type: Class <br/>
     * Label: PagerNumber  <br/>
     * Comment: A pager phone number. Inspired by the (TYPE=pager) parameter of the TEL property as defined in RFC 2426 sec  3.3.1.  <br/>
     */
    public static final URI PagerNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PagerNumber");
    /**
     * Type: Class <br/>
     * Label: ParcelDeliveryAddress  <br/>
     * Comment: Parcel Delivery Addresse. Class inspired by TYPE=parcel parameter of the ADR property defined in RFC 2426 sec. 3.2.1  <br/>
     */
    public static final URI ParcelDeliveryAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ParcelDeliveryAddress");
    /**
     * Type: Class <br/>
     * Label: PcsNumber  <br/>
     * Comment: Personal Communication Services Number. A class inspired by the TYPE=pcs parameter of the TEL property defined in RFC 2426 sec. 3.3.1  <br/>
     */
    public static final URI PcsNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PcsNumber");
    /**
     * Type: Class <br/>
     * Label: PersonContact  <br/>
     * Comment: A Contact that denotes a Person. A person can have multiple Affiliations.  <br/>
     */
    public static final URI PersonContact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact");
    /**
     * Type: Class <br/>
     * Label: PhoneNumber  <br/>
     * Comment: A telephone number.  <br/>
     */
    public static final URI PhoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber");
    /**
     * Type: Class <br/>
     * Label: PostalAddress  <br/>
     * Comment: A postal address. A class aggregating the various parts of a value for the 'ADR' property as defined in RFC 2426 Sec. 3.2.1.  <br/>
     */
    public static final URI PostalAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress");
    /**
     * Type: Class <br/>
     * Label: Role  <br/>
     * Comment: A role played by a contact. Contacts that denote people, can have many roles (e.g. see the hasAffiliation property and Affiliation class). Contacts that denote Organizations or other Agents usually have one role.  Each role can introduce additional contact media.  <br/>
     */
    public static final URI Role = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role");
    /**
     * Type: Class <br/>
     * Label: VideoIMAccount  <br/>
     * Comment: An account in an instant messaging system capable of video conversations.  <br/>
     */
    public static final URI VideoIMAccount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VideoIMAccount");
    /**
     * Type: Class <br/>
     * Label: VideoTelephoneNumber  <br/>
     * Comment: A Video telephone number. A class inspired by the TYPE=video parameter of the TEL property defined in RFC 2426 sec. 3.3.1  <br/>
     */
    public static final URI VideoTelephoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VideoTelephoneNumber");
    /**
     * Type: Class <br/>
     * Label: VoicePhoneNumber  <br/>
     * Comment: A telephone number with voice communication capabilities. Class inspired by the TYPE=voice parameter of the TEL property defined in RFC 2426 sec. 3.3.1  <br/>
     */
    public static final URI VoicePhoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VoicePhoneNumber");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender <br/>
     * Label: female  <br/>
     * Comment: A Female  <br/>
     */
    public static final URI female = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#female");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender <br/>
     * Label: male  <br/>
     * Comment: A Male  <br/>
     */
    public static final URI male = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#male");
    /**
     * Type: Property <br/>
     * Label: addressLocation  <br/>
     * Comment: The geographical location of a postal address.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point  <br/>
     */
    public static final URI addressLocation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#addressLocation");
    /**
     * Type: Property <br/>
     * Label: birthDate  <br/>
     * Comment: Birth date of the object represented by this Contact. An equivalent of the 'BDAY' property as defined in RFC 2426 Sec. 3.1.5.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#date  <br/>
     */
    public static final URI birthDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#birthDate");
    /**
     * Type: Property <br/>
     * Label: blogUrl  <br/>
     * Comment: A Blog url.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI blogUrl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#blogUrl");
    /**
     * Type: Property <br/>
     * Label: contactGroupName  <br/>
     * Comment: The name of the contact group. This property was NOT defined 
    in the VCARD standard. See documentation of the 'ContactGroup' class for 
    details  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contactGroupName = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactGroupName");
    /**
     * Type: Property <br/>
     * Label: contactMediumComment  <br/>
     * Comment: A comment about the contact medium.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contactMediumComment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactMediumComment");
    /**
     * Type: Property <br/>
     * Label: contactUID  <br/>
     * Comment: A value that represents a globally unique  identifier corresponding to the individual or resource associated with the Contact. An equivalent of the 'UID' property defined in RFC 2426 Sec. 3.6.7  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contactUID = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contactUID");
    /**
     * Type: Property <br/>
     * Label: containsContact  <br/>
     * Comment: A property used to group contacts into contact groups. This 
    property was NOT defined in the VCARD standard. See documentation for the 
    'ContactGroup' class for details  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactGroup  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactListDataObject  <br/>
     */
    public static final URI containsContact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#containsContact");
    /**
     * Type: Property <br/>
     * Label: contributor  <br/>
     * Comment: An entity responsible for making contributions to the content of the InformationElement.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI contributor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#contributor");
    /**
     * Type: Property <br/>
     * Label: country  <br/>
     * Comment: A part of an address specyfing the country. Inspired by the seventh part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI country = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#country");
    /**
     * Type: Property <br/>
     * Label: creator  <br/>
     * Comment: Creator of a data object, an entity primarily responsible for the creation of the content of the data object.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI creator = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#creator");
    /**
     * Type: Property <br/>
     * Label: department  <br/>
     * Comment: Department. The organizational unit within the organization.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI department = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#department");
    /**
     * Type: Property <br/>
     * Label: emailAddress  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI emailAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#emailAddress");
    /**
     * Type: Property <br/>
     * Label: extendedAddress  <br/>
     * Comment: An extended part of an address. This field might be used to express parts of an address that aren't include in the name of the Contact but also aren't part of the actual location. Usually the streed address and following fields are enough for a postal letter to arrive. Examples may include ('University of California Campus building 45', 'Sears Tower 34th floor' etc.) Inspired by the second part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI extendedAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#extendedAddress");
    /**
     * Type: Property <br/>
     * Label: foafUrl  <br/>
     * Comment: The URL of the FOAF file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI foafUrl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#foafUrl");
    /**
     * Type: Property <br/>
     * Label: fullname  <br/>
     * Comment: To specify the formatted text corresponding to the name of the object the Contact represents. An equivalent of the FN property as defined in RFC 2426 Sec. 3.1.1.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fullname = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#fullname");
    /**
     * Type: Property <br/>
     * Label: gender  <br/>
     * Comment: Gender of the given contact.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Gender  <br/>
     */
    public static final URI gender = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#gender");
    /**
     * Type: Property <br/>
     * Label: hasAffiliation  <br/>
     * Comment: Links a PersonContact with an Affiliation.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation  <br/>
     */
    public static final URI hasAffiliation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasAffiliation");
    /**
     * Type: Property <br/>
     * Label: hasContactMedium  <br/>
     * Comment: A superProperty for all properties linking a Contact to an instance of a contact medium.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#ContactMedium  <br/>
     */
    public static final URI hasContactMedium = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasContactMedium");
    /**
     * Type: Property <br/>
     * Label: hasEmailAddress  <br/>
     * Comment: An address for electronic mail communication with the object specified by this contact. An equivalent of the 'EMAIL' property as defined in RFC 2426 Sec. 3.3.1.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#EmailAddress  <br/>
     */
    public static final URI hasEmailAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasEmailAddress");
    /**
     * Type: Property <br/>
     * Label: hasIMAccount  <br/>
     * Comment: Indicates that an Instant Messaging account owned by an entity represented by this contact.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     */
    public static final URI hasIMAccount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasIMAccount");
    /**
     * Type: Property <br/>
     * Label: hasLocation  <br/>
     * Comment: Geographical location of the contact. Inspired by the 'GEO' property specified in RFC 2426 Sec. 3.4.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point  <br/>
     */
    public static final URI hasLocation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasLocation");
    /**
     * Type: Property <br/>
     * Label: hasPhoneNumber  <br/>
     * Comment: A number for telephony communication with the object represented by this Contact. An equivalent of the 'TEL' property defined in RFC 2426 Sec. 3.3.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber  <br/>
     */
    public static final URI hasPhoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasPhoneNumber");
    /**
     * Type: Property <br/>
     * Label: hasPostalAddress  <br/>
     * Comment: The default Address for a Contact. An equivalent of the 'ADR' property as defined in RFC 2426 Sec. 3.2.1.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     */
    public static final URI hasPostalAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hasPostalAddress");
    /**
     * Type: Property <br/>
     * Label: hobby  <br/>
     * Comment: A hobby associated with a PersonContact. This property can be used to express hobbies and interests.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI hobby = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#hobby");
    /**
     * Type: Property <br/>
     * Label: imAccountType  <br/>
     * Comment: Type of the IM account. This may be the name of the service that provides the IM functionality. Examples might include Jabber, ICQ, MSN etc  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imAccountType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imAccountType");
    /**
     * Type: Property <br/>
     * Label: imID  <br/>
     * Comment: Identifier of the IM account. Examples of such identifier might include ICQ UINs, Jabber IDs, Skype names etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imID = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imID");
    /**
     * Type: Property <br/>
     * Label: imNickname  <br/>
     * Comment: A nickname attached to a particular IM Account.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imNickname = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imNickname");
    /**
     * Type: Property <br/>
     * Label: imStatus  <br/>
     * Comment: Current status of the given IM account. Values for this property may include 'Online', 'Offline', 'Do not disturb' etc. The exact choice of them is unspecified.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imStatus");
    /**
     * Type: Property <br/>
     * Label: imStatusMessage  <br/>
     * Comment: A feature common in most IM systems. A message left by the user for all his/her contacts to see.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#IMAccount  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imStatusMessage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#imStatusMessage");
    /**
     * Type: Property <br/>
     * Label: key  <br/>
     * Comment: An encryption key attached to a contact. Inspired by the KEY property defined in RFC 2426 sec. 3.7.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI key = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#key");
    /**
     * Type: Property <br/>
     * Label: locality  <br/>
     * Comment: Locality or City. Inspired by the fourth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI locality = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#locality");
    /**
     * Type: Property <br/>
     * Label: logo  <br/>
     * Comment: Logo of a company. Inspired by the LOGO property defined in RFC 2426 sec. 3.5.3  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI logo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#logo");
    /**
     * Type: Property <br/>
     * Label: nameAdditional  <br/>
     * Comment: Additional given name of an object represented by this contact. See documentation for 'nameFamily' property for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nameAdditional = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameAdditional");
    /**
     * Type: Property <br/>
     * Label: nameFamily  <br/>
     * Comment: The family name of an Object represented by this Contact. These applies to people that have more than one given name. The 'first' one is considered 'the' given name (see nameGiven) property. All additional ones are considered 'additional' names. The name inherited from parents is the 'family name'. e.g. For Dr. John Phil Paul Stevenson Jr. M.D. A.C.P. we have contact with: honorificPrefix: 'Dr.', nameGiven: 'John', nameAdditional: 'Phil', nameAdditional: 'Paul', nameFamily: 'Stevenson', honorificSuffix: 'Jr.', honorificSuffix: 'M.D.', honorificSuffix: 'A.C.P.'. These properties form an equivalent of the compound 'N' property as defined in RFC 2426 Sec. 3.1.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nameFamily = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameFamily");
    /**
     * Type: Property <br/>
     * Label: nameGiven  <br/>
     * Comment: The given name for the object represented by this Contact. See documentation for 'nameFamily' property for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nameGiven = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameGiven");
    /**
     * Type: Property <br/>
     * Label: nameHonorificPrefix  <br/>
     * Comment: A prefix for the name of the object represented by this Contact. See documentation for the 'nameFamily' property for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nameHonorificPrefix = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameHonorificPrefix");
    /**
     * Type: Property <br/>
     * Label: nameHonorificSuffix  <br/>
     * Comment: A suffix for the name of the Object represented by the given object. See documentation for the 'nameFamily' for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PersonContact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nameHonorificSuffix = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nameHonorificSuffix");
    /**
     * Type: Property <br/>
     * Label: nickname  <br/>
     * Comment: A nickname of the Object represented by this Contact. This is an equivalen of the 'NICKNAME' property as defined in RFC 2426 Sec. 3.1.3.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI nickname = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#nickname");
    /**
     * Type: Property <br/>
     * Label: note  <br/>
     * Comment: A note about the object represented by this Contact. An equivalent for the 'NOTE' property defined in RFC 2426 Sec. 3.6.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI note = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#note");
    /**
     * Type: Property <br/>
     * Label: org  <br/>
     * Comment: Name of an organization or a unit within an organization the object represented by a Contact is associated with. An equivalent of the 'ORG' property defined in RFC 2426 Sec. 3.5.5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#OrganizationContact  <br/>
     */
    public static final URI org = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#org");
    /**
     * Type: Property <br/>
     * Label: phoneNumber  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PhoneNumber  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI phoneNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#phoneNumber");
    /**
     * Type: Property <br/>
     * Label: photo  <br/>
     * Comment: Photograph attached to a Contact. Inspired by the PHOTO property defined in RFC 2426 sec. 3.1.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI photo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#photo");
    /**
     * Type: Property <br/>
     * Label: pobox  <br/>
     * Comment: Post office box. This is the first part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pobox = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#pobox");
    /**
     * Type: Property <br/>
     * Label: postalcode  <br/>
     * Comment: Postal Code. Inspired by the sixth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI postalcode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#postalcode");
    /**
     * Type: Property <br/>
     * Label: publisher  <br/>
     * Comment: An entity responsible for making the InformationElement available.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#InformationElement  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI publisher = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#publisher");
    /**
     * Type: Property <br/>
     * Label: region  <br/>
     * Comment: Region. Inspired by the fifth part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI region = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#region");
    /**
     * Type: Property <br/>
     * Label: representative  <br/>
     * Comment: An object that represent an object represented by this Contact. Usually this property is used to link a Contact to an organization, to a contact to the representative of this organization the user directly interacts with. An equivalent for the 'AGENT' property defined in RFC 2426 Sec. 3.5.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI representative = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#representative");
    /**
     * Type: Property <br/>
     * Label: role  <br/>
     * Comment: Role an object represented by this contact represents in the organization. This might include 'Programmer', 'Manager', 'Sales Representative'. Be careful to avoid confusion with the title property. An equivalent of the 'ROLE' property as defined in RFC 2426. Sec. 3.5.2. Note the difference between nco:Role class and nco:role property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI role = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#role");
    /**
     * Type: Property <br/>
     * Label: sound  <br/>
     * Comment: Sound clip attached to a Contact. Inspired by the SOUND property defined in RFC 2425 sec. 3.6.6.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI sound = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#sound");
    /**
     * Type: Property <br/>
     * Label: streetAddress  <br/>
     * Comment: The streed address. Inspired by the third part of the value of the 'ADR' property as defined in RFC 2426, sec. 3.2.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#PostalAddress  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI streetAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#streetAddress");
    /**
     * Type: Property <br/>
     * Label: title  <br/>
     * Comment: The official title  the object represented by this contact in an organization. E.g. 'CEO', 'Director, Research and Development', 'Junior Software Developer/Analyst' etc. An equivalent of the 'TITLE' property defined in RFC 2426 Sec. 3.5.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Affiliation  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI title = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#title");
    /**
     * Type: Property <br/>
     * Label: url  <br/>
     * Comment: A uniform resource locator associated with the given role of a Contact. Inspired by the 'URL' property defined in RFC 2426 Sec. 3.6.8.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI url = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#url");
    /**
     * Type: Property <br/>
     * Label: voiceMail  <br/>
     * Comment: Indicates if the given number accepts voice mail. (e.g. there is an answering machine). Inspired by TYPE=msg parameter of the TEL property defined in RFC 2426 sec. 3.3.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#VoicePhoneNumber  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI voiceMail = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#voiceMail");
    /**
     * Type: Property <br/>
     * Label: websiteUrl  <br/>
     * Comment: A url of a website.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Role  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI websiteUrl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nco#websiteUrl");
}
