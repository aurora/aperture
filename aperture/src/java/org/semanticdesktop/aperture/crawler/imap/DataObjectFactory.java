/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

/**
 * Creates a set of DataObjects from a MimeMessage.
 */
public class DataObjectFactory {

    private static final Logger LOGGER = Logger.getLogger(DataObjectFactory.class.getName());

    private static final String ATTACHMENT_SEPARATOR = "-";

    private DataSource source;

    private RDFContainerFactory containerFactory;

    private int bnodeIndex;

    // a mapping from child DataObjects to their virtual parent DataObjects
    private Map messageHierarchy;

    public DataObjectFactory(DataSource source, RDFContainerFactory containerFactory) {
        this.source = source;
        this.containerFactory = containerFactory;
        this.bnodeIndex = 0;
        this.messageHierarchy = new IdentityHashMap();
    }

    /**
     * Returns a list of DataObjects that have been created based on the contents of the specified
     * MimeMessage. The order of the DataObjects reflects the order of the message parts they represent.
     * 
     * @param message The MimeMessage to interpret.
     * @param messageUri The URI to use to identify the specified MimeMessage. URIs of child DataObjects
     *            must be derived from this URI.
     * @return A List of DataObjects derived from the specified MimeMessage. The order of the DataObjects
     *         reflects the order of the message parts they represent.
     * @throws MessagingException Thrown when accessing the mail contents.
     * @throws IOException Thrown when accessing the mail contents.
     */
    public List createDataObjects(MimeMessage message, String messageUri) throws MessagingException,
            IOException {
        // TODO: we could use the URL format specified in RFC 2192 to construct a proper IMAP4 URL.
        // To investigate: does JavaMail provide us with enough information for constructing proper
        // URLs for attachments?
        URI uri = new URIImpl(messageUri);

        ArrayList resultList = new ArrayList();
        handleMailPart(message, uri, getDate(message), resultList);
        encodeHierarchy();

        return resultList;
    }

    /* ----------------------------- Methods for MIME interpretation ----------------------------- */

