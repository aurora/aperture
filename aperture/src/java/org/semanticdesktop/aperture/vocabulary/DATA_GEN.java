package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Thu Sep 28 15:57:50 CEST 2006
 * input file: doc/ontology/data.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/data#
 */
public class DATA_GEN {
	public static final String NS = "http://aperture.semanticdesktop.org/ontology/data#";

    /**
     * Label: AddressBookEntry 
     * Comment: An addressbook-entry, for example from the Apple address book or from Microsoft Outlook. 
     */
    public static final URI AddressBookEntry = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#AddressBookEntry");

    /**
     * Label: Agent 
     */
    public static final URI Agent = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#Agent");

    /**
     * Label: DataObject 
     * Comment: A resource that contains some information. 
     */
    public static final URI DataObject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#DataObject");

    /**
     * Label: Document 
     */
    public static final URI Document = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#Document");

    /**
     * Label: Email 
     */
    public static final URI Email = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#Email");

    /**
     * Label: FileDataObject 
     * Comment: A file-based data object. May be a website, a file from filesystem or any other file representable as a stream. Has a size and some content, that is extracted. 
     */
    public static final URI FileDataObject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#FileDataObject");

    /**
     * Label: FolderDataObject 
     */
    public static final URI FolderDataObject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#FolderDataObject");

    /**
     * Label: MSOLDistList 
     * Comment: A distribution list from outlook 
     */
    public static final URI MSOLDistList = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#MSOLDistList");

    /**
     * Label: MSOLNote 
     */
    public static final URI MSOLNote = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#MSOLNote");

    /**
     * Label: MSOLTask 
     */
    public static final URI MSOLTask = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#MSOLTask");

    /**
     * Label: MSOutlookObject 
     */
    public static final URI MSOutlookObject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#MSOutlookObject");

    /**
     * Label: bcc 
     * Comment: e-mail: BCC 
     * Domain: Email 
     * Range: Agent 
     */
    public static final URI bcc = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#bcc");

    /**
     * Label: byteSize 
     * Comment: Size of dataobject in bytes. 
     * Domain: FileDataObject 
     * Range: Literal 
     */
    public static final URI byteSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#byteSize");

    /**
     * Label: cc 
     * Comment: e-mail: CC 
     * Domain: Email 
     * Range: Agent 
     */
    public static final URI cc = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#cc");

    /**
     * Label: characterSet 
     * Comment: Characterset in which the content of the document was created. Example: ISO-8859-1, UTF-8. One of the registered character sets at http://www.iana.org/assignments/character-sets 
     * Domain: FileDataObject 
     * Range: Literal 
     */
    public static final URI characterSet = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#characterSet");

    /**
     * Label: contentMimeType 
     * Comment: Key used to store the MIME type of the content of an object when it is different from the object's main MIME type. This value can be used, for example, to model an e-mail message whose mime type is"message/rfc822", but whose content has type "text/html". If not specified, the MIME type of the
content defaults to the value specified by the 'mimeType' property. 
     * Domain: FileDataObject 
     * Range: Literal 
     */
    public static final URI contentMimeType = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#contentMimeType");

    /**
     * Label: created 
     * Comment: DublinCore: Date of creation of the resource. See 'date' for more details. 
     * Range: Literal 
     */
    public static final URI created = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#created");

    /**
     * Label: creator 
     * Comment: Dublin Core: An entity primarily responsible for making the content of the resource. Examples of a Creator include a person, an organisation, or a service. Typically, the name of a Creator should be used to indicate the entity. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI creator = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#creator");

    /**
     * Label: dataSource 
     * Comment: The datasource this data-object came from. 
     * Domain: DataObject 
     * Range: Resource 
     */
    public static final URI dataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#dataSource");

    /**
     * Label: date 
     * Comment: The general "date", typically last modification or publication date. See sub-properties "modified", "created" for more detailed dates. Date on which the resource was changed. Conforms to Xml-Schema dateTime and therefore also ISO 8601. See http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#dateTime.  See http://www.iso.ch/markete/8601.pdf. To code: SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI date = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#date");

    /**
     * Label: dateAsNumber 
     * Comment: The date expressed as a number, measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970). 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI dateAsNumber = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#dateAsNumber");

    /**
     * Label: description 
     * Comment: DublinCore: An account of the content of the resource. Description may include but is not limited to: an abstract, table of contents, reference to a graphical representation of content or a free-text account of the content. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI description = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#description");

    /**
     * Label: emailAddress 
     * Domain: Agent 
     * Range: Literal 
     */
    public static final URI emailAddress = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#emailAddress");

    /**
     * Label: expirationDate 
     * Comment: Date the resource expires (especially needed for web-resources). See 'date' for more details. 
     * Range: Literal 
     */
    public static final URI expirationDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#expirationDate");

    /**
     * Label: from 
     * Comment: sender of the e-mail 
     * Domain: Email 
     * Range: Agent 
     */
    public static final URI from = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#from");

    /**
     * Label: fullText 
     * Comment: Fulltext of the resource. Plain-text representation with all markup removed. This text can be displayed and used to feed search engines. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI fullText = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#fullText");

    /**
     * Label: generator 
     * Comment: The application that created the resource. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI generator = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#generator");

    /**
     * Label: group 
     * Comment: The group of this entry, most address books lets you organize entries by groups "work", "personal", etc. This property is for representing this group. 
     * Domain: AddressBookEntry 
     * Range: Literal 
     */
    public static final URI group = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#group");

