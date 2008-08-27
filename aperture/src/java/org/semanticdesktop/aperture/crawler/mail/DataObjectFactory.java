/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.HtmlParserUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a set of DataObjects from a MimeMessage.
 * 
 * <p>
 * DataObjectFactory interprets the structure of a MimeMessage and creates a list of DataObjects that model
 * its contents in a way that is most natural to users. Practically this means that the DataObject list should
 * be as similar as possible to how mail readers present the mail.
 * 
 * <p>
 * For example, a multipart/alternative message may have a rather complex object structure (a Part with a
 * MultiPart content, on its turn containing two BodyParts), but this is translated to a single DataObject
 * holding all the mail metadata (sender, receiver, etc) as well as an InputStream accessing the simplest of
 * the two body parts (typically the text/plain part).
 * 
 * <p>
 * Known bugs/features:
 * <ul>
 * <li> The DataObjectFactory constructs person URIs that begin with the emailperson: prefix and have the
 * person name in them. This means that if the sender and the receiver have the same name, but different
 * addresses, they will be mapped to the same resource with two different addresses. In this way we lose
 * information about which address was the sending address and which address was the receiving address.</li>
 * <li> Each email has one nie:contentCreated triple. It is either the date the message was sent, or received
 * or the date of the message retrieval. We loose information what kind of date it is, and we loose the
 * timezone information that is in the original email, but gets swallowed during the crawling process.</li>
 * <li> The messageId property is stored in the message metadata with the angle brackets, whereas they
 * probably should be removed.</li>
 * <li> Each message part is equipped with the nie:byteSize property. It is against the definition of NIE mime
 * type because it marks the length of the message content only, not of the message as a whole (i.e. excluding
 * the message headers). In multipart/alternative emails the byte size is the size of both the html and plain
 * parts combined.</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class DataObjectFactory {

    /** Obtains InputStreams from {@link Part} instances. */
    public static interface PartStreamFactory {

        /**
         * Returns an input stream with the part content. It's conceptually a wrapper around the
         * {@link Part#getInputStream()} method, designed to allow for customization of the returned input
         * stream.
         * 
         * @param part
         * @return an InputStream with the content of the part
         * @throws MessagingException
         * @throws IOException
         */
        public InputStream getPartStream(Part part) throws MessagingException, IOException;
    }

    // TODO: we could use the URL format specified in RFC 2192 to construct a proper IMAP4 URL.
    // Right now we use something home-grown for representing attachments rather than isections.
    // To investigate: does JavaMail provide us with enough information for constructing proper
    // URLs for attachments? Perhaps we can create them ourselves by carefully counting BodyParts?

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Key used to store a DataObject's URI in the intermediate HashMap representation.
     */
    private static final String ID_KEY = "id";

    /**
     * Key used to store a DataObject's children in the intermediate HashMap representation.
     */
    private static final String CHILDREN_KEY = "children";

    /**
     * Key used to store a DataObject's content (an InputStream) in the intermediate HashMap representation.
     */
    private static final String CONTENTS_KEY = "contents";

    /**
     * The DataSource that the generated DataObjects will report as source.
     */
    private DataSource dataSource;

    /**
     * The RDFContainerFactory delivering the RDFContainer to be used in the DataObjects.
     */
    private RDFContainerFactory containerFactory;

    /**
     * The PartStreamFactory used to obtain inputStreams.
     */
    private PartStreamFactory streamFactory;

    /**
     * The message this factory has been created for
     */
    private MimeMessage message;

    /**
     * The URI of the message
     */
    private URI messageURI;

    /**
     * The URI of the folder this message is enclosed in (can be null)
     */
    private URI folderUri;

    /**
     * The list of data objects, generated from the message.
     */
    private List<DataObject> dataObjectsToReturn;

    /**
     * The index in that list specifying the data object to be returned on the next call to
     * {@link #getObject()}
     */
    private int currentDataObjectToReturn;

    /**
     * Constructs a data object factory for the given message
     * 
     * @param message
     * @param containerFactory
     * @param streamFactory a factory of streams for mail parts, this parameter can be null, in which case a
     *            default implementation will be used, that simply calls {@link Part#getInputStream()}
     * @param dataSource
     * @param messageUri
     * @param folderUri
     * @throws IOException
     * @throws MessagingException
     */
    public DataObjectFactory(MimeMessage message, RDFContainerFactory containerFactory,
            PartStreamFactory streamFactory, DataSource dataSource, URI messageUri, URI folderUri)
            throws IOException, MessagingException {
        this.message = message;
        this.containerFactory = containerFactory;
        this.dataSource = dataSource;
        this.folderUri = folderUri;
        this.currentDataObjectToReturn = 0;
        this.dataObjectsToReturn = new ArrayList<DataObject>(10); // 10 should be more than enough
        this.messageURI = messageUri;
        
        if (streamFactory == null) {
            this.streamFactory = new PartStreamFactory() {
                public InputStream getPartStream(Part part) throws MessagingException, IOException {
                    return part.getInputStream();
                }
            };
        } else {
            this.streamFactory = streamFactory;
        }
        
        try {
            createDataObjects();
        }
        catch (MessagingException e) {
            disposeRemainingObjects();
            throw e;
        }
        catch (IOException e) {
            disposeRemainingObjects();
            throw e;
        }
    }

    /**
     * Returns a DataObject representing a single message. This data object contains a flattened version of
     * the (arbitrary complex) tree-like MIME structure of the message. This method is called repeatedly for a
     * single MimeMessage. At the first call it creates a cachedDataObjectsMap of all dataObjects that are to
     * be returned from this message. On all subsequent calls DataObjects from this map are returned.
     * 
     * @param message
     * @param url
     * @param folderUri
     * @param dataSource
     * @param newAccessData
     * @param containerFactory
     * @return a DataObject instance for the given message
     * @throws MessagingException
     * @throws IOException
     */
    public DataObject getObject() throws MessagingException, IOException {
        if (currentDataObjectToReturn >= dataObjectsToReturn.size()) {
            return null;
        }
        else {
            DataObject result = dataObjectsToReturn.get(currentDataObjectToReturn);
            currentDataObjectToReturn++;
            return result;
        }
    }

    /**
     * Disposes of the data objects remaining on the list of objects to return. This method must be called if
     * the user stopped calling the getObject() method BEFORE it returned null, thus marking the fact that all
     * objects have been read.
     */
    public void disposeRemainingObjects() {
        for (; currentDataObjectToReturn < dataObjectsToReturn.size(); currentDataObjectToReturn++) {
            try {
                dataObjectsToReturn.get(currentDataObjectToReturn).dispose();
            } catch (Exception e) {
                // this shouldn't stop the factory from disposing other objects
            }
        }
    }

    /**
     * Returns a data object with the given url
     * 
     * @param url
     * @return
     */
    public DataObject getObject(String url) {
        for (DataObject object : dataObjectsToReturn) {
            if (object.getID().toString().equals(url)) {
                return object;
            }
        }
        return null;
    }

    /**
     * Initializes the list of DataObjects that have been created based on the contents of the specified
     * MimeMessage. The order of the DataObjects reflects the order of the message parts they represent, i.e.
     * the first DataObject represents the entire message, subsequent DataObjects represent attachments in a
     * depth first order several layers of attachments can exist when forwarding messages containing
     * attachments).
     * 
     * @throws MessagingException Thrown when accessing the mail contents.
     * @throws IOException Thrown when accessing the mail contents.
     */
    private void createDataObjects() throws MessagingException, IOException {
        // perform a depth-first crawl over the entire structure of the mime message, return hashmaps
        HashMap map = handleMailPart(message, messageURI, MailUtil.getDate(message));
        // convert the HashMap representation to a DataObject representation and add them to the list
        createDataObjects(map, folderUri, dataObjectsToReturn);

        // The first object is the Message itself
        RDFContainer msgContainer = dataObjectsToReturn.get(0).getMetadata();
        // we know that the message itself is an email
        msgContainer.add(RDF.type, NMO.Email);
        // Apart from being a message, it is also a MailboxDataObject
        msgContainer.add(RDF.type, NMO.MailboxDataObject);
        // the messageID property belongs to the message as a whole, that's why it's not called anywhere else
        String messageID = message.getMessageID();
        if (messageID != null) {
            msgContainer.add(NMO.messageId, messageID);
        }
    }

    /* ----------------------------- Methods for MIME interpretation ----------------------------- */

    /**
     * <p>
     * Creates a HashMap representation of this mail part and all its nested parts The hashmap representation
     * of a message contains a set of name-value pairs for each metadata property of a message and a special
     * pair named with the CHILDREN_KEY, whose value is an ArrayList of further HashMaps for the children.
     * This makes the HashMap representation equivalent to the tree-like structure of the MimeMessage. (Which
     * is non-obvious at the first glance - Antoni 06.11.2007)
     * </p>
     * 
     * <p>
     * This particular method tries to distinguish if this part has a multipart content type, or if it is a
     * singular part
     * </p>
     * 
     * @param mailPart the part that is to be converted into a hashmap
     * @param uri the URI of the mail part
     * @param messageCreationDate the date that is to be included as the nie:contentCreationDate triple
     * @return a HashMap representation of the mailPart and its children. The Hashmap has some special keys:
     *         <ul>
     *         <li>ID_KEY - a URI - the identifier of the part</li>
     *         <li>CONTENTS_KEY - an InputStream - the content of the message
     *         <li>CHILDREN_KEY - an ArrayList - contains HashMaps, pertaining to the children of the
     *         mailPart
     *         </ul>
     */
    private HashMap handleMailPart(Part mailPart, URI uri, Date messageCreationDate)
            throws MessagingException, IOException {
        // determine the primary type of this Part
        ContentType contentType = null;
        String primaryType = null;

        String contentTypeStr = null;
        try {
            contentTypeStr = mailPart.getContentType();
        }
        catch (MessagingException me) {
            /*
             * This catch has been added during the work on issue number 2005759. It protects the crawler
             * against servers that have errors in the IMAP protocol implementation and yield exceptions when
             * trying to download the BODYSTRUCTURE
             * 
             * see
             * https://sourceforge.net/tracker/index.php?func=detail&aid=2005759&group_id=150969&atid=779500
             * for a description of the error and http://java.sun.com/products/javamail/FAQ.html#imapserverbug
             * for the description of the workaround I've used.
             */
            if (me.getMessage().contains("Unable to load BODYSTRUCTURE") && mailPart instanceof MimeMessage) {
                mailPart = new MimeMessage((MimeMessage) mailPart);
                contentTypeStr = mailPart.getContentType();
            }
            else {
                throw me;
            }
        }

        if (contentTypeStr != null) {
            contentType = new ContentType(contentTypeStr);
            primaryType = normalizeString(contentType.getPrimaryType());
        }

        // make an exception for multipart mails
        if ("multipart".equals(primaryType)) {
            Object content = mailPart.getContent();
            if (content instanceof Multipart) {
                // content is a container for multiple other parts
                return handleMultipart((Multipart) content, contentType, uri, messageCreationDate);
            }
            else {
                logger.warn("multipart '" + uri + "' does not contain a Multipart object: ");
                return null;
            }
        }
        else {
            return handleSinglePart(mailPart, contentType, uri, messageCreationDate, false);
        }
    }

    /**
     * <p>
     * Creates a HashMap representation of this mail part. This method assumes that the part is singular and
     * has no nested parts, only one HashMap is returned, it does not contain the CHILDREN_KEY
     * </p>
     * 
     * @param mailPart the part that is to be converted into a hashmap
     * @param contentType the type of the content of the mailPart
     * @param uri the URI of the mail part
     * @param messageCreationDate the date that is to be included as the nie:contentCreationDate triple
     * @return a HashMap representation of the mailPart and its children. The Hashmap has some special keys:
     *         <ul>
     *         <li>ID_KEY - the URI identifier of the part</li>
     *         <li>CONTENTS_KEY - an InputStream with the content of the message
     *         </ul>
     */
    private HashMap handleSinglePart(Part mailPart, ContentType contentType, URI uri,
            Date messageCreationDate, boolean emptyContent) throws MessagingException, IOException {
        // determine the content type properties of this mail
        String mimeType = getMimeTypeFromContentType(contentType);
        String charsetStr = getCharsetStringFromContentType(contentType, mimeType);

        HashMap result = null;
        // these three methods are responsible for putting the CONTENTS_KEY in the result hash map
        // i.e. for the extraction of the actual content stream
        if (emptyContent) {
            // we explicitly don't need any content from this mail part
            result = handleEmptyContentSinglePart(uri);
        }
        else if ("message/rfc822".equals(mimeType)) {
            // the part is a message in itself, we need to crawl into it
            return handleRfc822SinglePart(mailPart, uri);
        }
        else {
            // it is a normal single part, it may be a forwarded message or an attachment, or the message itself
            result = handleNormalSinglePart(mailPart, charsetStr, mimeType, uri, messageCreationDate);
        }

        extractGenericSinglePartMetadata(mailPart, result, messageCreationDate);

        // Differentiate between Messages and other types of mail parts. We don't use the Part.getHeader
        // method as they don't decode non-ASCII 'encoded words' (see RFC 2047).
        if (mailPart instanceof Message) {
            // I don't know how a mail part can be a Message but not have message/rfc822 mime type
            extractMessageSinglePartMetadata(mailPart, result, mimeType);
        }
        else {
            extractNonMessageSinglePartMetadata(mailPart, result, mimeType);
        }

        return result;
    }

    private String getMimeTypeFromContentType(ContentType contentType) {
        if (contentType != null) {
            return normalizeString(contentType.getBaseType());
        }
        else { // set the defaults according to RFC 2045
            return "text/plain";
        }
    }

    private String getCharsetStringFromContentType(ContentType contentType, String mimeType) {
        String charsetStr = null;

        if (contentType != null) {
            charsetStr = normalizeString(contentType.getParameter("charset"));
        }

        if (charsetStr == null && "text/plain".equals(mimeType)) {
            // set the defaults according to RFC 2045
            charsetStr = "us-ascii";
        }

        return normalizeCharset(charsetStr);
    }

    /**
     * Creates hashmap without content, as explicitly requested
     * 
     * @param uri the uri of the mail part that is to be exempted from the content extraction
     * @return a hashmap containing a single ID key with the uri value
     */
    private HashMap handleEmptyContentSinglePart(URI uri) {
        HashMap result = new HashMap();
        result.put(ID_KEY, uri);
        return result;
    }

    private HashMap handleRfc822SinglePart(Part mailPart, URI uri) throws MessagingException, IOException {
        Object content = mailPart.getContent();
        if (content instanceof Message) {
            /*
             * this part contains a nested message (typically a forwarded message): ignore this part and only
             * model the contents of the nested message, as the parent message will have been generated
             * already and this specific Part contains no additional useful information
             */
            Message nestedMessage = (Message) content;
            HashMap result = handleMailPart(nestedMessage, uri, MailUtil.getDate(nestedMessage));
            
            /*
             * We need to pull in the message id at this level, because this is one of the two places during
             * the message tree traversal, where we actually operate on an instance of the Message interface,
             * all other traversal-related methods operate on javax.mail.Part, and the messageid is a property
             * of a message and not a part.
             */
            if (nestedMessage instanceof MimeMessage) {
                String messageId = ((MimeMessage)nestedMessage).getMessageID();
                if (messageId != null) {
                    result.put(NMO.messageId, messageId);
                }
            }
            
            /*
             * Also, this is also the only place where we can mark the attached message as an Email by it's 
             * own right. Otherwise, it would simply be a MimeEntity, or an attachment.
             */
            result.put(RDF.type,NMO.Email);
            return result;
        }
        else {
            logger.warn("message/rfc822 part with unknown content class: "
                    + (content == null ? null : content.getClass()));
            return null;
        }
    }
    
    /**
     * Creates a data object embedding the data stream of the mail part. If the mail part has a file name it
     * is treated as an attachment.
     * 
     * @param normalSinglePart
     * @param charsetStr
     * @param uri
     * @param messageCreationDate - the date of the message creation, it will be added to the part metadata if
     *            this part does not contain an attached file
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private HashMap handleNormalSinglePart(Part normalSinglePart, String charsetStr, String mimeType, URI uri,
            Date messageCreationDate)
    throws MessagingException, IOException {
        HashMap result = new HashMap();
        result.put(ID_KEY, uri);
        
        Object content = normalSinglePart.getContent();
        
        /*
         * When this message is called the content will always be either a string, because this part is a
         * normal message (either the root message or a forwarded message), or an attachment containing the
         * binary stream. All kinds of multipart issues have been solved on a higher level. Note that this
         * method does not contain any further recursive calls.
         */
        if (content instanceof String) {
            addStringContent((String)content,mimeType,result);
        } else if (content instanceof InputStream) {
            result.put(CONTENTS_KEY, streamFactory.getPartStream(normalSinglePart));
        } else {
            // a serious error, if it happens - it is a bug and let the users report it
            throw new MessagingException("the content should be a string or a stream");
        }
        
        if (charsetStr != null) {
            result.put(NIE.characterSet, charsetStr);
        }
        
        /*
         * The file name is not null only if this part is an attachment.
         */
        String fileName = normalSinglePart.getFileName();
        if (fileName != null) {
            try {
                fileName = MimeUtility.decodeWord(fileName);
            }
            catch (MessagingException e) {
                // happens on unencoded file names! so just ignore it and leave the file name as it is
            }
            result.put(NFO.fileName, fileName);
            // everything that has a file name is an attachment
            result.put(RDF.type, NFO.Attachment);
        } else {
            /*
             * we only insert the message creation date for mail parts that aren't attachments, otherwise the
             * metadata extracted from the attached file may have different content creation date, thus the
             * extractor will add a second contentCreated triple which will violate the maxCardinality
             * constrained defined in NIE for the nie:contentCreated property
             */ 
            result.put(NIE.contentCreated, messageCreationDate);
        }
        return result;
    }
    
    /**
     * This method adds the content string to the results hash map. It checks if the content type of the part
     * indicates that the content string is in HTML. In such a case, it fires the routine that strips the
     * markup from the html string and returns the plain text.
     * 
     * @param content the string with the content
     * @param mimeType the base mime type of the part the content string comes from, most probably obtained by
     *            the getMimeTypeFromContentType method
     * @param result the hashmap where the result should be stored.
     */
    private void addStringContent(String content, String mimeType, HashMap result) {
        if (mimeType.equals("text/html")) {
            content = extractTextFromHtml(content);
        }
        result.put(NMO.plainTextMessageContent, content);
    }
    
    /**
     * This method strips the HTML markup from the given string and produces a plain text version. It appends
     * the html metadata at the beginning of the fulltext.
     * 
     * @param string
     * @return
     */
    private String extractTextFromHtml(String string) {
        // parse the HTML and extract full-text and metadata
        HtmlParserUtil.ContentExtractor extractor = new HtmlParserUtil.ContentExtractor();
        InputStream stream = new ByteArrayInputStream(string.getBytes()); // default encoding, problematic?
        try {
            HtmlParserUtil.parse(stream, null, extractor);
        }
        catch (ExtractorException e) {
            return "";
        }

        // append metadata and full-text to a string buffer
        StringBuilder buffer = new StringBuilder(32 * 1024);
        append(buffer, extractor.getTitle());
        append(buffer, extractor.getAuthor());
        append(buffer, extractor.getDescription());
        Iterator keywords = extractor.getKeywords();
        while (keywords.hasNext()) {
            append(buffer, (String) keywords.next());
        }
        append(buffer, extractor.getText());

        // return the buffer's content
        return buffer.toString();
    }

    private void append(StringBuilder buffer, String text) {
        if (text != null) {
            buffer.append(text);
            buffer.append(' ');
        }
    }

    /**
     * Extracts some generally applicable single part metadata properties, namely the size and the message
     * creation date
     * 
     * @param mailPart the part we're working on
     * @param result the hashmap where the results should go
     * @param messageCreationDate the creation date of the, the mailPart belongs to. This may be the main
     *            message, or some attached or forwarded message
     * @throws MessagingException
     */
    private void extractGenericSinglePartMetadata(Part mailPart, HashMap result, Date messageCreationDate)
            throws MessagingException {
        int size = mailPart.getSize();
        if (size >= 0) {
            result.put(NIE.byteSize, new Integer(size));
        }
    }

    private void extractMessageSinglePartMetadata(Part mailPart, HashMap result, String mimeType)
            throws MessagingException {
        // the data object's primary mimetype is message/rfc822. The MIME type of the InputStream
        // (most often text/plain or text/html) is modeled as a secondary MIME type
        result.put(NIE.mimeType, "message/rfc822");
        result.put(NMO.contentMimeType, mimeType);

        // add message metadata
        Message localMessage = (Message) mailPart;
        try {
            addObjectIfNotNull(NMO.messageSubject, localMessage.getSubject(), result);
            addContactArrayIfNotNull(NMO.from, localMessage.getFrom(), result);
            addContactArrayIfNotNull(NMO.to, localMessage.getRecipients(RecipientType.TO), result);
            addContactArrayIfNotNull(NMO.cc, localMessage.getRecipients(RecipientType.CC), result);
            addContactArrayIfNotNull(NMO.bcc, localMessage.getRecipients(RecipientType.BCC), result);
        } catch (Exception e) {
            // do nothing, this catch has been introduced as a temporary workaround, not to crash the crawling
            // process in case the crawled email contains a header like this:
            // From: <Saved by Mozilla 5.0 (Windows; en-US)>
            // TODO, turn this catch into something that works on a per-field basis
        }
        result.put(RDF.type, NMO.Email);

        if (localMessage instanceof MimeMessage) {
            MimeMessage mimeMessage = (MimeMessage) localMessage;
            addObjectIfNotNull(NMO.sender, mimeMessage.getSender(), result);
        }
    }

    private void extractNonMessageSinglePartMetadata(Part mailPart, HashMap result, String mimeType) {
        // this is most likely an attachment: set the InputStream's mime type as the data object's
        // primary MIME type
        result.put(NIE.mimeType, mimeType);
        // originally this line treated all parts of a multipart message as attachments, this is wrong
        // that's why i (Antoni Mylka) commented this line out on 27.02.2008, giving attachments an
        // rdf:type of nmo:MimeEntity is clearly correct for message parts and for attachments,
        // the entire idea of creating a hashmap from a message part is insufficient in this respect
        // this issue will need to be resolved when this class is rewritten to allow for a mail part
        // to have multiple types
        // result.put(RDF.type, NFO.Attachment);
        if (result.get(RDF.type) == null) {
            // the part may already have a type, e.g. the attachments are marked as attachments
            // if the part has a fileName, see above
            result.put(RDF.type, NMO.MimeEntity);
        }
    }


    private HashMap handleMultipart(Multipart multipart, ContentType contentType, URI uri,
            Date messageCreationDate) throws MessagingException, IOException {
        // fetch the content subtype
        String subType = normalizeString(contentType.getSubType());

        // handle the part according to its subtype
        if ("mixed".equals(subType)) {
            return handleMixedPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("alternative".equals(subType)) {
            return handleAlternativePart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("digest".equals(subType)) {
            return handleDigestPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("related".equals(subType)) {
            return handleRelatedPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("signed".equals(subType)) {
            return handleSignedPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("encrypted".equals(subType)) {
            return handleEncryptedPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("report".equals(subType)) {
            return handleReportPart(multipart, contentType, uri, messageCreationDate);
        }
        else if ("parallel".equals(subType)) {
            return handleParallelPart(multipart, contentType, uri, messageCreationDate);
        }
        else {
            return handleUnknownTypePart(multipart, contentType, uri, messageCreationDate);
        }
    }

    private HashMap handleMixedPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // interpret the parent part, reflecting subject and address metadata, and skip if there is none
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);
        if (parent == null) {
            return null;
        }

        // determine the uri prefix that all attachments parts should have
        String uriPrefix = getBodyPartURIPrefix(uri);

        // interpret every nested part
        int nrParts = part.getCount();
        ArrayList children = new ArrayList(nrParts);

        for (int i = 0; i < nrParts; i++) {
            BodyPart bodyPart = part.getBodyPart(i);
            if (bodyPart == null) {
                continue;
            }

            URI bodyURI = new URIImpl(uriPrefix + i);
            HashMap childResult = handleMailPart(bodyPart, bodyURI, date);

            if (childResult != null) {
                children.add(childResult);
            }
        }

        // the first child with type text/plain or text/html is promoted to become the body text
        // of the parent object
        int nrChildren = children.size();
        for (int i = 0; i < nrChildren; i++) {
            HashMap child = (HashMap) children.get(i);
            Object bodyMimeType = child.get(NIE.mimeType);

            if ("text/plain".equals(bodyMimeType) || "text/html".equals(bodyMimeType)) {
                children.remove(i);
                transferInfo(child, parent);
                break;
            }
        }

        // all remaining data objects are registered as the parent object's children
        parent.put(CHILDREN_KEY, children);

        // return the parent as the result of this operation
        return parent;
    }

    private HashMap handleAlternativePart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // nothing to return when there are no parts
        int count = part.getCount();
        if (count == 0) {
            return null;
        }

        // try to fetch the text/plain alternative
        int index = getPartWithMimeType(part, "text/plain");

        // if not available, try to fetch the text/html alternative
        if (index < 0) {
            index = getPartWithMimeType(part, "text/html");
        }

        // if still not found, simply take the first available part;
        if (index < 0) {
            index = 0;
        }

        // interpret the selected alternative part
        HashMap child = handleMailPart(part.getBodyPart(index), uri, date);

        // If this part was nested in a message, we should merge the obtained info with a data object
        // modeling all message info, in all other cases (e.g. when the multipart/alternative was
        // nested in a multipart/mixed), we can simply return the child data object.
        // We can use the same uri as for the child object, as only one of these objects will actually
        // be returned.
        Part parentPart = part.getParent();
        if (parentPart instanceof Message) {
            HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);

            if (parent == null) {
                return child;
            }
            else {
                transferInfo(child, parent);
                return parent;
            }
        }
        else {
            return child;
        }
    }

    private HashMap handleDigestPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // interpret the parent part
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);
        if (parent == null) {
            return null;
        }

        // create the URI prefix for all children
        String bodyURIPrefix = getBodyPartURIPrefix(uri);

        // interpret every body part in the digest multipart
        ArrayList children = new ArrayList();

        int nrParts = part.getCount();
        for (int i = 0; i < nrParts; i++) {
            // fetch the body part
            Part bodyPart = part.getBodyPart(i);
            if (bodyPart == null) {
                continue;
            }

            // derive a URI
            URI bodyURI = new URIImpl(bodyURIPrefix + i);

            // interpret this part
            HashMap child = handleMailPart(bodyPart, bodyURI, date);
            if (child != null) {
                children.add(child);
            }
        }

        // all interpreted body parts become children of the parent
        parent.put(CHILDREN_KEY, children);

        return parent;
    }

    private HashMap handleRelatedPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // interpret the parent part
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);
        if (parent == null) {
            return null;
        }

        // determine the prefix for all children
        String bodyURIPrefix = getBodyPartURIPrefix(uri);

        // find the index of the root part, if specified (defaults to 0)
        int rootPartIndex = 0;
        int nrBodyParts = part.getCount();

        String rootPartString = contentType.getParameter("start");
        if (rootPartString != null) {
            rootPartString = rootPartString.trim();
            if (rootPartString.length() > 0) {
                for (int i = 0; i < nrBodyParts; i++) {
                    BodyPart bodyPart = part.getBodyPart(i);
                    String bodyID = getHeader(bodyPart, "Content-ID");
                    if (rootPartString.equals(bodyID)) {
                        rootPartIndex = i;
                        break;
                    }
                }
            }
        }

        // interpret each body part, giving special treatment to the root part
        ArrayList children = new ArrayList();

        for (int i = 0; i < nrBodyParts; i++) {
            // fetch the body part
            BodyPart bodyPart = part.getBodyPart(i);

            // interpret this body part
            URI bodyURI = new URIImpl(bodyURIPrefix + i);
            HashMap child = handleMailPart(bodyPart, bodyURI, date);

            // append it to the part object in the appropriate way
            if (child != null) {
                if (i == rootPartIndex) {
                    transferInfo(child, parent);
                }
                else {
                    children.add(child);
                }
            }
        }

        // all interpreted body parts become children of the parent part, except for the root part, whose
        // properties have already been shifted to the parent
        parent.put(CHILDREN_KEY, children);

        return parent;
    }

    private HashMap handleSignedPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        return handleProtectedPart(part, 0, contentType, uri, date);
    }

    private HashMap handleEncryptedPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        return handleProtectedPart(part, 1, contentType, uri, date);
    }

    private HashMap handleProtectedPart(Multipart part, int partIndex, ContentType contentType, URI uri,
            Date date) throws MessagingException, IOException {
        // interpret the first body part, which contains the actual content
        HashMap child = null;
        if (part.getCount() >= 2) {
            child = handleMailPart(part.getBodyPart(partIndex), uri, date);
        }
        else {
            logger.warn("multipart/signed or multipart/encrypted without enough body parts, uri = " + uri);
        }

        // if this part was nested in a message, we should merge the obtained info with the message info,
        // else we simply return the child
        Part parentPart = part.getParent();
        if (parentPart instanceof Message) {
            HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);
            if (parent == null) {
                return child;
            }
            else {
                if (child != null) {
                    transferInfo(child, parent);
                }
                return parent;
            }
        }
        else {
            return child;
        }
    }

    private HashMap handleReportPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // interpret for the parent message
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        HashMap parent = handleSinglePart(parentPart, contentType, uri, date, true);
        if (parent == null) {
            return null;
        }

        // the first part contains a human-readable error message and will be treated as the mail body
        int count = part.getCount();
        if (count > 0) {
            HashMap errorPart = handleMailPart(part.getBodyPart(0), uri, date);
            if (errorPart != null) {
                transferInfo(errorPart, parent);
            }
        }

        // the optional third part contains the (partial) returned message and will become an attachment
        if (count > 2) {
            URI nestedURI = new URIImpl(getBodyPartURIPrefix(uri) + "0");
            HashMap returnedMessage = handleMailPart(part.getBodyPart(2), nestedURI, date);
            if (returnedMessage != null) {
                ArrayList children = new ArrayList();
                children.add(returnedMessage);
                parent.put(CHILDREN_KEY, children);
            }
        }

        return parent;
    }

    private HashMap handleParallelPart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // treat this as multipart/mixed
        return handleMixedPart(part, contentType, uri, date);
    }

    private HashMap handleUnknownTypePart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // treat this as multipart/mixed, as imposed by RFC 2046
        logger.warn("Unknown multipart MIME type: \"" + contentType.getBaseType()
                + "\", treating as multipart/mixed");
        return handleMixedPart(part, contentType, uri, date);
    }

    /* ------- Methods for transforming a HashMap to a list of DataObjects ------- */

    private void createDataObjects(HashMap dataObjectHashMap, URI parentUri,
            List<DataObject> resultDataObjectList) {
        // fetch the minimal set of properties needed to create a DataObject
        URI dataObjectId = (URI) dataObjectHashMap.get(ID_KEY);
        InputStream content = (InputStream) dataObjectHashMap.get(CONTENTS_KEY);
        RDFContainer metadata = containerFactory.getRDFContainer(dataObjectId);

        if (content != null && !content.markSupported()) {
            content = new BufferedInputStream(content, 16384);
        }

        // create the DataObject
        DataObject dataObject = (content == null) ? new DataObjectBase(dataObjectId, dataSource, metadata)
                : new FileDataObjectBase(dataObjectId, dataSource, metadata, content);

        resultDataObjectList.add(dataObject);

        // extend metadata with additional properties
        if (parentUri != null) {
            metadata.add(NIE.isPartOf, parentUri);
            // we need to add the type of the parent, otherwise the validator will complain
            if (dataObjectId.equals(messageURI)) {
                // this means that we're converting the root message object, the parent is a folder
                metadata.getModel().addStatement(parentUri, RDF.type, NFO.Folder);
            }
            else {
                // this means that we're deeper within the message tree
                metadata.getModel().addStatement(parentUri, RDF.type, NMO.MimeEntity);
            }
        }

        copyString(NIE.characterSet, dataObjectHashMap, metadata);
        copyString(NIE.mimeType, dataObjectHashMap, metadata);
        copyString(NMO.contentMimeType, dataObjectHashMap, metadata);
        copyString(NMO.messageSubject, dataObjectHashMap, metadata);
        copyString(NFO.fileName, dataObjectHashMap, metadata);
        copyString(NMO.plainTextMessageContent, dataObjectHashMap, metadata);
        copyString(NMO.messageId, dataObjectHashMap, metadata);

        copyInt(NIE.byteSize, dataObjectHashMap, metadata);

        copyDate(NIE.contentCreated, dataObjectHashMap, metadata);

        copyAddresses(NMO.from, dataObjectHashMap, metadata);
        copyAddresses(NMO.sender, dataObjectHashMap, metadata);
        copyAddresses(NMO.to, dataObjectHashMap, metadata);
        copyAddresses(NMO.cc, dataObjectHashMap, metadata);
        copyAddresses(NMO.bcc, dataObjectHashMap, metadata);

        copyUri(RDF.type, dataObjectHashMap, metadata);

        // a really crappy workaround, the hashmap allows the mail types to have only one type
        // this means, that attachments can be marked as attachments, but thay can't be marked
        // as MimeEntities, therefore we always add the RDF.type NMO.MimeEntity at this point
        metadata.add(RDF.type, NMO.MimeEntity);
        metadata.add(RDF.type, NMO.MailboxDataObject);

        // repeat recursively on children
        ArrayList children = (ArrayList) dataObjectHashMap.get(CHILDREN_KEY);
        if (children != null) {
            int nrChildren = children.size();
            for (int i = 0; i < nrChildren; i++) {
                HashMap childHashMap = (HashMap) children.get(i);

                // also register the child in the parent's metadata
                URI childID = (URI) childHashMap.get(ID_KEY);
                metadata.getModel().addStatement(childID, NIE.isPartOf, dataObjectId);
                // we need to determine the type of the child, otherwise the validator will complain
                metadata.getModel().addStatement(childID, RDF.type, NMO.MailboxDataObject);

                createDataObjects(childHashMap, dataObjectId, resultDataObjectList);
            }
        }
    }

    private void copyString(URI predicate, HashMap map, RDFContainer metadata) {
        String value = (String) map.get(predicate);
        if (value != null) {
            metadata.add(predicate, value);
        }
    }

    private void copyInt(URI predicate, HashMap map, RDFContainer metadata) {
        Integer value = (Integer) map.get(predicate);
        if (value != null) {
            metadata.add(predicate, value.intValue());
        }
    }

    private void copyDate(URI predicate, HashMap map, RDFContainer metadata) {
        Date value = (Date) map.get(predicate);
        if (value != null) {
            metadata.add(predicate, value);
        }
    }

    private void copyUri(URI predicate, HashMap map, RDFContainer metadata) {
        URI uri = (URI) map.get(predicate);
        if (uri != null) {
            metadata.add(predicate, uri);
        }
    }

    private void copyAddresses(URI predicate, HashMap map, RDFContainer metadata) {
        Object value = map.get(predicate);

        try {
            if (value instanceof InternetAddress) {
                MailUtil.addAddressMetadata((InternetAddress) value, predicate, metadata);
            }
            else if (value instanceof InternetAddress[]) {
                InternetAddress[] array = (InternetAddress[]) value;
                for (int i = 0; i < array.length; i++) {
                    MailUtil.addAddressMetadata(array[i], predicate, metadata);
                }
            }
            else if (value != null) {
                logger.warn("Unknown address class: " + value.getClass().getName());
            }
        }
        catch (ModelRuntimeException e) {
            logger.error("ModelException while handling address metadata", e);
        }
    }

    /* ----------------------------- Utility methods ----------------------------- */

    /**
     * Returns a version of the given string converted to lowercase with leading and trailing whitespace
     * removed.
     * 
     * @param string to normalize
     * @return a normalized version of the string
     */
    private String normalizeString(String string) {
        if (string != null) {
            string = string.trim().toLowerCase();
        }
        return string;
    }

    private String normalizeCharset(String charsetStr) {
        if (charsetStr == null || charsetStr.length() == 0) {
            charsetStr = MimeUtility.getDefaultJavaCharset();
        }
        else {
            charsetStr = MimeUtility.javaCharset(charsetStr);
        }

        // note: even MimeUtility.javaCharset may return different casings of the same charset
        charsetStr = charsetStr.toLowerCase();

        return charsetStr;
    }

    private void addContactArrayIfNotNull(URI predicate, Address[] addresses, HashMap result) {
        if (addresses != null) {
            result.put(predicate, addresses);
        }
    }

    private String getBodyPartURIPrefix(URI parentURI) {
        String prefix = parentURI.toString();
        return prefix + (prefix.indexOf('#') < 0 ? "#" : "-");
    }

    /**
     * Transfer all properties from one interpreted mail part to another, taking care to merge information
     * rather than overwrite it when appropriate.
     */
    private void transferInfo(HashMap fromObject, HashMap toObject) throws IOException {
        // transfer content stream if applicable
        Object content = fromObject.get(CONTENTS_KEY);
        if (content != null) {
            toObject.put(CONTENTS_KEY, content);
        }

        // transfer mime type, carefully placing it as mime type or content mime type
        Object fromType = fromObject.get(NIE.mimeType);
        if (fromType != null) {
            Object toType = toObject.get(NIE.mimeType);
            URI predicate = "message/rfc822".equals(toType) ? NMO.contentMimeType : NIE.mimeType;
            toObject.put(predicate, fromType);
        }

        // transfer the first object's children to the second object relationships
        ArrayList fromChildren = (ArrayList) fromObject.get(CHILDREN_KEY);

        if (fromChildren != null && !fromChildren.isEmpty()) {
            ArrayList toChildren = (ArrayList) toObject.get(CHILDREN_KEY);
            if (toChildren == null) {
                toChildren = new ArrayList();
                toObject.put(CHILDREN_KEY, toChildren);
            }

            toChildren.addAll(fromChildren);
        }
        
        // transfer all other fromObject properties, with the exception of those that already received
        // special treatment - CONTENTS_KEY, NIE.mimeType and CHILDREN_KEY
        for (Object entryObject : fromObject.entrySet()) {
            Map.Entry entry = (Map.Entry)entryObject;
            // we overwrite all other fields, we make the assumption that the values from the fromObject
            // are better than those from the toObject
            String keyString = entry.getKey().toString();
            if (!keyString.equals(ID_KEY) && // obviously the identifier should not be overwritten 
                !keyString.equals(CONTENTS_KEY) && // the CONTENTS has been handled already
                !keyString.equals(NIE.mimeType.toString()) &&  // the mime type has been handled already
                !keyString.equals(CHILDREN_KEY)  && // the children have been handled already
                !keyString.equals(NIE.byteSize.toString())) {  // the 'to' object has better knowledge of the size of the
                                                    // overall message (e.g. in multipart/alternative)
                toObject.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private int getPartWithMimeType(Multipart multipart, String mimeType) throws MessagingException {
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String partType = getMimeType(bodyPart);
            if (mimeType.equalsIgnoreCase(partType)) {
                return i;
            }
        }

        return -1;
    }

    private String getMimeType(Part mailPart) throws MessagingException {
        String contentType = mailPart.getContentType();

        if (contentType != null) {
            ContentType ct = new ContentType(contentType);
            return ct.getBaseType();
        }

        return null;
    }

    private String getHeader(Part mailPart, String headerName) throws MessagingException {
        String[] headerValues = mailPart.getHeader(headerName);
        return (headerValues != null && headerValues.length > 0) ? headerValues[0] : null;
    }

    private void addObjectIfNotNull(URI predicate, Object value, HashMap map) {
        if (value != null) {
            map.put(predicate, value);
        }
    }
}