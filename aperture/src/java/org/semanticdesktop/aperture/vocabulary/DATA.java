package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Feb 26 19:53:37 CET 2006
 * input file: doc/ontology/data.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/data#
 */
public class DATA {
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
     */
    public static final URI bcc = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#bcc");

    /**
     * Label: byteSize 
     * Comment: Size of dataobject in bytes. 
     */
    public static final URI byteSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#byteSize");

    /**
     * Label: cc 
     * Comment: e-mail: CC 
     */
    public static final URI cc = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#cc");

    /**
     * Label: characterSet 
     * Comment: Characterset in which the content of the document was created. Example: ISO-8859-1, UTF-8. One of the registered character sets at http://www.iana.org/assignments/character-sets 
     */
    public static final URI characterSet = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#characterSet");

    /**
     * Label: contentMimeType 
     * Comment: Key used to store the MIME type of the content of an object when it is different from the object's main MIME type. This value can be used, for example, to model an e-mail message whose mime type is"message/rfc822", but whose content has type "text/html". If not specified, the MIME type of the
content defaults to the value specified by the 'mimeType' property. 
     */
    public static final URI contentMimeType = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#contentMimeType");

    /**
     * Label: created 
     * Comment: DublinCore: Date of creation of the resource. See 'date' for more details. 
     */
    public static final URI created = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#created");

    /**
     * Label: creator 
     * Comment: Dublin Core: An entity primarily responsible for making the content of the resource. Examples of a Creator include a person, an organisation, or a service. Typically, the name of a Creator should be used to indicate the entity. 
     */
    public static final URI creator = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#creator");

    /**
     * Label: date 
     * Comment: The general "date", typically last modification or publication date. See sub-properties "modified", "created" for more detailed dates. Date on which the resource was changed. Conforms to Xml-Schema dateTime and therefore also ISO 8601. See http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#dateTime.  See http://www.iso.ch/markete/8601.pdf. To code: SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
     */
    public static final URI date = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#date");

    /**
     * Label: description 
     * Comment: DublinCore: An account of the content of the resource. Description may include but is not limited to: an abstract, table of contents, reference to a graphical representation of content or a free-text account of the content. 
     */
    public static final URI description = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#description");

    /**
     * Label: emailAddress 
     */
    public static final URI emailAddress = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#emailAddress");

    /**
     * Label: expirationDate 
     * Comment: Date the resource expires (especially needed for web-resources). See 'date' for more details. 
     */
    public static final URI expirationDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#expirationDate");

    /**
     * Label: from 
     * Comment: sender of the e-mail 
     */
    public static final URI from = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#from");

    /**
     * Label: fullText 
     * Comment: Fulltext of the resource. Plain-text representation with all markup removed. This text can be displayed and used to feed search engines. 
     */
    public static final URI fullText = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#fullText");

    /**
     * Label: generator 
     * Comment: The application that created the resource. 
     */
    public static final URI generator = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#generator");

    /**
     * Label: homepage 
     * Comment: The homepage url of the Agent/Person 
     */
    public static final URI homepage = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#homepage");

    /**
     * Label: keyword 
     * Comment: Adapted DublinCore: The topic of the content of the resource, as keyword. No sentences here. Recommended best practice is to select a value from a controlled vocabulary or formal classification scheme. 
     */
    public static final URI keyword = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#keyword");

    /**
     * Label: language 
     * Comment: DublinCore: A language of the intellectual content of the resource. Recommended best practice is to use RFC 3066, which, in conjunction with ISO 639, defines two- and three-letter primary language tags with optional subtags. Examples include "en" or "eng" for English, "akk" for Akkadian, and "en-GB" for English used in the United Kingdom. See also: http://www.ietf.org/rfc/rfc3066.txt 
     */
    public static final URI language = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#language");

    /**
     * Label: mimeType 
     * Comment: The mime type of the resource, if available. Example: "text/plain". See http://www.iana.org/assignments/media-types/ 
     */
    public static final URI mimeType = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#mimeType");

    /**
     * Label: modified 
     * Comment: DublinCore: Date on which the resource was changed. See 'date' for more details. 
     */
    public static final URI modified = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#modified");

    /**
     * Label: msOLUID 
     * Comment: unique identifier created by Microsoft Outlook 
     */
    public static final URI msOLUID = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msOLUID");

    /**
     * Label: msolCompletedDate 
     * Comment: Task was completed then. 
     */
    public static final URI msolCompletedDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msolCompletedDate");

    /**
     * Label: msolDueDate 
     */
    public static final URI msolDueDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#msolDueDate");

    /**
     * Label: name 
     * Comment: Name of a DataObject or an Agent. File names, folder names, attachment names. In contrast to title, this is the name of the file itself whereas title is the heading inside the content. 
     */
    public static final URI name = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#name");

    /**
     * Label: pageCount 
     * Comment: Number of pages if printed. 
     */
    public static final URI pageCount = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#pageCount");

    /**
     * Label: partOf 
     * Comment: This file is part-of a folder or other container. 
     */
    public static final URI partOf = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#partOf");

    /**
     * Label: printDate 
     * Comment: Date the resource was last printed (only supported by OpenDocument format, candidate for removal here). See 'date' for more details. 
     */
    public static final URI printDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#printDate");

    /**
     * Label: receivedDate 
     * Comment: Daten when this e-mail was received. 
     */
    public static final URI receivedDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#receivedDate");

    /**
     * Label: retrievalDate 
     * Comment: Date when the resource was retrieved by Aperture. See 'date' for more details. 
     */
    public static final URI retrievalDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#retrievalDate");

    /**
     * Label: sentDate 
     * Comment: Date when this e-mail was sent. 
     */
    public static final URI sentDate = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#sentDate");

    /**
     * Label: subject 
     * Comment: DublinCore: The topic of the content of the resource. 
     */
    public static final URI subject = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#subject");

    /**
     * Label: title 
     * Comment: DublinCore: A name given to the resource. Typically, a Title will be a name by which the resource is formally known.
Difference to name is that title is the human readable title of the resurce whereas name is the filename. 
     */
    public static final URI title = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#title");

    /**
     * Label: to 
     * Comment: e-mail: TO 
     */
    public static final URI to = new URIImpl("http://aperture.semanticdesktop.org/ontology/data#to");

}