    /**
     * Label: homepage 
     * Comment: The homepage url of the Agent/Person 
     * Domain: Agent 
     * Range: Literal 
     */
    public static final URI homepage = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#homepage");

    /**
     * Label: keyword 
     * Comment: Adapted DublinCore: The topic of the content of the resource, as keyword. No sentences here. Recommended best practice is to select a value from a controlled vocabulary or formal classification scheme. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI keyword = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#keyword");

    /**
     * Label: language 
     * Comment: DublinCore: A language of the intellectual content of the resource. Recommended best practice is to use RFC 3066, which, in conjunction with ISO 639, defines two- and three-letter primary language tags with optional subtags. Examples include "en" or "eng" for English, "akk" for Akkadian, and "en-GB" for English used in the United Kingdom. See also: http://www.ietf.org/rfc/rfc3066.txt 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI language = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#language");

    /**
     * Label: linksTo 
     * Comment: this document links to another - for example links on webpages 
     * Domain: FileDataObject 
     * Range: DataObject 
     */
    public static final URI linksTo = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#linksTo");

    /**
     * Label: messageID 
     * Comment: The mailbox messageID of this message. 
     * Domain: Email 
     * Range: Literal 
     */
    public static final URI messageID = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#messageID");

    /**
     * Label: mimeType 
     * Comment: The mime type of the resource, if available. Example: "text/plain". See http://www.iana.org/assignments/media-types/ 
     * Domain: FileDataObject 
     * Range: Literal 
     */
    public static final URI mimeType = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#mimeType");

    /**
     * Label: modified 
     * Comment: DublinCore: Date on which the resource was changed. See 'date' for more details. 
     * Range: Literal 
     */
    public static final URI modified = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#modified");

    /**
     * Label: msOLUID 
     * Comment: unique identifier created by Microsoft Outlook 
     * Domain: MSOutlookObject 
     * Range: Literal 
     */
    public static final URI msOLUID = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msOLUID");

    /**
     * Label: msolCompletedDate 
     * Comment: Task was completed then. 
     * Domain: MSOLTask 
     * Range: Literal 
     */
    public static final URI msolCompletedDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msolCompletedDate");

    /**
     * Label: msolDueDate 
     * Domain: MSOLTask 
     * Range: Literal 
     */
    public static final URI msolDueDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msolDueDate");

    /**
     * Label: name 
     * Comment: Name of a DataObject or an Agent. File names, folder names, attachment names. In contrast to title, this is the name of the file itself whereas title is the heading inside the content. 
     * Domain: Agent DataObject 
     * Range: Literal 
     */
    public static final URI name = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#name");

    /**
     * Label: pageCount 
     * Comment: Number of pages if printed. 
     * Domain: Document 
     * Range: Literal 
     */
    public static final URI pageCount = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#pageCount");

    /**
     * Label: partOf 
     * Comment: This file is part-of a folder or other container. 
     * Domain: FileDataObject 
     * Range: FolderDataObject 
     */
    public static final URI partOf = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#partOf");

    /**
     * Label: printDate 
     * Comment: Date the resource was last printed (only supported by OpenDocument format, candidate for removal here). See 'date' for more details. 
     * Range: Literal 
     */
    public static final URI printDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#printDate");

    /**
     * Label: receivedDate 
     * Comment: Date when this e-mail was received. 
     * Domain: Email 
     * Range: Literal 
     */
    public static final URI receivedDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#receivedDate");

    /**
     * Label: redirectsTo 
     * Comment: this URI redirects to another - for example redirecting HTTP URLs 
     * Domain: DataObject 
     * Range: DataObject 
     */
    public static final URI redirectsTo = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#redirectsTo");

    /**
     * Label: retrievalDate 
     * Comment: Date when the resource was retrieved by Aperture. See 'date' for more details. 
     * Range: Literal 
     */
    public static final URI retrievalDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#retrievalDate");

    /**
     * Label: rootFolderOf 
     * Comment: This property specifies that this folder is the root folder of a particular datasource. 
     * Domain: FolderDataObject 
     * Range: Resource 
     */
    public static final URI rootFolderOf = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#rootFolderOf");

    /**
     * Label: sender 
     * Comment: (from RFC 822)
This field contains the authenticated identity  of  the  AGENT (person,  system  or  process)  that sends the message.  It is intended for use when the sender is not the author of the message,  or  to  indicate  who among a group of authors actually sent the message.  If the contents of the "Sender" field would be  completely  redundant  with  the  "From"  field,  then the "Sender" field need not be present and its use is  discouraged (though  still legal). 
     * Domain: Email 
     * Range: Agent 
     */
    public static final URI sender = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#sender");

    /**
     * Label: sentDate 
     * Comment: Date when this e-mail was sent. 
     * Domain: Email 
     * Range: Literal 
     */
    public static final URI sentDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#sentDate");

    /**
     * Label: subject 
     * Comment: DublinCore: The topic of the content of the resource. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI subject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#subject");

    /**
     * Label: title 
     * Comment: DublinCore: A name given to the resource. Typically, a Title will be a name by which the resource is formally known.
Difference to name is that title is the human readable title of the resurce whereas name is the filename. 
     * Domain: DataObject 
     * Range: Literal 
     */
    public static final URI title = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#title");

    /**
     * Label: to 
     * Comment: e-mail: TO 
     * Range: Literal 
     */
    public static final URI to = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#to");

}
