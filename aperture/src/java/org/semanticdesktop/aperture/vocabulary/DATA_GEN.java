package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Nov 29 11:56:03 CET 2006
 * input file: doc/ontology/data.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/data#
 */
public class DATA_GEN {
	public static final String NS = "http://aperture.semanticdesktop.org/ontology/data#";

    /**
     * Label: AddressBookEntry 
     * Comment: An addressbook-entry, for example from the Apple address book or from Microsoft Outlook. 
     */
    public static final URI AddressBookEntry = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#AddressBookEntry");

    /**
     * Label: Agent 
     */
    public static final URI Agent = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#Agent");

    /**
     * Label: DataObject 
     * Comment: A resource that contains some information. 
     */
    public static final URI DataObject = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#DataObject");

    /**
     * Label: Document 
     */
    public static final URI Document = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#Document");

    /**
     * Label: Email 
     */
    public static final URI Email = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#Email");

    /**
     * Label: FileDataObject 
     * Comment: A file-based data object. May be a website, a file from filesystem or any other file representable as a stream. Has a size and some content, that is extracted. 
     */
    public static final URI FileDataObject = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#FileDataObject");

    /**
     * Label: FolderDataObject 
     */
    public static final URI FolderDataObject = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#FolderDataObject");

    /**
     * Label: MSOLDistList 
     * Comment: A distribution list from outlook 
     */
    public static final URI MSOLDistList = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#MSOLDistList");

    /**
     * Label: MSOLNote 
     */
    public static final URI MSOLNote = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#MSOLNote");

    /**
     * Label: MSOLTask 
     */
    public static final URI MSOLTask = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#MSOLTask");

    /**
     * Label: MSOutlookObject 
     */
    public static final URI MSOutlookObject = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#MSOutlookObject");

    /**
     * Label: bcc 
     * Comment: e-mail: BCC 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://aperture.semanticdesktop.org/ontology/data#Agent 
     */
    public static final URI bcc = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#bcc");

    /**
     * Label: byteSize 
     * Comment: Size of dataobject in bytes. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI byteSize = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#byteSize");

    /**
     * Label: cc 
     * Comment: e-mail: CC 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://aperture.semanticdesktop.org/ontology/data#Agent 
     */
    public static final URI cc = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#cc");

    /**
     * Label: characterSet 
     * Comment: Characterset in which the content of the document was created. Example: ISO-8859-1, UTF-8. One of the registered character sets at http://www.iana.org/assignments/character-sets 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI characterSet = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#characterSet");

    /**
     * Label: contentMimeType 
     * Comment: Key used to store the MIME type of the content of an object when it is different from the object's main MIME type. This value can be used, for example, to model an e-mail message whose mime type is"message/rfc822", but whose content has type "text/html". If not specified, the MIME type of the
content defaults to the value specified by the 'mimeType' property. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI contentMimeType = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#contentMimeType");

    /**
     * Label: dataSource 
     * Comment: The datasource this data-object came from. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final URI dataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#dataSource");

    /**
     * Label: dateAsNumber 
     * Comment: The date expressed as a number, measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970). 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI dateAsNumber = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#dateAsNumber");

    /**
     * Label: emailAddress 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI emailAddress = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#emailAddress");

    /**
     * Label: expirationDate 
     * Comment: Date the resource expires (especially needed for web-resources). See 'date' for more details. 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI expirationDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#expirationDate");

    /**
     * Label: from 
     * Comment: sender of the e-mail 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://aperture.semanticdesktop.org/ontology/data#Agent 
     */
    public static final URI from = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#from");

