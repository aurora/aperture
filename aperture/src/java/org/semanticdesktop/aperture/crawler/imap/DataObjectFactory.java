/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.AccesVocabulary;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Creates a set of DataObjects from a MimeMessage.
 * 
 * <p>
 * DataObjectFactory interprets the structure of a MimeMessage and creates a tree of DataObjects that
 * model its contents in a way that is most natural to users. Practically this means that the DataObject
 * tree should be as similar as possible to how mail readers present the mail.
 * 
 * <p>
 * For example, a multipart/alternative message may have a rather complex object structure (a Part with a
 * MultiPart content, on its turn containing two BodyParts), but this is translated to a single
 * DataObject holding all the mail metadata (sender, receiver, etc) as well as an InputStream accessing
 * the simplest of the two body parts (typically the text/plain part).
 */
public class DataObjectFactory {

    // TODO: we could use the URL format specified in RFC 2192 to construct a proper IMAP4 URL.
    // Right now we use something home-grown for representing attachments rather than isections.
    // To investigate: does JavaMail provide us with enough information for constructing proper
    // URLs for attachments? Perhaps we can create them ourselves by carefully counting BodyParts?

    private static final Logger LOGGER = Logger.getLogger(DataObjectFactory.class.getName());

    /**
     * Key used to store a DataObject's URI in the intermediate HashMap representation.
     */
    private static final String ID_KEY = "id";

    /**
     * Key used to store a DataObject's children in the intermediate HashMap representation.
     */
    private static final String CHILDREN_KEY = "children";

    /**
     * Key used to store a DataObject's content (an InputStream) in the intermediate HashMap
     * representation.
     */
    private static final String CONTENTS_KEY = "contents";

    /**
     * The DataSource that the generated DataObjects will report as source.
     */
    private DataSource source;

    /**
     * The RDFContainerFactory delivering the RDFContainer to be used in the DataObjects.
     */
    private RDFContainerFactory containerFactory;

    /**
     * Internal counter used to generate BNode identifiers.
     */
    private int bnodeIndex;

    /**
     * Returns a list of DataObjects that have been created based on the contents of the specified
     * MimeMessage. The order of the DataObjects reflects the order of the message parts they represent,
     * i.e. the first DataObject represents the entire message, subsequent DataObjects represent
     * attachments in a depth first orderseveral layers of attachments can exist when forwarding messages
     * containing attachments).
     * 
     * @param message The MimeMessage to interpret.
     * @param messageUri The URI to use to identify the specified MimeMessage. URIs of child DataObjects
     *            must be derived from this URI.
     * @param folderUri The URI of the Folder from which these MimeMessages were obtained. The root
     *            DataObject will have this URI as parent.
     * @param source The DataSource that the DataObjects will return as source.
     * @param containerFactory An RDFContainerFactory that can deliver RDFContainer to be used in the
     *            returned DataObjects.
     * @return A List of DataObjects derived from the specified MimeMessage. The order of the DataObjects
     *         reflects the order of the message parts they represent.
     * @throws MessagingException Thrown when accessing the mail contents.
     * @throws IOException Thrown when accessing the mail contents.
     */
    public List createDataObjects(MimeMessage message, String messageUri, URI folderUri, DataSource source,
            RDFContainerFactory containerFactory) throws MessagingException, IOException {
        // initialize variables
        this.source = source;
        this.containerFactory = containerFactory;
        this.bnodeIndex = 0;

        // create a HashMap representation of this message and all its nested parts
        HashMap map = handleMailPart(message, new URIImpl(messageUri), getDate(message));

        // convert the HashMap representation to a DataObject representation
        ArrayList result = new ArrayList();
        createDataObjects(map, folderUri, result);
        return result;
    }

    /* ----------------------------- Methods for MIME interpretation ----------------------------- */