    private DataObject handleMailPart(Part mailPart, URI uri, Date date, List resultList)
            throws MessagingException, IOException {
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
                // Content is a container for multiple other parts
                return handleMultipart((Multipart) content, contentType, uri, date, resultList);
            }
            else {
                LOGGER.log(Level.WARNING, "multipart '" + uri + "' does not contain a Multipart object: "
                        + (content == null ? null : content.getClass()));
                return null;
            }
        }
        else {
            return handleSinglePart(mailPart, contentType, uri, date, false, resultList);
        }
    }

    private DataObject handleSinglePart(Part mailPart, ContentType contentType, URI uri, Date date,
            boolean emptyContent, List resultList) throws MessagingException, IOException {
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

        // content is not a Multipart object (or not a correct one), report its content in a new
        // DataObject
        charsetStr = normalizeCharset(charsetStr);

        // create the Imap data object
        DataObject result = null;
        RDFContainer metadata = containerFactory.getRDFContainer(uri);

        if (emptyContent) {
            // create a data object without content, as explicitly requested
            result = new DataObjectBase(uri, source, metadata);
        }
        else if ("message/rfc822".equals(mimeType)) {
            Object content = mailPart.getContent();
            if (content instanceof Message) {
                Message nestedMessage = (Message) content;
                return handleMailPart(nestedMessage, uri, getDate(nestedMessage), resultList);
            }
            else {
                LOGGER.warning("message/rfc822 part with unknown content class: "
                        + (content == null ? null : content.getClass()));
                return null;
            }
        }
        else {
            // create a data object embedding the data stream of the mail part
            result = new FileDataObjectBase(uri, source, metadata, mailPart.getInputStream());

            if (charsetStr != null) {
                metadata.put(Vocabulary.CHARACTER_SET, charsetStr);
            }

            String fileName = mailPart.getFileName();
            if (fileName != null) {
                metadata.put(Vocabulary.NAME, fileName);
            }
        }

        resultList.add(result);

        // set some generally applicable metadata properties
        int size = mailPart.getSize();
        if (size >= 0) {
            metadata.put(Vocabulary.BYTE_SIZE, size);
        }

        metadata.put(Vocabulary.DATE, date);

        // Differentiate between Messages and other mail Parts. We don't use the Part.getHeader message
        // as they don't decode non-ASCII 'encoded words' (see RFC 2047).
        if (mailPart instanceof Message) {
            metadata.put(Vocabulary.MIME_TYPE, "message/rfc822");
            metadata.put(Vocabulary.CONTENT_MIME_TYPE, mimeType);

            Message message = (Message) mailPart;
            metadata.put(Vocabulary.SUBJECT, message.getSubject());

            addAddressMetadata(message.getFrom(), Vocabulary.FROM, metadata);
            addAddressMetadata(message.getRecipients(RecipientType.TO), Vocabulary.TO, metadata);
            addAddressMetadata(message.getRecipients(RecipientType.CC), Vocabulary.CC, metadata);
            addAddressMetadata(message.getRecipients(RecipientType.BCC), Vocabulary.BCC, metadata);
        }
        else {
            metadata.put(Vocabulary.MIME_TYPE, mimeType);
        }

        // done!
        return result;
    }

    private DataObject handleMultipart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // fetch the content subtype
        String subType = normalizeString(contentType.getSubType());

        if ("mixed".equals(subType)) {
            return handleMixedPart(part, contentType, uri, date, resultList);
        }
        else if ("alternative".equals(subType)) {
            return handleAlternativePart(part, contentType, uri, date, resultList);
        }
        else if ("digest".equals(subType)) {
            return handleDigestPart(part, contentType, uri, date, resultList);
        }
        else if ("related".equals(subType)) {
            return handleRelatedPart(part, contentType, uri, date, resultList);
        }
        else if ("signed".equals(subType)) {
            return handleSignedPart(part, contentType, uri, date, resultList);
        }
        else if ("encrypted".equals(subType)) {
            return handleEncryptedPart(part, contentType, uri, date, resultList);
        }
        else if ("report".equals(subType)) {
            return handleReportPart(part, contentType, uri, date, resultList);
        }
        else if ("parallel".equals(subType)) {
            return handleParallelPart(part, contentType, uri, date, resultList);
        }
        else {
            return handleUnknownTypePart(part, contentType, uri, date, resultList);
        }
    }

    private DataObject handleMixedPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // create a data object for the parent part, reflecting subject and address metadata, and skip if
        // there is no parent
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        DataObject parentObject = handleSinglePart(parentPart, contentType, uri, date, true, resultList);
        if (parentObject == null) {
            return null;
        }

        // determine the uri prefix that all attachments parts should have
        String uriPrefix = getBodyPartURIPrefix(uri);

        // create an ImapDataObject for every nested part
        int nrParts = part.getCount();
        ArrayList dataObjects = new ArrayList(nrParts);

        for (int i = 0; i < nrParts; i++) {
            BodyPart bodyPart = part.getBodyPart(i);
            if (bodyPart == null) {
                continue;
            }

            URI bodyURI = new URIImpl(uriPrefix + i);
            DataObject dataObject = handleMailPart(bodyPart, bodyURI, date, resultList);

            if (dataObject != null) {
                dataObjects.add(dataObject);
            }
        }

        // the first ImapDataObject of type text/plain or text/html is promoted to become the body text
        // of the parent object
        int nrObjects = dataObjects.size();
        for (int i = 0; i < nrObjects; i++) {
            DataObject bodyObject = (DataObject) dataObjects.get(i);
            Object bodyMimeType = getLiteral(bodyObject, Vocabulary.MIME_TYPE);

            if ("text/plain".equals(bodyMimeType) || "text/html".equals(bodyMimeType)) {
                dataObjects.remove(i);
                transferInfo(bodyObject, parentObject);
                break;
            }
        }

        // all remaining data objects are registered as the parent object's children
        messageHierarchy.put(parentObject, dataObjects);

        // return the parent data object as the result of this operation
        return parentObject;
    }

    private DataObject handleAlternativePart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // nothing to return when there are no parts
        int count = part.getCount();
        if (count == 0) {
            // fixme: shouldn't we create a data object for the parent, similar to handleMixedPart?
            // after all, the parent part may still contain useful metadata
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

        // create a data object for the selected alternative part
        DataObject childObject = handleMailPart(part.getBodyPart(index), uri, date, resultList);

        // If this part was nested in a message, we should merge the obtained info with a data object
        // modeling all message info, in all other cases (e.g. when the multipart/alternative was
        // nested in a multipart/mixed), we can simply return the child data object.
        // We can use the same uri as for the child object, as only one of these objects will actually
        // be returned.
        Part parent = part.getParent();
        if (parent instanceof Message) {
            DataObject parentObject = handleSinglePart(parent, contentType, uri, date, true, resultList);

            if (parentObject == null) {
                return childObject;
            }
            else {
                transferInfo(childObject, parentObject);
                return parentObject;
            }
        }
        else {
            return childObject;
        }
    }

    private DataObject handleDigestPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // create a data object for the parent part
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        DataObject parentObject = handleSinglePart(parentPart, contentType, uri, date, true, resultList);
        if (parentObject == null) {
            return null;
        }

        // create the URI prefix for all children
        String bodyURIPrefix = getBodyPartURIPrefix(uri);

        // add child objects for every body part in the digest multipart
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

            // create and append a child data object
            DataObject bodyObject = handleMailPart(bodyPart, bodyURI, date, resultList);
            if (bodyObject != null) {
                children.add(bodyObject);
            }
        }

        messageHierarchy.put(parentObject, children);

        // done!
        return parentObject;
    }

    private DataObject handleRelatedPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // create a data object for the parent part
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        DataObject parentObject = handleSinglePart(parentPart, contentType, uri, date, true, resultList);
        if (parentObject == null) {
            return null;
        }

        // determine the prefix for all children
        String bodyURIPrefix = getBodyPartURIPrefix(uri);

        // find the index of the root part, if specified (default to 0)
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

        // create child objects for each body part, giving special treatment to the root part
        ArrayList children = new ArrayList();

        for (int i = 0; i < nrBodyParts; i++) {
            // fetch the body part
            BodyPart bodyPart = part.getBodyPart(i);

            // create a data object for this body part
            URI bodyURI = new URIImpl(bodyURIPrefix + i);
            DataObject bodyObject = handleMailPart(bodyPart, bodyURI, date, resultList);

            // append it to the part object in the appropriate way
            if (bodyObject != null) {
                if (i == rootPartIndex) {
                    transferInfo(bodyObject, parentObject);
                }
                else {
                    children.add(bodyObject);
                }
            }
        }

        messageHierarchy.put(parentObject, children);

        return parentObject;
    }

    private DataObject handleSignedPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        return handleProtectedPart(part, 0, contentType, uri, date, resultList);
    }

    private DataObject handleEncryptedPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        return handleProtectedPart(part, 1, contentType, uri, date, resultList);
    }

    private DataObject handleProtectedPart(Multipart part, int partIndex, ContentType contentType, URI uri,
            Date date, List resultList) throws MessagingException, IOException {
        // create a data object for the first body part, which contains the actual content
        DataObject childObject = null;
        if (part.getCount() >= 2) {
            childObject = handleMailPart(part.getBodyPart(partIndex), uri, date, resultList);
        }
        else {
            LOGGER.info("multipart/signed or multipart/encrypted without enough body parts, uri = " + uri);
        }

        // if this part was nested in a message, we should merge the obtained info with a data object
        // modeling all message info, else we simply return the child object
        Part parent = part.getParent();
        if (parent instanceof Message) {
            DataObject parentObject = handleSinglePart(parent, contentType, uri, date, true, resultList);
            if (parentObject == null) {
                return childObject;
            }
            else {
                if (childObject != null) {
                    transferInfo(childObject, parentObject);
                }
                return parentObject;
            }
        }
        else {
            return childObject;
        }
    }

    private DataObject handleReportPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // create a data object for the parent message
        Part parentPart = part.getParent();
        if (parentPart == null) {
            return null;
        }

        DataObject parentObject = handleSinglePart(parentPart, contentType, uri, date, true, resultList);
        if (parentObject == null) {
            return null;
        }

        // the first part contains a human-readable error message and is treated as the body.
        int count = part.getCount();
        if (count > 0) {
            DataObject errorObject = handleMailPart(part.getBodyPart(0), uri, date, resultList);
            if (errorObject != null) {
                transferInfo(errorObject, parentObject);
            }
        }

        // the optional third part contains the (partial) returned message and will become an attachment
        if (count > 2) {
            URI nestedURI = new URIImpl(getBodyPartURIPrefix(uri) + "0");
            DataObject returnedMessageObject = handleMailPart(part.getBodyPart(2), nestedURI, date,
                    resultList);
            if (returnedMessageObject != null) {
                ArrayList children = new ArrayList();
                children.add(returnedMessageObject);
                messageHierarchy.put(parentObject, children);
            }
        }

        return parentObject;
    }

    private DataObject handleParallelPart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // treat this as multipart/mixed
        return handleMixedPart(part, contentType, uri, date, resultList);
    }

    private DataObject handleUnknownTypePart(Multipart part, ContentType contentType, URI uri, Date date,
            List resultList) throws MessagingException, IOException {
        // treat this as multipart/mixed, as imposed by RFC 2046
        LOGGER.log(Level.INFO, "Unknown multipart MIME type: \"" + contentType.getBaseType()
                + "\", treating as multipart/mixed");
        return handleMixedPart(part, contentType, uri, date, resultList);
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

    private void addAddressMetadata(Object value, URI predicate, RDFContainer metadata) {
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

        if (hasProperValue(name) || hasProperValue(emailAddress)) {
            String identifier = metadata.getDescribedUri().toString() + "-bnode-" + bnodeIndex++;
            BNodeImpl bnode = new BNodeImpl(identifier);
            metadata.put(predicate, bnode);

            if (name != null) {
                Literal literal = new LiteralImpl(name);
                metadata.add(new StatementImpl(bnode, Vocabulary.NAME, literal));
            }

            if (emailAddress != null) {
                Literal literal = new LiteralImpl(emailAddress);
                metadata.add(new StatementImpl(bnode, Vocabulary.EMAIL_ADDRESS, literal));
            }
        }
    }

    private boolean hasProperValue(String string) {
        return string != null && !string.equals("");
    }

    private String getBodyPartURIPrefix(URI parentURI) {
        String prefix = parentURI.toString();
        return prefix + (prefix.indexOf('#') < 0 ? "#" : ATTACHMENT_SEPARATOR);
    }

    /**
     * Temporary workaround for accessing a property of a DataObject when the described URI of its
     * RDFContainer can no longer be relied upon.
     */
    private String getLiteral(DataObject object, URI predicate) {
        RDFContainer metadata = object.getMetadata();
        Repository repository = ((SesameRDFContainer) metadata).getRepository();
        Collection statements = repository.getStatements(object.getID(), predicate, null);
        if (statements.isEmpty()) {
            return null;
        }
        else {
            Statement firstStatement = (Statement) statements.iterator().next();
            Value value = firstStatement.getObject();
            if (value instanceof Literal) {
                return ((Literal) value).getLabel();
            }
            else {
                return null;
            }
        }
    }

    private void transferInfo(DataObject fromObject, DataObject toObject) throws IOException {
        // transfer content stream if applicable
        if (fromObject instanceof FileDataObjectBase && toObject instanceof FileDataObjectBase) {
            InputStream stream = ((FileDataObject) fromObject).getContent();
            ((FileDataObjectBase) toObject).setContent(stream);
        }

        // transfer mime type, carefully placing it as mime type or content mime type
        String fromType = getLiteral(fromObject, Vocabulary.MIME_TYPE);
        if (fromType != null) {
            RDFContainer metadata = toObject.getMetadata();
            String toType = getLiteral(toObject, Vocabulary.MIME_TYPE);
            URI predicate = "message/rfc822".equals(toType) ? Vocabulary.CONTENT_MIME_TYPE
                    : Vocabulary.MIME_TYPE;
            metadata.add(new StatementImpl(toObject.getID(), predicate, new LiteralImpl(fromType)));
        }

        // transfer the first object's children to the second object relationships
        ArrayList fromChildren = (ArrayList) messageHierarchy.remove(fromObject);

        if (fromChildren != null && !fromChildren.isEmpty()) {
            ArrayList toChildren = (ArrayList) messageHierarchy.get(toObject);
            if (toChildren == null) {
                toChildren = new ArrayList();
                messageHierarchy.put(toObject, toChildren);
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

    private void encodeHierarchy() {
        // loop over all registered DataObjects
        Iterator objects = messageHierarchy.keySet().iterator();
        while (objects.hasNext()) {
            // fetch a parent DataObject and its children
            DataObject parent = (DataObject) objects.next();
            ArrayList children = (ArrayList) messageHierarchy.get(parent);

            // retrieve some info from the parent
            RDFContainer parentMetadata = parent.getMetadata();
            URI parentID = parent.getID();

            // loop over all children
            int nrChildren = children.size();
            for (int i = 0; i < nrChildren; i++) {
                DataObject child = (DataObject) children.get(i);

                // create the statement that is added to both the parent's and the child's metadata
                Statement statement = new StatementImpl(child.getID(), Vocabulary.PART_OF, parentID);

                // add this statement to both DataObjects
                parentMetadata.add(statement);
                child.getMetadata().add(statement);
            }
        }
    }
}