    /**
     * Label: fullText 
     * Comment: Fulltext of the resource. Plain-text representation with all markup removed. This text can be displayed and used to feed search engines. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI fullText = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#fullText");

    /**
     * Label: generator 
     * Comment: The application that created the resource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI generator = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#generator");

    /**
     * Label: group 
     * Comment: The group of this entry, most address books lets you organize entries by groups "work", "personal", etc. This property is for representing this group. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#AddressBookEntry 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI group = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#group");

    /**
     * Label: homepage 
     * Comment: The homepage url of the Agent/Person 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Agent 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI homepage = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#homepage");

    /**
     * Label: keyword 
     * Comment: Adapted DublinCore: The topic of the content of the resource, as keyword. No sentences here. Recommended best practice is to select a value from a controlled vocabulary or formal classification scheme. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI keyword = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#keyword");

    /**
     * Label: linksTo 
     * Comment: this document links to another - for example links on webpages 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     */
    public static final URI linksTo = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#linksTo");

    /**
     * Label: messageID 
     * Comment: The mailbox messageID of this message. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI messageID = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#messageID");

    /**
     * Label: mimeType 
     * Comment: The mime type of the resource, if available. Example: "text/plain". See http://www.iana.org/assignments/media-types/ 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI mimeType = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#mimeType");

    /**
     * Label: msOLUID 
     * Comment: unique identifier created by Microsoft Outlook 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#MSOutlookObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI msOLUID = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#msOLUID");

    /**
     * Label: msolCompletedDate 
     * Comment: Task was completed then. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#MSOLTask 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI msolCompletedDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#msolCompletedDate");

    /**
     * Label: msolDueDate 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#MSOLTask 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI msolDueDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#msolDueDate");

    /**
     * Label: name 
     * Comment: Name of a DataObject or an Agent. File names, folder names, attachment names. In contrast to title, this is the name of the file itself whereas title is the heading inside the content. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Agent http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI name = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#name");

    /**
     * Label: pageCount 
     * Comment: Number of pages if printed. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Document 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI pageCount = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#pageCount");

    /**
     * Label: partOf 
     * Comment: This file is part-of a folder or other container. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FileDataObject 
     * Range: http://aperture.semanticdesktop.org/ontology/data#FolderDataObject 
     */
    public static final URI partOf = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#partOf");

    /**
     * Label: printDate 
     * Comment: Date the resource was last printed (only supported by OpenDocument format, candidate for removal here). See 'date' for more details. 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI printDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#printDate");

    /**
     * Label: receivedDate 
     * Comment: Date when this e-mail was received. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI receivedDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#receivedDate");

    /**
     * Label: redirectsTo 
     * Comment: this URI redirects to another - for example redirecting HTTP URLs 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     * Range: http://aperture.semanticdesktop.org/ontology/data#DataObject 
     */
    public static final URI redirectsTo = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#redirectsTo");

    /**
     * Label: retrievalDate 
     * Comment: Date when the resource was retrieved by Aperture. See 'date' for more details. 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI retrievalDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#retrievalDate");

    /**
     * Label: rootFolderOf 
     * Comment: This property specifies that this folder is the root folder of a particular datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#FolderDataObject 
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource 
     */
    public static final URI rootFolderOf = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#rootFolderOf");

    /**
     * Label: sender 
     * Comment: (from RFC 822)
This field contains the authenticated identity  of  the  AGENT (person,  system  or  process)  that sends the message.  It is intended for use when the sender is not the author of the message,  or  to  indicate  who among a group of authors actually sent the message.  If the contents of the "Sender" field would be  completely  redundant  with  the  "From"  field,  then the "Sender" field need not be present and its use is  discouraged (though  still legal). 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://aperture.semanticdesktop.org/ontology/data#Agent 
     */
    public static final URI sender = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#sender");

    /**
     * Label: sentDate 
     * Comment: Date when this e-mail was sent. 
     * Comment: http://aperture.semanticdesktop.org/ontology/data#Email 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI sentDate = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#sentDate");

    /**
     * Label: to 
     * Comment: e-mail: TO 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI to = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#to");

    /**
     * Label: containsId 
     * Comment: Used in ModelAccessData to tie the knownID's with the root node 
     */
    public static final URI containsId = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/data#containsId");

}