    private HashMap handleMailPart(Part mailPart, URI uri, Date date) throws MessagingException, IOException {
        // determine the primary type of this Part
        ContentType contentType = null;
        String primaryType = null;

        String contentTypeStr = mailPart.getContentType();
        if (contentTypeStr != null) {
            contentType = new ContentType(contentTypeStr);
            primaryType = normalizeString(contentType.getPrimaryType());
        }

        // make an exception for multipart mails
        if ("multipart".equals(primaryType)) {
            Object content = mailPart.getContent();
            if (content instanceof Multipart) {
                // content is a container for multiple other parts
                return handleMultipart((Multipart) content, contentType, uri, date);
            }
            else {
                LOGGER.log(Level.WARNING, "multipart '" + uri + "' does not contain a Multipart object: "
                        + (content == null ? null : content.getClass()));
                return null;
            }
        }
        else {
            return handleSinglePart(mailPart, contentType, uri, date, false);
        }
    }

    private HashMap handleSinglePart(Part mailPart, ContentType contentType, URI uri, Date date,
            boolean emptyContent) throws MessagingException, IOException {
        // determine the content type properties of this mail
        String mimeType = null;
        String charsetStr = null;

        if (contentType != null) {
            mimeType = normalizeString(contentType.getBaseType());
            charsetStr = normalizeString(contentType.getParameter("charset"));
        }

        // if some are unspecified, set the defaults according to RFC 2045
        if (mimeType == null) {
            mimeType = "text/plain";
        }
        if (charsetStr == null && "text/plain".equals(mimeType)) {
            charsetStr = "us-ascii";
        }

        charsetStr = normalizeCharset(charsetStr);

        // extract the mail's contents
        HashMap result = null;

        if (emptyContent) {
            // create a data object without content, as explicitly requested
            result = new HashMap();
            result.put(ID_KEY, uri);
        }
        else if ("message/rfc822".equals(mimeType)) {
            Object content = mailPart.getContent();
            if (content instanceof Message) {
                // this part contains a nested message (typically a forwarded message): ignore this part
                // and only model the contents of the nested message, as the parent message will have
                // been generated already and this specific Part contains no additional useful
                // information
                Message nestedMessage = (Message) content;
                return handleMailPart(nestedMessage, uri, getDate(nestedMessage));
            }
            else {
                LOGGER.warning("message/rfc822 part with unknown content class: "
                        + (content == null ? null : content.getClass()));
                return null;
            }
        }
        else {
            // create a data object embedding the data stream of the mail part
            result = new HashMap();
            result.put(ID_KEY, uri);
            result.put(CONTENTS_KEY, mailPart.getInputStream());

            if (charsetStr != null) {
                result.put(AccesVocabulary.CHARACTER_SET, charsetStr);
            }

            String fileName = mailPart.getFileName();
            if (fileName != null) {
                result.put(AccesVocabulary.NAME, fileName);
            }
        }

        // set some generally applicable metadata properties
        int size = mailPart.getSize();
        if (size >= 0) {
            result.put(AccesVocabulary.BYTE_SIZE, new Integer(size));
        }

        result.put(AccesVocabulary.DATE, date);

        // Differentiate between Messages and other types of mail parts. We don't use the Part.getHeader
        // method as they don't decode non-ASCII 'encoded words' (see RFC 2047).
        if (mailPart instanceof Message) {
            // the data object's primary mimetype is message/rfc822. The MIME type of the InputStream
            // (most often text/plain or text/html) is modeled as a secondary MIME type
            result.put(AccesVocabulary.MIME_TYPE, "message/rfc822");
            result.put(AccesVocabulary.CONTENT_MIME_TYPE, mimeType);

            // add message metadata
            Message message = (Message) mailPart;
            addIfNotNull(AccesVocabulary.SUBJECT, message.getSubject(), result);
            addIfNotNull(AccesVocabulary.FROM, message.getFrom(), result);
            addIfNotNull(AccesVocabulary.TO, message.getRecipients(RecipientType.TO), result);
            addIfNotNull(AccesVocabulary.CC, message.getRecipients(RecipientType.CC), result);
            addIfNotNull(AccesVocabulary.BCC, message.getRecipients(RecipientType.BCC), result);
        }
        else {
            // this is most likely an attachment: set the InputStream's mime type as the data object's
            // primary MIME type
            result.put(AccesVocabulary.MIME_TYPE, mimeType);
        }

        // done!
        return result;
    }

