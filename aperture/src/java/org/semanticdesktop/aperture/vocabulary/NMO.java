/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:35:49 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/nmo.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#
 */
public class NMO {

    /** Path to the ontology resource */
    public static final String NMO_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/nmo.rdfs";

    /**
     * Puts the NMO ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNMOOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NMO_RESOURCE_PATH, NMO.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NMO_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NMO */
    public static final URI NS_NMO = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#");
    /**
     * Type: Class <br/>
     * Label: MessageHeader  <br/>
     * Comment: An arbitrary message header.  <br/>
     */
    public static final URI MessageHeader = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MessageHeader");
    /**
     * Type: Class <br/>
     * Label: Email  <br/>
     * Comment: An email.  <br/>
     */
    public static final URI Email = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Email");
    /**
     * Type: Class <br/>
     * Label: Message  <br/>
     * Comment: A message. Could be an email, instant messanging message, SMS message etc.  <br/>
     */
    public static final URI Message = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message");
    /**
     * Type: Class <br/>
     * Label: MimeEntity  <br/>
     * Comment: A MIME entity, as defined in RFC2045, Section 2.4.  <br/>
     */
    public static final URI MimeEntity = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MimeEntity");
    /**
     * Type: Class <br/>
     * Label: MailboxDataObject  <br/>
     * Comment: An entity encountered in a mailbox. Most common interpretations for such an entity include Message or Folder  <br/>
     */
    public static final URI MailboxDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MailboxDataObject");
    /**
     * Type: Class <br/>
     * Label: IMMessage  <br/>
     * Comment: A message sent with Instant Messaging software.  <br/>
     */
    public static final URI IMMessage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#IMMessage");
    /**
     * Type: Class <br/>
     * Label: Mailbox  <br/>
     * Comment: A mailbox - container for MailboxDataObjects.  <br/>
     */
    public static final URI Mailbox = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Mailbox");
    /**
     * Type: Property <br/>
     * Label: references  <br/>
     * Comment: Signifies that a message references another message. This property is a generic one. See RFC 2822 Sec. 3.6.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     */
    public static final URI references = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#references");
    /**
     * Type: Property <br/>
     * Label: receivedDate  <br/>
     * Comment: Date when this message was received.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI receivedDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#receivedDate");
    /**
     * Type: Property <br/>
     * Label: headerName  <br/>
     * Comment: Name of the message header.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MessageHeader  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI headerName = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#headerName");
    /**
     * Type: Property <br/>
     * Label: hasAttachment  <br/>
     * Comment: Links a message with files that were sent as attachments.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Attachment  <br/>
     */
    public static final URI hasAttachment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#hasAttachment");
    /**
     * Type: Property <br/>
     * Label: replyTo  <br/>
     * Comment: An address where the reply should be sent.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI replyTo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#replyTo");
    /**
     * Type: Property <br/>
     * Label: cc  <br/>
     * Comment: A Contact that is to receive a cc of the email. A cc (carbon copy) is a copy of an email message whose recipient appears on the recipient list, so that all other recipients are aware of it.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Email  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI cc = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#cc");
    /**
     * Type: Property <br/>
     * Label: secondaryRecipient  <br/>
     * Comment: A superproperty for all "additional" recipients of a message.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI secondaryRecipient = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#secondaryRecipient");
    /**
     * Type: Property <br/>
     * Label: messageSubject  <br/>
     * Comment: The subject of a message  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI messageSubject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#messageSubject");
    /**
     * Type: Property <br/>
     * Label: to  <br/>
     * Comment: The primary intended recipient of an email.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Email  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI to = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#to");
    /**
     * Type: Property <br/>
     * Label: primaryRecipient  <br/>
     * Comment: The primary intended recipient of a message.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI primaryRecipient = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#primaryRecipient");
    /**
     * Type: Property <br/>
     * Label: sender  <br/>
     * Comment: The person or agent submitting the message to the network, if other from the one given with the nmo:from property. Defined in RFC 822 sec. 4.4.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI sender = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#sender");
    /**
     * Type: Property <br/>
     * Label: recipient  <br/>
     * Comment: A common superproperty for all properties that link a message with its recipients. Please don't use this property directly.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI recipient = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#recipient");
    /**
     * Type: Property <br/>
     * Label: sentDate  <br/>
     * Comment: Date when this message was sent.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI sentDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#sentDate");
    /**
     * Type: Property <br/>
     * Label: htmlMessageContent  <br/>
     * Comment: HTML representation of the body of the message. For multipart messages, all parts are concatenated into the value of this property. Attachments, whose mimeTypes are different from text/plain or message/rfc822 are considered separate DataObjects and are therefore not included in the value of this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI htmlMessageContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#htmlMessageContent");
    /**
     * Type: Property <br/>
     * Label: messageId  <br/>
     * Comment: An identifier of a message. This property has been inspired by the message-id property defined in RFC 2822, Sec. 3.6.4. It should be used for all kinds of identifiers used by various messaging applications to connect multiple messages into conversations.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI messageId = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#messageId");
    /**
     * Type: Property <br/>
     * Label: headerValue  <br/>
     * Comment: Value of the message header.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MessageHeader  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI headerValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#headerValue");
    /**
     * Type: Property <br/>
     * Label: inReplyTo  <br/>
     * Comment: Signifies that a message is a reply to another message. This feature is commonly used to link messages into conversations. Note that it is more specific than nmo:references. See RFC 2822 sec. 3.6.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     */
    public static final URI inReplyTo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#inReplyTo");
    /**
     * Type: Property <br/>
     * Label: from  <br/>
     * Comment: The sender of the message  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI from = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#from");
    /**
     * Type: Property <br/>
     * Label: messageHeader  <br/>
     * Comment: Links the message wiith an arbitrary message header.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MessageHeader  <br/>
     */
    public static final URI messageHeader = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#messageHeader");
    /**
     * Type: Property <br/>
     * Label: plainTextMessageContent  <br/>
     * Comment: Plain text representation of the body of the message. For multipart messages, all parts are concatenated into the value of this property. Attachments, whose mimeTypes are different from text/plain or message/rfc822 are considered separate DataObjects and are therefore not included in the value of this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Message  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI plainTextMessageContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#plainTextMessageContent");
    /**
     * Type: Property <br/>
     * Label: isRead  <br/>
     * Comment: A flag that states the fact that a MailboxDataObject has been read.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#MailboxDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI isRead = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#isRead");
    /**
     * Type: Property <br/>
     * Label: bcc  <br/>
     * Comment: A Contact that is to receive a bcc of the email. A Bcc (blind carbon copy) is a copy of an email message sent to a recipient whose email address does not appear in the message.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Email  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI bcc = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#bcc");
    /**
     * Type: Property <br/>
     * Label: contentMimeType  <br/>
     * Comment: Key used to store the MIME type of the content of an object when it is different from the object's main MIME type. This value can be used, for example, to model an e-mail message whose mime type is"message/rfc822", but whose content has type "text/html". If not specified, the MIME type of the
content defaults to the value specified by the 'mimeType' property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#Email  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentMimeType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#contentMimeType");
}