    private HashMap handleMultipart(Multipart part, ContentType contentType, URI uri, Date date)
            throws MessagingException, IOException {
        // fetch the content subtype
        String subType = normalizeString(contentType.getSubType());

        // handle the part according to its subtype
        if ("mixed".equals(subType)) {
            return handleMixedPart(part, contentType, uri, date);
        }
        else if ("alternative".equals(subType)) {
            return handleAlternativePart(part, contentType, uri, date);
        }
        else if ("digest".equals(subType)) {
            return handleDigestPart(part, contentType, uri, date);
        }
        else if ("related".equals(subType)) {
            return handleRelatedPart(part, contentType, uri, date);
        }
        else if ("signed".equals(subType)) {
            return handleSignedPart(part, contentType, uri, date);
        }
        else if ("encrypted".equals(subType)) {
            return handleEncryptedPart(part, contentType, uri, date);
        }
        else if ("report".equals(subType)) {
            return handleReportPart(part, contentType, uri, date);
        }
        else if ("parallel".equals(subType)) {
            return handleParallelPart(part, contentType, uri, date);
        }
        else {
            return handleUnknownTypePart(part, contentType, uri, date);
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
            Object bodyMimeType = child.get(AccesVocabulary.MIME_TYPE);

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
            LOGGER.info("multipart/signed or multipart/encrypted without enough body parts, uri = " + uri);
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
        LOGGER.log(Level.INFO, "Unknown multipart MIME type: \"" + contentType.getBaseType()
                + "\", treating as multipart/mixed");
        return handleMixedPart(part, contentType, uri, date);
    }

    /* ------- Methods for transforming a HashMap to a list of DataObjects ------- */

    private void createDataObjects(HashMap map, URI parentUri, ArrayList result) {
        // fetch the minimal set of properties needed to create a DataObject
        URI id = (URI) map.get(ID_KEY);
        InputStream content = (InputStream) map.get(CONTENTS_KEY);
        RDFContainer metadata = containerFactory.getRDFContainer(id);

        if (!content.markSupported()) {
            content = new BufferedInputStream(content, 16384);
        }
        
        // create the DataObject
        DataObject object = content == null ? new DataObjectBase(id, source, metadata)
                : new FileDataObjectBase(id, source, metadata, content);
        result.add(object);

        // extend metadata with additional properties
        if (parentUri != null) {
            metadata.put(AccesVocabulary.PART_OF, parentUri);
        }

        copyString(AccesVocabulary.CHARACTER_SET, map, metadata);
        copyString(AccesVocabulary.MIME_TYPE, map, metadata);
        copyString(AccesVocabulary.CONTENT_MIME_TYPE, map, metadata);
        copyString(AccesVocabulary.SUBJECT, map, metadata);
        copyString(AccesVocabulary.NAME, map, metadata);

        copyInt(AccesVocabulary.BYTE_SIZE, map, metadata);

        copyDate(AccesVocabulary.DATE, map, metadata);

        copyAddresses(AccesVocabulary.FROM, map, metadata);
        copyAddresses(AccesVocabulary.TO, map, metadata);
        copyAddresses(AccesVocabulary.CC, map, metadata);
        copyAddresses(AccesVocabulary.BCC, map, metadata);

        // repeat recursively on children
        ArrayList children = (ArrayList) map.get(CHILDREN_KEY);
        if (children != null) {
            int nrChildren = children.size();
            for (int i = 0; i < nrChildren; i++) {
                HashMap child = (HashMap) children.get(i);

                // also register the child in the parent's metadata
                URI childID = (URI) child.get(ID_KEY);
                metadata.add(new StatementImpl(childID, AccesVocabulary.PART_OF, id));

                createDataObjects(child, id, result);
            }
        }
    }

    private void copyString(URI predicate, HashMap map, RDFContainer metadata) {
        String value = (String) map.get(predicate);
        if (value != null) {
            metadata.put(predicate, value);
        }
    }

    private void copyInt(URI predicate, HashMap map, RDFContainer metadata) {
        Integer value = (Integer) map.get(predicate);
        if (value != null) {
            metadata.put(predicate, value.intValue());
        }
    }

    private void copyDate(URI predicate, HashMap map, RDFContainer metadata) {
        Date value = (Date) map.get(predicate);
        if (value != null) {
            metadata.put(predicate, value);
        }
    }

    private void copyAddresses(URI predicate, HashMap map, RDFContainer metadata) {
        Object value = map.get(predicate);

        if (value instanceof InternetAddress) {
            addAddressMetadata((InternetAddress) value, predicate, metadata);
        }
        else if (value instanceof InternetAddress[]) {
            InternetAddress[] array = (InternetAddress[]) value;
            for (int i = 0; i < array.length; i++) {
                addAddressMetadata(array[i], predicate, metadata);
            }
        }
        else if (value != null) {
            LOGGER.warning("Unknown address class: " + value.getClass().getName());
        }
    }

    private void addAddressMetadata(InternetAddress address, URI predicate, RDFContainer metadata) {
        // create a BNode with the name and address and associate it with the container's described URI
        String name = address.getPersonal();
        String emailAddress = address.getAddress();

        if (name != null) {
            name = name.trim();
        }

        if (emailAddress != null) {
            emailAddress = emailAddress.trim();
        }

        if (hasRealValue(name) || hasRealValue(emailAddress)) {
            String identifier = metadata.getDescribedUri().toString() + "-bnode-" + bnodeIndex++;
            BNodeImpl bnode = new BNodeImpl(identifier);
            metadata.put(predicate, bnode);

            if (hasRealValue(name)) {
                Literal literal = new LiteralImpl(name);
                metadata.add(new StatementImpl(bnode, AccesVocabulary.NAME, literal));
            }

            if (hasRealValue(emailAddress)) {
                Literal literal = new LiteralImpl(emailAddress);
                metadata.add(new StatementImpl(bnode, AccesVocabulary.EMAIL_ADDRESS, literal));
            }
        }
    }

    /* ----------------------------- Utility methods ----------------------------- */

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

    private Date getDate(Message message) throws MessagingException {
        Date result = message.getSentDate();
        if (result == null) {
            result = message.getReceivedDate();
            if (result == null) {
                result = new Date();
            }
        }

        return result;
    }

    private void addIfNotNull(URI predicate, Object value, HashMap map) {
        if (value != null) {
            map.put(predicate, value);
        }
    }

    private String getBodyPartURIPrefix(URI parentURI) {
        String prefix = parentURI.toString();
        return prefix + (prefix.indexOf('#') < 0 ? "#" : "-");
    }

    /**
     * Transfer all properties from one interpreted mail part to another, taking care to merge
     * information rather than overwrite it when appropriate.
     */
    private void transferInfo(HashMap fromObject, HashMap toObject) throws IOException {
        // transfer content stream if applicable
        Object content = fromObject.get(CONTENTS_KEY);
        if (content != null) {
            toObject.put(CONTENTS_KEY, content);
        }

        // transfer mime type, carefully placing it as mime type or content mime type
        Object fromType = fromObject.get(AccesVocabulary.MIME_TYPE);
        if (fromType != null) {
            Object toType = toObject.get(AccesVocabulary.MIME_TYPE);
            URI predicate = "message/rfc822".equals(toType) ? AccesVocabulary.CONTENT_MIME_TYPE
                    : AccesVocabulary.MIME_TYPE;
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

    private boolean hasRealValue(String string) {
        return string != null && !string.equals("");
    }
}